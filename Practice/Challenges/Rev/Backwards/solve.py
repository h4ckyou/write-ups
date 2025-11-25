import string

a = 81

charset = string.printable
mapping = {}

for i in charset:
    n = ord(i) * 3 + 65 - 32 + a
    r = str(int(str(int(str(int(str(int(str(n)[1:]))))))))
    mapping[r] = i

# print(mapping)

# boundary = [21, 39, 62, 48, 43, 96, 22, 42, 76, 30, 72, 59, 57, 36, 9, 71, 97, 80, 68, 18, 71, 68, 18, 68, 90]
# boundary = [21, 39, 6, 24, 84, 39, 62, 24, 27, 63, 100, 72, 59, 57, 36, 100, 97, 19, 78, 68, 18, 71, 68, 18, 68, 90]
# manual check reveals "_" == 99 but in the enc value given it's represented by "0" which is "100"

boundary = [21, 39, 6, 24, 84, 39, 62, 24, 27, 63, 100, 72, 59, 57, 36, 100, 9, 71, 9, 78, 68, 18, 71, 68, 18, 68, 90]
enc = [i-1 for i in boundary]

for val in enc:
    print(mapping[str(val)], end='')

# flag{l1ght_w0rk_b4by3e43e3}