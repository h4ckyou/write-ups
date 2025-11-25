import string

def big(data):
    x, y = data[:20], data[20:]
    o = ""
    f = ""

    for i in range(len(data)//2):
        o += x[i]+y[i]
    
    for i in o:
        f += chr(ord(i) ^ 0xb)
    
    rev = (f[-1] + f[:-1])[::-1]

    return rev

def bang(data, shift):
    rev = ''

    for char in data:
        if char not in string.ascii_letters:
            rev += char
        else:
            if char in string.ascii_lowercase:
                start = ord('a')
            else:
                start = ord('A')
            
            rev += chr(((ord(char) - start - shift) % 26) + start)
        
    return rev[1::].swapcase() + rev[0]

        
s = "ZYYKXWAT[6RM@T6?ES>69=?Z6GRE}6WEGNK^>Oa6"
print(big(bang(bang(big(bang(bang(big(s),11),6)),13),18)))