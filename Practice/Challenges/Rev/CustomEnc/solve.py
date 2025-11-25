from pwn import ror

enc = "00010000 10101110 01110000 10001111 11110000 01010000 10110000 10001111 01001010 01110001 00101110 11010010 10101110 10001111 00001110 11010100 01110001 10101110".split(" ")

key1 = 109
key2 = 5
flag = ""

for bit in enc:
    inverted = ['1' if i == '0' else '0' for i in bit]
    op1 = (int("".join(inverted), 2) - 42) % 256
    op2 = ror(op1, 5, word_size=8) & 0xff
    flag += chr((op2 ^ key1) & 0xff)

print(flag)