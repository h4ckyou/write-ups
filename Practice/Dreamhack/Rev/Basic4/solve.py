def dec(val):
    return (val << 4 | val >> 4) & 0xff

data = bytes.fromhex("24 27 13 C6 C6 13 16 E6 47 F5 26 96 47 F5 46 27 13 26 26 C6 56 F5 C3 C3 F5 E3 E3 00")
flag = ""

for i in data:
    flag += chr(dec(i))

print(flag)