import string

data = bytes.fromhex("AC F3 0C 25 A3 10 B7 25 16 C6 B7 BC 07 25 02 D5 C6 11 07 C5")
charset = string.printable
flag = ""

for i in range(len(data)):
    for j in charset:
        v1 = (-5 * ord(j)) & 0xff
        if (data[i] == v1):
            flag += j

print(flag)