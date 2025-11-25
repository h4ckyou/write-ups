import ctypes

libc = ctypes.CDLL(None)

srand = libc.srand
rand = libc.rand
srand.argtypes = [ctypes.c_uint]
rand.restype = ctypes.c_int

def rc4_init(seed):
    key = (ctypes.c_ubyte * 256)(*range(256))
    enc = (ctypes.c_ubyte * 256)()

    srand(seed)

    for j in range(256):
        enc[j] = rand() % 256

    idx = 0
    for k in range(256):
        idx = (key[k] + idx + enc[k]) % 256
        key[k], key[idx] = key[idx], key[k]

    return key

def rc4_crypt(key, data):
    i = 0
    j = 0
    out = bytearray()

    for byte in data:
        i = (i + 1) % 256
        j = (j + key[i]) % 256
        key[i], key[j] = key[j], key[i]
        k = key[(key[i] + key[j]) % 256]
        out.append(byte ^ k)

    return bytes(out)

def decrypt_file(ciphertext, seed):

    key = rc4_init(seed)
    plaintext = rc4_crypt(key, ciphertext)

    return plaintext


with open("flag.txt.enc", "rb") as f:
    ciphertext = f.read()


for seed in range(0x68600000, 0x68699999):
    plaintext = decrypt_file(ciphertext, seed)
    if b"Blit" in plaintext:
        print(plaintext)