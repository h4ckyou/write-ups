#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from Crypto.Util.number import isPrime

exe = context.binary = ELF('bomba')
context.log_level = 'info'

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
b *0x4A1742  
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    stage1 = b"For whom the bell tolls. Time marches on."
    stage2 = [5, 6, 8, 11, 15, 20]

    io.sendlineafter(b"know", stage1)

    for i in stage2:
        io.sendline(str(i).encode())

    data = bytes.fromhex("72 00 00 00 00 00 00 00 A0 00 00 00 00 00 00 00 A4 00 00 00 00 00 00 00 AF 00 00 00 00 00 00 00 C4 00 00 00 00 00 00 00 C8 00 00 00 00 00 00 00 C9 00 00 00 00 00 00 00 DC 00 00 00 00 00 00 00 FC 00 00 00 00 00 00 00 0C 01 00 00 00 00 00 00 2E 01 00 00 00 00 00 00 37 01 00 00 00 00 00 00 45 01 00 00 00 00 00 00 69 01 00 00 00 00 00 00 8B 01 00 00 00 00 00 00 8C 01 00 00 00 00 00 00 94 01 00 00 00 00 00 00 A6 01 00 00 00 00 00 00 A9 01 00 00 00 00 00 00 BF 01 00 00 00 00 00 00 D0 01 00 00 00 00 00 00 D8 01 00 00 00 00 00 00 DA 01 00 00 00 00 00 00 E2 01 00 00 00 00 00 00 F2 01 00 00 00 00 00 00 F9 01 00 00 00 00 00 00 20 02 00 00 00 00 00 00 2B 02 00 00 00 00 00 00 37 02 00 00 00 00 00 00 3C 02 00 00 00 00 00 00 47 02 00 00 00 00 00 00 4A 02 00 00 00 00 00 00 4D 02 00 00 00 00 00 00 A2 02 00 00 00 00 00 00 A6 02 00 00 00 00 00 00 BB 02 00 00 00 00 00 00 C6 02 00 00 00 00 00 00 E8 02 00 00 00 00 00 00 10 03 00 00 00 00 00 00 14 03 00 00 00 00 00 00 24 03 00 00 00 00 00 00 26 03 00 00 00 00 00 00 30 03 00 00 00 00 00 00 7D 03 00 00 00 00 00 00 B4 03 00 00 00 00 00 00 C2 03 00 00 00 00 00 00 CF 03 00 00 00 00 00 00 DA 03 00 00 00 00 00 00 E0 03 00 00 00 00 00 00 E4 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00")
    qwords = [struct.unpack("<Q", data[i:i+8])[0] for i in range(0, len(data), 8)]

    for i in qwords:
        if isPrime(i):
            idx = qwords.index(i)

    ptrs = [
        "donkey",
        "elephant",
        "squid",
        "hamster",
        "parrot",
        "horse",
        "emu"
    ]

    io.sendline(str(ptrs.index("hamster")).encode())
    io.sendline(str(idx).encode())

    # use Wolframalpha: https://www.wolframalpha.com/input?i=FibonacciIndex%5B196418%5D
    stage4 = 27
    io.sendline(str(stage4).encode())

    target = b"ratatouillelinguines"
    data = bytes.fromhex("45 00 00 00 00 00 00 00 54 00 00 00 00 00 00 00 44 00 00 00 00 00 00 00 35 00 00 00 00 00 00 00 3E 00 00 00 00 00 00 00 27 00 00 00 00 00 00 00 4D 00 00 00 00 00 00 00 5B 00 00 00 00 00 00 00 5A 00 00 00 00 00 00 00 2A 00 00 00 00 00 00 00 2D 00 00 00 00 00 00 00 26 00 00 00 00 00 00 00 20 00 00 00 00 00 00 00 5F 00 00 00 00 00 00 00 5F 00 00 00 00 00 00 00 46 00 00 00 00 00 00 00 3D 00 00 00 00 00 00 00 3C 00 00 00 00 00 00 00 23 00 00 00 00 00 00 00 4B 00 00 00 00 00 00 00")
    keys = [struct.unpack("<Q", data[i:i+8])[0] for i in range(0, len(data), 8)]
    stage5 = [target[i] ^ keys[i] for i in range(len(target))]

    io.sendline(bytes(stage5))

    io.sendline(b"LRRL")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

