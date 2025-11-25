#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
import re

# Set up pwntools for the correct architecture
exe = context.binary = ELF('prob')
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
breakrva 0x01737
breakrva 0x014F1
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def generate_spell(target):
    spell = []
    v7 = target

    while v7 > 0:
        if v7 % 2 == 0:
            r = v7 // 2
            spell.append("B")
        else:
            r = v7 - 1
            spell.append("A")

        v7 = r
    
    return ''.join(spell[::-1])


def solve():

    for i in range(10):
        io.recvuntil(b"[INFO]")
        output = io.recvline()
        numbers = list(map(int, re.findall(r"\d+", output.decode())))[::-1]
        urandom = [numbers[-1]] + [i for i in numbers][:-1]
        value = "0x"

        for i in urandom:
            value += hex(i)[2:].zfill(2)

        urandom = int(value, 16)
        info("urandom: %#x", urandom)

        io.sendline(generate_spell(urandom).encode())

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

