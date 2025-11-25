node = [76, 17, 78, 75, 19, 68, 63, 76, 17, 83, 84, 63, 17, 78, 63, 77, 19, 77, 16, 82, 89]
nodes = [i - 32 for i in node]

flag = [i + 64 for i in nodes]

print("".join(map(chr, flag)))