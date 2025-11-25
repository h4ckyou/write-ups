check = [0]*35
check[0] = 'i'
check[1] = 'r'
check[2] = 'b'
check[3] = 'u'
check[4] = 'g'
check[5] = 'z'
check[6] = 'v'
check[7] = '1'
check[8] = 'v'
check[9] = '^'
check[10] = 'x'
check[11] = '1'
check[12] = 't'
check[13] = '^'
check[14] = 'j'
check[15] = 'o'
check[16] = '1'
check[17] = 'v'
check[18] = '^'
check[19] = 'e'
check[20] = '5'
check[21] = '^'
check[22] = 'v'
check[23] = '@'
check[24] = '2'
check[25] = '^'
check[26] = '9'
check[27] = 'i'
check[28] = '3'
check[29] = 'c'
check[30] = '@'
check[31] = '1'
check[32] = '3'
check[33] = '8'
check[34] = '|'

correct = ""

for i in check:
    correct += chr(ord(i) ^ 1)

print(correct)


