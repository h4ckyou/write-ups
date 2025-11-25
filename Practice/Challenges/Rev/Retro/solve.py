from Crypto.Cipher import AES
from hashlib import md5
from pwn import xor

"""
pwndbg> x/2gx 0x555555559150
0x555555559150: 0x4030e4cb4228d5e7      0xc43b09011e6b0b63
pwndbg> 

set *(long *)0x555555559150=0x4030e4cb4228d5e7
set *(long *)0x555555559158=0xc43b09011e6b0b63
"""

def keygen(choice):
    hashed = md5(choice.encode()).hexdigest()
    return hashed

aes_key = bytes([0xE7, 0xD5, 0x28, 0x42, 0xCB, 0xE4, 0x30, 0x40, 0x63, 0xB, 0x6B, 0x1E, 0x1, 0x9, 0x3B, 0xC4])
cipher = AES.new(aes_key, AES.MODE_ECB)

with open("enc.txt", "r") as fptr:
    enc = bytes.fromhex(fptr.read())
    fptr.close()

xor_key = [0]*4

xor_key[1] = 2 ^ enc[1]
xor_key[0] = 0x7E ^ enc[8]
xor_key[2] = 0xBD ^ enc[58]
xor_key[3] = 0x3C ^ enc[63]

key = bytes(xor_key).decode()

# with open("rom.txt", "r") as f:
#     rom = bytes.fromhex(f.read())

# decrypted_rom = cipher.decrypt(rom)

# with open("rom.bin", "wb") as fw:
#     fw.write(decrypted_rom)