with open("flag.byc", "rb") as f:
    enc = bytearray(f.read())

data = enc[1:]
data.append(enc[0])

with open("out.byc", "wb") as f:
    f.write(data)