data = bytes.fromhex("74 78 4B 65 77 48 5C 69 68 7E 5C 79 77 62 46 79 77 05 46 54 73 72 59 69 68 7E 5C 7E 5A 61 57 6A 77 66 5A 52 02 62 5C 79 77 5C 00 7C 57 0D 0D 4D")
flag = ""

for i in range(len(data)):
    flag += chr(len(data) ^ data[i])

print(flag)