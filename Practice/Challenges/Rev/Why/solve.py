memo = {}

def why(n):
    if n in memo:
        return memo[n]
    
    if n == 0:
        r = 3
    elif n == 1:
        r = 5
    else:
        r = (why(n - 1) * 2) + (why(n - 2) * 3)

    memo[n] = r
    return r

local_48 = ['A', 'M', -27, -87, -30, '"', -6, -2, 'q', -76, -51, '"', -36, -92, -47, '5', 'm', 'a', -4, -79, -21, 'v', -4, -38, '\\', -38, -4, '\x1a', -36, -102, -4, 'Z', '\\', 'Z', -4, -102, -36, '\x1a', -4, -38, 'k', -79, -4, '"', -77, -79, -4, '|', 'l', 'p', -34]


flag =  ""

for i in range(len(local_48)):
    key = why(i * i) & 0xff
    # print(local_48[i], key)
    if type(local_48[i]) == str:
        flag += chr((ord(local_48[i]) ^ key) & 0xff)
    else:
        flag += chr((local_48[i] ^ key) & 0xff)

print(flag)