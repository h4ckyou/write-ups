import struct
import string

def ror(n, c, bits=32):
    c %= bits
    mask = (1 << bits) - 1
    return ((n >> c) | (n << (bits - c))) & mask

def rol(n, c, bits=32):
    return ror(n, bits - (c % bits), bits)

def find(expected, byte):
    v5 = 4
    v6 = 6
    v2 = byte

    for i in range(5):
        # print(f"i: {hex(i)} \t, v2: {hex(v2)} \t, v5: {hex(v5)} \t, v6: {hex(v6)}")
        v2 ^= rol(v2, v5) ^ ror(v2, v6)
        v5 *= 2
        v6 *= 2
        
    if v2 == expected:
        return True
    
def reverse(value):
    v5 = 4 << 4
    v6 = 6 << 4
    v2 = value

    for i in range(4, -1, -1):
        print(f"i: {hex(i)} \t, v2: {hex(v2)} \t, v5: {hex(v5)} \t, v6: {hex(v6)}")
        v5 //= 2
        v6 //= 2
        v2 ^= rol(v2, v6) ^ ror(v2, v5)
    
    print(hex(v2))


data = "30 03 0C F3 9D DE 0D 34 C9 9A 0D 75 2A BC 1F 39 5B AF 16 9F 61 06 18 E6 6B AC 1A 6C 9D DE 0D 34 35 56 0D B6 5B AF 16 9F 64 53 19 A3 3A BD 1B 68 30 03 0C F3 64 53 19 A3 C6 71 1B AB 30 03 0C F3 74 52 1D F2 5B AF 16 9F 61 06 18 E6 CC CF 0C 30 74 52 1D F2 5B AF 16 9F C6 71 1B AB 64 53 19 A3 C9 9A 0D 75 64 53 19 A3 5B AF 16 9F 74 52 1D F2 30 03 0C F3 64 53 19 A3 74 52 1D F2 D9 8F 1C 35 98 8B 0C 71 61 12 0D F7 3F E8 1A 2D 30 03 0C F3 C3 24 1A EE 61 12 0D F7 DC CE 08 61 DC CE 08 61"
buffer = bytes.fromhex(data)
expected_buf = []

for i in range(0, len(buffer), 4):
    dword = struct.unpack("<I", buffer[i:i+4])[0]
    expected_buf.append(dword)

flag = ""

for val in expected_buf:
    for j in string.printable:
        byte = ord(j)
        result = find(val, byte)
        if result:
            print(j, val)

print(flag)