#!/usr/bin/env python3
import os, re, json, subprocess, tempfile, traceback, sys
import xml.etree.ElementTree as ET
from flask import Flask, request, jsonify, Response

class FilteredIO:
    def write(self, msg):
        if msg.startswith('[DEBUG]') or msg.startswith('[ERROR]') or msg.startswith('[WARNING]'):
            sys.__stdout__.write(msg)
    def flush(self):
        sys.__stdout__.flush()

sys.stdout = sys.stderr = FilteredIO()
app = Flask(__name__)
ALL_REGS = ['rax','rbx','rcx','rdx','rsi','rdi','rbp','rsp','r8','r9','r10','r11','r12','r13','r14','r15']

def debug(msg, *args):
    print(f"[DEBUG] {msg % args}\n")

def strip_ns(elem):
    if '}' in elem.tag:
        elem.tag = elem.tag.split('}', 1)[1]
    for child in list(elem):
        strip_ns(child)

def load_instr():
    debug("Loading instructions.json")
    with open('instructions.json') as f:
        data = json.load(f)
    colormap = {blk_type: cat['colour']
                for cat in data['categories']
                for blk_type in cat['blocks']}
    for blk in data['blocks']:
        colour = colormap.get(blk['type'])
        if colour:
            blk['colour'] = colour
            blk.setdefault('json', {})['colour'] = colour
    debug("Loaded %d categories, %d blocks", len(data['categories']), len(data['blocks']))
    return data

INSTR = load_instr()
TEMPLATES = {b['type']: b for b in INSTR['blocks']}

def emit_asm(root):
    debug("emit_asm: starting")
    lines, errs, seen = [], [], set()
    label_id = 1
    def walk(block):
        nonlocal label_id
        bid = block.get('id')
        if not bid or bid in seen:
            return
        seen.add(bid)
        t = block.get('type')
        tpl = TEMPLATES.get(t)
        if not tpl:
            errmsg = f"Unknown block type: {t}"
            print(f"[ERROR] {errmsg}\n")
            errs.append(errmsg)
        else:
            vals = {f.get('name'): (f.text or '') for f in block.findall('field')}
            if t == 'label':
                n = vals.get('N','').strip()
                if not n or n.lower() == 'main':
                    vals['N'] = f'label{label_id}'
                    label_id += 1
            try:
                lines.append(tpl['template'].format(**vals))
            except Exception:
                traceback.print_exc()
                errs.append(f"{t} template error")
        nxt = block.find('next')
        if nxt is not None:
            nb = nxt.find('block')
            if nb is not None:
                walk(nb)
    for blk in root.findall('block'):
        walk(blk)
    debug("emit_asm: done, %d lines, %d errors", len(lines), len(errs))
    return lines, errs

def wrap_for_as(code):
    body = [l.strip() for l in code.splitlines() if l.strip() and not l.strip().startswith(';')]
    need_exit = not any(re.search(r'(mov (?:al|rax),?60|syscall)', l) for l in body)
    parts = ['.intel_syntax noprefix', '.global _start', '_start:']
    for l in body:
        if l.endswith(':'):
            parts.append(l)
        else:
            parts.append('\t' + l)
    if need_exit:
        parts += ['\txor edi,edi', '\tmov al,60', '\tsyscall']
    return '\n'.join(parts) + '\n'

def scrape_regs(asm):
    vals = {r: '0x0' for r in ALL_REGS}
    used = set()
    for reg, val in re.findall(r'\bmov\s+([er][a-z0-9]+l?)\s*,\s*([0-9a-fx]+)', asm, flags=re.I):
        rname = reg.lower()
        if rname.startswith('e'):
            rname = 'r' + rname[1:]
        elif rname in ('al','bl','cl','dl'):
            rname = 'r' + rname[0] + 'x'
        if rname in vals:
            try:
                vals[rname] = hex(int(val,0))
                used.add(rname)
            except ValueError:
                print(f"[WARNING] Invalid literal: {val}\n")
    debug("scrape_regs: used registers = %s", used)
    return vals, sorted(used)

def run_asm(body):
    debug("run_asm: body=\n%s", body)
    res = dict(success=False, asm=body, errors=[], output='', hex_bytes='', full_asm='')
    with tempfile.TemporaryDirectory() as td:
        asm_path = os.path.join(td,'a.s')
        obj_path = os.path.join(td,'a.o')
        bin_path = os.path.join(td,'a.out')
        full = wrap_for_as(body)
        res['full_asm'] = full
        debug("Wrapped asm=\n%s", full)
        open(asm_path,'w').write(full)
        for cmd in (['as','--64',asm_path,'-o',obj_path],['ld',obj_path,'-o',bin_path]):
            debug("Running: %s", cmd)
            p = subprocess.run(cmd,capture_output=True,text=True)
            if p.returncode:
                err = p.stderr.strip()
                print(f"[ERROR] {err}\n")
                res['errors'].append(err)
                res['output'] = err
                return res
        try:
            subprocess.run(['objcopy','-O','binary','--only-section=.text',obj_path,obj_path+'.bin'],capture_output=True,text=True)
            blob = open(obj_path+'.bin','rb').read()
            res['hex_bytes'] = ' '.join(f'{b:02x}' for b in blob)
        except Exception:
            traceback.print_exc()
        try:
            p = subprocess.run([bin_path],capture_output=True,text=True,timeout=5)
            res['output'] = p.stdout or p.stderr or f'[exit {p.returncode}]'
        except subprocess.TimeoutExpired:
            print("[WARNING] Execution timeout\n")
            res['output'] = 'Execution timeout'
        res['success'] = True
    return res

@app.route('/')
def index(): return Response(INDEX_HTML,mimetype='text/html')

@app.route('/instructions')
def instructions(): return jsonify(INSTR)

@app.route('/process',methods=['POST'])
def process():
    try:
        data=request.data.decode(); debug("Incoming XML: %s", data)
        root=ET.fromstring(data); strip_ns(root)
        lines,errs=emit_asm(root); asm='\n'.join(lines)
        regs,used=scrape_regs(asm)
        if errs and not lines: return jsonify({'asm':asm,'errors':errs,'output':'Block errors','registers':used,'register_values':regs,'instruction_count':0,'success':False,'hex_bytes':'','full_asm':''}),400
        out=run_asm(asm)
        return jsonify({'asm':asm,'full_asm':out['full_asm'],'errors':out['errors']+errs,'output':out['output'],'registers':used,'register_values':regs,'instruction_count':len(lines),'success':out['success'],'hex_bytes':out['hex_bytes']})
    except Exception:
        traceback.print_exc(); return jsonify({'asm':'','full_asm':'','errors':[],'output':'Server error','registers':[],'register_values':{},'instruction_count':0,'success':False,'hex_bytes':''}),500

INDEX_HTML = open(os.path.join(os.path.dirname(__file__),'index.html'),encoding='utf-8').read()

if __name__=='__main__':
    app.run(debug=False,use_reloader=False,port=5000,host='0.0.0.0')
