import ctypes
import time

global n

n = 64

def stage1():
    libc = ctypes.CDLL("libc.so.6")
    libc.srand(0x1337)
    enc = bytes.fromhex("40 1B 16 ED 89 54 EB 5E EB 61 F4 8B D0 98 A1 D2 02 B2 9E 87 8D 2D BE 7B E6 F3 A9 B2 5F 20 1B 51 C2 EA 85 0C 8E 2D 43 03 89 EA F2 4B C6 03 11 31 51 32 19 29 40 E2 13 CE 36 14 13 21 7F 83 00 6E")
    res = bytearray(n)

    for i in range(n):
        v0 = (libc.rand() - i) ^ enc[i]
        arr = (v0 - i) & 0xff
        res[i] = arr

    crc = res.decode()
    print(f"crc stage1: {crc}")


def stage2():
    libc = ctypes.CDLL("libc.so.6")
    rand_table = [0]*n

    for i in range(n):
        rand_table[i] = libc.rand()
    
    seed = int(time.time())
    libc.srand(seed)

    enc = bytes.fromhex("19 2C 4E 24 32 35 7F 23 5E 31 6F 62 56 5E 16 78 2F 26 07 5A 50 21 48 43 2D 0A 61 19 17 04 2B 53 17 6A 73 1D 67 0B 05 3C 0A 16 69 4B 09 4A 0F 0C 5D 6B 30 29 7E 18 0A 48 44 36 58 74 5A 1E 11 37")

    print(rand_table)
    for i in range(n):
        rnd1 = libc.rand()
        rnd2 = libc.rand()





def main():
    # stage1()
    stage2()


if __name__ == '__main__':
    main()