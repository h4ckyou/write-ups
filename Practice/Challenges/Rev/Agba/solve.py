check = [13, 119, 8, 127, 29, 98, 108, 45, 87, 86, 37, 88, 70, 71, 53, 60, 241, 180, 191, 184, 162, 193, 170, 168, 250]
password = ""

for i in range(len(check)):
    password += chr(check[i] ^ 4 * i + 67)

print(password)