import struct

"""
fw = 24934
sw = 25963

key1 = 30035
key2 = 13136

v5 = key1 ^ (sw + ((fw << 7) | (fw >> 9)))
fw = key2 ^ ((sw << 7) | (sw >> 9))
sw = v5

fw = 34530
sw = 28104

fw = key2 ^ ((sw << 7) | (sw >> 9))
key2 ^ fw = (sw << 7) | (sw >> 9)
x = key2 ^ fw
sw_prev = ((x >> 7) | (x << 9)) & 0xffff


v5 = key1 ^ (sw_prev + ((fw << 7) | (fw >> 9)))
v5 ^ key1 = sw_prev + ((fw << 7) | (fw >> 9))
(v5 ^ key1) - sw_prev = ((fw << 7) | (fw >> 9))
y = ((v5 ^ key1) - sw_prev) & 0xffff


fw_prev = ((y >> 7) | (y << 9))
"""

def encrypt(buf):
    key = "SuP3RSaFeK3Y".encode()
    enc = b""

    hiword = int.from_bytes(buf[:2], 'little')
    loword = int.from_bytes(buf[2:], 'little')

    for counter in range(3):
        key1 = key[(4 * counter):(4 * counter + 2)]
        key2 = key[(4 * counter + 2):(4 * counter + 4)]

        v5 = int.from_bytes(key1, 'little') ^ (loword + (((hiword << 7) | (hiword >> 9)) & 0xffff) & 0xffff)
        hiword = int.from_bytes(key2, 'little') ^ (((loword << 7) | (loword >> 9)) & 0xffff)
        loword = v5


    enc += struct.pack('<H', hiword)
    enc += struct.pack('<H', loword)

    return enc



def decrypt(buf):
    key = "SuP3RSaFeK3Y".encode()
    dec = b""

    hiword = int.from_bytes(buf[:2], 'little')
    loword = int.from_bytes(buf[2:], 'little')

    for counter in reversed(range(3)):
        key1 = key[(4 * counter):(4 * counter + 2)]
        key2 = key[(4 * counter + 2):(4 * counter + 4)]

        x = int.from_bytes(key2, 'little') ^ hiword
        sw_prev = ((x >> 7) | (x << 9)) & 0xffff

        y = ((loword ^ int.from_bytes(key1, 'little')) - sw_prev) & 0xffff
        fw_prev = ((y >> 7) | (y << 9)) & 0xffff

        hiword = fw_prev
        loword = sw_prev
    
    dec += struct.pack('<H', hiword)
    dec += struct.pack('<H', loword)

    return dec


def wr_encrypt():

    with open("a.txt", "rb") as f:
        buf = f.read()

    size = len(buf)

    if size % 4 != 0:
        remainder = size % 4
        buf += b"\x00"*remainder


    n = len(buf)
    encrypted = b""

    for i in range(0, n, 4):
        encrypted += encrypt(buf[i:i+4])


    with open("a.out", "wb") as f:
        f.write(encrypted)
        f.close()


def wr_decrypt():

    filename = "flag.enc"
    with open(filename, "rb") as fp:
        enc = fp.read()

    n = len(enc)
    decrypted = b""

    for i in range(0, n, 4):
        block = enc[i:i+4]
        decrypted += decrypt(block)

    print(decrypted)



def main():
    # wr_encrypt()
    wr_decrypt()



if __name__ == '__main__':
    main()