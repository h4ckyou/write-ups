#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from collections import deque

exe = context.binary = ELF('stripmine', checksec=False)
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
brva 0xEE1
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def generatePath(path):
    result = bytearray()

    for pos in path:
        lower_bit = pos
        upper_bit = 0
        upper_nibble = (upper_bit << 3) | lower_bit

        lower_bit = 0
        upper_bit = 1
        lower_nibble = (upper_bit << 3) | lower_bit

        byte = (upper_nibble << 4) | lower_nibble
        result.append(byte)

    return bytes(result)


def solve():

    points_x = bytes.fromhex("00 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF")
    points_y = bytes.fromhex("FF FF FF FF FF FF FF FF 00 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 FF FF FF FF")

    x_axis = [struct.unpack("<i", points_x[i:i+4])[0] for i in range(0, len(points_x), 4)]
    y_axis = [struct.unpack("<i", points_y[i:i+4])[0] for i in range(0, len(points_y), 4)]

    grid = [
        [0,0,2,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0],
        [0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0],
        [0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0],
        [0,0,0,0,1,1,2,0,0,0,0,0,0,1,1,1,0,0,0,0],
        [0,0,0,0,0,0,0,2,0,0,0,0,2,1,0,0,0,0,0,0],
        [2,0,0,0,0,0,0,2,0,0,0,0,0,1,1,1,0,0,0,0],
        [0,0,0,0,1,1,1,1,1,2,0,0,0,1,0,0,0,0,0,0],
        [0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
        [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0],
        [0,0,1,1,1,1,2,1,0,0,0,0,1,1,1,1,1,0,0,0],
        [0,0,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0],
        [0,0,1,0,0,0,0,2,0,1,1,0,0,0,0,0,0,0,2,0],
        [0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,0,0,0],
        [0,0,0,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0],
        [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0],
        [0,0,0,0,1,1,1,1,0,0,0,1,1,1,0,0,0,0,0,0],
        [0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,2],
        [0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0],
        [0,0,0,0,0,0,0,0,0,0,2,0,1,1,1,1,1,0,0,0],
        [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
    ]

    rows, cols = len(grid), len(grid[0])

    def bfs(start_x, start_y):
        q = deque()
        q.append((start_x, start_y, []))
        visited = set()
        visited.add((start_x, start_y))

        while q:
            x, y, path = q.popleft()

            for pos in range(8):
                nx, ny = x + x_axis[pos], y + y_axis[pos]
                if 0 <= nx < cols and 0 <= ny < rows and (nx, ny) not in visited:
                    if grid[ny][nx] == 1:
                        continue
                    new_path = path + [pos]
                    if grid[ny][nx] == 2: 
                        grid[ny][nx] = 1
                        return nx, ny, new_path
                    q.append((nx, ny, new_path))
                    visited.add((nx, ny))

        return None  


    start_x, start_y = 0, 0
    win_path = b""

    while True:
        result = bfs(start_x, start_y)
        if not result:
            break
        start_x, start_y, path = result
        print(f"Reached food at ({start_x}, {start_y}) with moves {path}")
        win_path += generatePath(path)
    
    io.send(win_path)

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

