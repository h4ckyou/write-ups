from z3 import *

matrix = [
    [1, 2, 3, 4, 5],
    [6, 7, 8, 9, 10],
    [11, 12, 13, 14, 15],
    [16, 17, 18, 19, 20],
    [21, 22, 23, 24, 25]
]

r = [BitVec(f'f_{i}', 8) for i in range(25)]
tmp = [BitVec(f'f_{i}', 8) for i in range(25)]
auth = [0xee, 0xb0, 0xb0, 0x7b, 0x89, 0xb0, 0xce, 0x8b][::-1] + [0x94, 0x99, 0x99, 0x9a, 0x9d, 0x65, 0x92, 0xbf][::-1] + [0xe4, 0xad][::-1]


for i in range(25):
    m = (i * 2) % 25
    f = (i * 7) % 25
    # print([m, f])
    matrix[m//5][m%5] = r[f]

s = Solver()

for i in range(len(r)):
    s.add(r[i] > 0x20)
    s.add(r[i] < 0x7f)


s.add(auth[0] == matrix[0][0] + matrix[4][4])
s.add(auth[1] == matrix[2][1] + matrix[0][2])
s.add(auth[2] == matrix[4][2] + matrix[4][1])
s.add(auth[3] == matrix[1][3] + matrix[3][1])
s.add(auth[4] == matrix[3][4] + matrix[1][2])
s.add(auth[5] == matrix[1][0] + matrix[2][3])
s.add(auth[6] == matrix[2][4] + matrix[2][0])
s.add(auth[7] == matrix[3][3] + matrix[3][2] + matrix[0][3])
s.add(auth[8] == matrix[0][4] + matrix[4][0] + matrix[0][1])
s.add(auth[9] == matrix[3][3] + matrix[2][0])
s.add(auth[10] == matrix[4][0] + matrix[1][2])
s.add(auth[11] == matrix[0][4] + matrix[4][1])
s.add(auth[12] == matrix[0][3] + matrix[0][2])
s.add(auth[13] == matrix[3][0] + matrix[2][0])
s.add(auth[14] == matrix[1][4] + matrix[1][2])
s.add(auth[15] == matrix[4][3] + matrix[2][3])
s.add(auth[16] == matrix[2][2] + matrix[0][2])
s.add(auth[17] == matrix[1][1] + matrix[4][1])


if s.check() == sat:
    m = s.model()
    r = ""

    for i in range(25):
        r += chr(m[tmp[i]].as_long())

    print(r)

# by9)7c`UM)TwD>&ExXGU!mkE\ --> a right value!