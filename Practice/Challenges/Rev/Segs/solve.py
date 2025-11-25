key = int.from_bytes(b"DoHC", "little")
data = [ 85133584, 958138136, 956302367, 302714898, 1953237054 ]

flag = b""

for i in data:
    j = key.to_bytes(4, 'little')
    print(j.decode(), end="")
    key ^= i

