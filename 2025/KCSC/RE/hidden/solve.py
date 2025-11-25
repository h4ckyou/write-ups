import struct
from pwn import xor

array = [0xFDE7F1F3CBDBCBC3, 0xFBD7FCAFE6E9EBD7, 0xF5FEB2EDE5D7EDED, 0xE5D7EDED]
buffer = b""

for i in array:
    buffer += struct.pack(b"<Q", i)

print(xor(buffer, 0x88))