from struct import pack

mem = bytearray(0x100)
array = [ 
    0x90737F6B00000000, 0x0DED643A17B1BA3EC, 0x0BFF81381E56C90B, 
    0x88CF93C68A7DBE, 0x2154434552524F43, 0x7369206572654820, 
    0x6C662072756F7920, 0x4F434E490A3A6761, 0x454B215443455252,
    0x67616C662F203A59
]

offset = 112

for value in array:
    mem[offset:offset + 8] = pack("<Q", value) 
    offset += 8 


expected = []
counter = 0x15
idx = 0x8e

while counter != 0:
    value = mem[idx]
    expected.append(value)
    idx -= 1
    counter -= 1

expected.reverse()
key = [0x2a, 0xed, 0xae, 0x21, 0x4f, 0x5a, 0xec, 0xf5, 0x5f, 0x49, 0xfa, 0x18, 0xd5, 0xcf, 0x86, 0x6b, 0x56, 0xb9, 0x8e, 0xd8, 0x52]
data = []

for i, j in zip(expected, key):
    value = (i - j) & 0xff
    data.append(value)

res = bytes(data)

with open("ans", "wb") as f:
    f.write(res)