import struct

def rotate(v):
    return ((v << 1) | (v >> 7)) & 0xff

v9 = [0] * 7
v9[0] = 0x9eb369a64bed83de
v9[1] = 0x056ae1e057dd089d
v9[2] = 0xe020f632dc2607e0
v9[3] = 0xfd58d1b45d75efaf
v9[4] = 0xfcc6ff6f88e77a65
v9[5] = 0xeff132dbb6f6b1c8
v9[6] = 0x0000230985856675

enc = b""
for i in v9:
    d = struct.pack("<Q", i)
    enc += d

key = 0xf3
flag = []

for i in range(len(enc)):
    s1 = rotate(enc[i])
    s2 = s1 ^ (3 * i + key)
    s2 &= 0xff
    key = (s2 + key) ^ (13 * i)
    flag.append(s2)

print(bytes(flag))
 