memo = {}

def gen(n):
    if n in memo:
        return memo[n]
    
    if n <= 1:
        r = n
    else:
        r = gen(n - 1) + gen(n - 2)

    memo[n] = r
    return r


array = [115 ,104 ,100 ,120 ,98 ,89 ,233 ,2 ,189 ,113 ,15 ,64 ,78 ,80 ,16 ,75 ,108 ,184 ,217 ,90 ,148 ,78 ,184 ,45 ,57 ,62 ,144 ,36 ,154 ,224 ,143 ,239 ,137 ,57 ,121 ,235 ,85 ,242 ,106 ,14 ,208 ,36 ,192 ,244 ]
flag = [0]*len(array)
n = 0

for i in range(len(array)):
    key = gen(n * 2) & 0xff
    flag[i] = array[i] ^ key
    n += 1

print(bytes(flag))


