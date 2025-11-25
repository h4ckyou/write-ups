#!/usr/bin/env python3

from pwn import *

exe = ELF('./game', checksec=False)

context.binary = exe
context.log_level = 'info'

NUM_COLUMNS = 100
NUM_ROWS = 100
ARRAY_SIZE = NUM_COLUMNS * NUM_ROWS

FLOORS = {
    1: 0x51B840,
    2: 0x51DF60,
    3: 0x520680,
    4: 0x522DA0,
    5: 0x5254C0,
    6: 0x527BE0,
    7: 0x52A300,
    8: 0x52CA20,
    9: 0x52F140,
    10: 0x531860
}


def generate_ascii_art():
    art = []
    for floor, addr in FLOORS.items():
        floor_art = exe.read(addr, ARRAY_SIZE)
        art += [
            floor_art[i:i + NUM_COLUMNS]
            for i in range(0, len(floor_art), NUM_COLUMNS)
        ]

    return art


def main():

    art = generate_ascii_art()

    with open('flag_art.txt', 'wb') as f:
        for line in art:
            f.write(line + b'\n')


if __name__ == '__main__':
    main()
