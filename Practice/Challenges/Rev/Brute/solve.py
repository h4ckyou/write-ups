import gdb
import string

base = 0x56555000

gdb.execute("aslr off")
gdb.execute(f"b *{base+0x098e}")

charset = string.ascii_letters + string.digits + '_{}'
flag = "picoCTF{I_5D3_A11DA7"

while flag[-1] != '}':
    for char in charset:
        guess = (flag + char).encode()
        guess = guess.ljust(0x1e, b'.')
        print(f'Brute forcing: {guess}')

        with open("cracking", "wb") as f:
            f.write(guess)
        
        gdb.execute("r < cracking")

        for _ in range(len(flag)):
            gdb.execute("c")
        
        dl = int(gdb.parse_and_eval("$dl"))
        al = int(gdb.parse_and_eval("$al"))
        
        if dl == al:
            flag += char
            break
    
print(f'Flag Found: {flag}')