from z3 import *

def doNot(n):
    notted = ''
    for j in n:
        if j == '0':
            notted += '1'
        else:
            notted += '0'

    int_form = int(notted, 2)
    
    return int_form

def reverse(array):
    return array[::-1]

def operationNot(array):
    void = []

    for i in array:
        binzz = bin(i)[2:]

        if len(binzz) < 8:
            diff = 8 - len(binzz)
            bin_form = "0"*diff + binzz
            void.append(doNot(bin_form))
        else:
            void.append(doNot(binzz))
        
    return void

alt = [0x41, 0x29, 0xd9, 0x65, 0xa1, 0xf1, 0xe1, 0xc9, 0x19, 0x09, 0x93, 0x13, 0xa1, 0x09, 0xb9, 0x49, 0xb9, 0x89, 0xdd, 0x61, 0x31, 0x69, 0xa1, 0xf1, 0x71, 0x21, 0x9d, 0xd5, 0x3d, 0x15, 0xd5]
reversed = reverse(alt)
check = operationNot(reversed)

s = Solver()

flag = []

for i in range(len(check)):
    b = BitVec("%d" % i, 16)
    flag.append(b)

for i in range(len(check)):
    x = (flag[i] & 0x55) * 2 | (flag[i] >> 1) & 0x55
    y = (x & 0x33) * 4 | (x >> 2) & 0x33
    z = (y * 16) | y >> 4
    z = z & 0xff

    s.add(z == check[i])

if s.check() == sat:
    m = s.model()
    win = ''

    for i in range(len(flag)):
        win += chr(m[flag[i]].as_long())

    print(f"Flag: {win}")