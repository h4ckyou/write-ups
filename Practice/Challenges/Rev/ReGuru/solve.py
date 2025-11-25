from hashlib import sha1
import string
from itertools import product

charset = string.printable

def caesarCipher(a1, a2):
    if (a1 > 64 and a1 <= 90):
        return ((a1 - 65 + a2) % 26 + 65)

    if (a1 <= 96 or a1 > 122):
        return a1
    
    return ((a1 - 97 + a2) % 26 + 97)


def two():
    value = "50c9e8d5fc98727b4bbc93cf5d64a68db647f04f"
    key = 2
    for byte in charset:
        hashed = sha1(bytes([ord(byte) ^ key])).hexdigest()
        if hashed == value:
            return ord(byte)


def three():
    value = "534f33c201a45017b502e90a800f1b708ebcb300"
    key = 3
    for byte in charset:
        hashed = sha1(bytes([ord(byte) ^ key])).hexdigest()[2:] + "00"
        if hashed == value:
            return ord(byte)

def four_five():
    value = "c8261cec9ddc2177740dac736b2d3d78bb897f00"
    key = 4

    for byte in product(charset, repeat=2):
        first_byte = caesarCipher(ord(byte[0]), key)
        second_byte = caesarCipher(ord(byte[1]), key + 1)
        caesar = bytes([first_byte]) + bytes([second_byte])
        hashed = sha1(caesar).hexdigest()[2:] + "00"
        if hashed == value:
            return ord(byte[0]), ord(byte[1])

def six():
    value = "a0acfad59379b3e050338bf9f23cfc172ee78700"
    for byte in charset:
        hashed = sha1(byte.encode()).hexdigest()[2:] + "00"
        if hashed == value:
            return ord(byte)

def s89():
    value = "6d3f39529429a256ea6bb6563a8af3a61ca7da00"
    key = 7

    for byte in product(charset, repeat=3):
        first_byte = caesarCipher(ord(byte[0]), key)
        second_byte = caesarCipher(ord(byte[1]), key + 1)
        third_byte = caesarCipher(ord(byte[2]), key + 2)
        caesar = bytes([first_byte, second_byte, third_byte])
        hashed = sha1(caesar).hexdigest()[2:] + "00"
        if hashed == value:
            return ord(byte[0]), ord(byte[1]), ord(byte[2])
        

def ten():
    value = "a0acfad59379b3e050338bf9f23cfc172ee78700"
    for byte in charset:
        hashed = sha1(byte.encode()).hexdigest()[2:] + "00"
        if hashed == value:
            return ord(byte)
        


def eleven_234():
    value = "2151fd95207766c14aff2644749c2f8597350b00"
    key = 11

    for byte in product(charset, repeat=4):
        first_byte = caesarCipher(ord(byte[0]), key)
        second_byte = caesarCipher(ord(byte[1]), key + 1)
        third_byte = caesarCipher(ord(byte[2]), key + 2)
        fourth_byte = caesarCipher(ord(byte[3]), key + 3)
        caesar = bytes([first_byte, second_byte, third_byte, fourth_byte])
        hashed = sha1(caesar).hexdigest()[2:] + "00"
        if hashed == value:
            return ord(byte[0]), ord(byte[1]), ord(byte[2]), ord(byte[3])


def fifteen():
    value = "534f33c201a45017b502e90a800f1b708ebcb300"
    key = 3

    for byte in charset:
        hashed = sha1(bytes([ord(byte) ^ key])).hexdigest()[2:] + "00"
        if hashed == value:
            return ord(byte)


def sixteen789():
    value = "a439a385842a9a99ca5bc7f07ed48acd587f1900"
    key = 0x17

    for byte in product(charset, repeat=4):
        first_byte = ord(byte[0]) ^ key
        second_byte = ord(byte[1]) ^ key
        third_byte = ord(byte[2]) ^ key
        fourth_byte = ord(byte[3]) ^ key
        final = bytes([first_byte, second_byte, third_byte, fourth_byte])
        hashed = sha1(final).hexdigest()[2:] + "00"
        if hashed == value:
            return ord(byte[0]), ord(byte[1]), ord(byte[2]), ord(byte[3]) 


flag = [0x2e] * 20

flag[0] = 72
flag[1] = 84
flag[2] = two()
flag[3] = three()
flag[4], flag[5] = four_five()
flag[6] = six()
flag[7], flag[8], flag[9] = s89()
flag[10] = ten()
flag[11], flag[12], flag[13], flag[14] = (76, 33, 102, 101) #eleven_234() return value from calling this function -> eleven_234()
flag[15] = fifteen()
flag[16], flag[17], flag[18], flag[19] = sixteen789()

print(bytes(flag))

