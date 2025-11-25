"""
The check algorithm from the dummped seccomp BPF rule is this:

- When first called it initializes a variable X which holds 0
- It sets another variable A which holds the value of our input at the current index
- It subtracts the variable A by X then the result is compared against a value and then the result is stored in X and the loop continues
"""

mem_check = [
    [72, 12, 54, 69, 46, 101],
    [101, 4294967294, 101, 4294967243, 162],
    [112, 4294967229, 164, 4294967232, 116],
    [98, 16, 36, 4294967293, 128]
]

flag = ""

for i in mem_check:
    X = 0
    for j in i:
        A = (j + X) & 0xff
        X = j
        flag += chr(A)

print(flag)