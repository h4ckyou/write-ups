from pwn import remote

HOST, PORT = "host1.dreamhack.games", 15387

class RNGBreaker:
    def __init__(self, seed):
        self.seed = seed
        self.rand_array = [0] * 8
        self.pos = 8

    def update_seed(self):
        self.rand_array[0] = self.seed
        for i in range(1, 8):
            self.rand_array[i] = (0x6C07 * (self.rand_array[i - 1] ^ (self.rand_array[i - 1] >> 14)) + i) & 0xFFFF        

    def update_rng_state(self):
        for i in range(8):
            v1 = (self.rand_array[i] & 0x8000 | self.rand_array[(i + 1) % 8] & 0x7FFF) >> 1
            if ((self.rand_array[(i + 1) % 8] & 1) != 0):
                v1 ^= 0x9908
            self.rand_array[i] = v1 ^ self.rand_array[(i + 4) % 8]

        self.pos = 0

    def get_rand_idx(self):
        if (self.pos > 7):
            self.update_rng_state()
        
        v0 = self.pos
        v7 = self.rand_array[v0]
        v6 = v7 >> 12
        v1 = 5 * ((v7 >> 4) & 0xF) + 3 * (v7 & 0xF) + 7 * (HIBYTE(v7) & 0xF) + 2 * v6
        v2 = 6 * (HIBYTE(v7) & 0xF) + 7 * ((v7 >> 4) & 0xF) + 4 * (v7 & 0xF) + 3 * v6
        v3 = (3 * ((v7 >> 4) & 0xF) + 2 * (v7 & 0xF) + 5 * (HIBYTE(v7) & 0xF) + 4 * v6) >> 31
        v4 = 5 * (v7 & 0xF) + 6 * ((v7 >> 4) & 0xF) + 4 * (HIBYTE(v7) & 0xF)
        self.pos += 1
        return (((((v3 >> 28) + 3 * ((v7 >> 4) & 0xF) + 2 * (v7 & 0xF) + 5 * (HIBYTE(v7) & 0xF) + 4 * v6) & 0xF) - (v3 >> 28)) << 8) | (16 * ((((HIDWORD(v2) >> 28) + 6 * (HIBYTE(v7) & 0xF) + 7 * ((v7 >> 4) & 0xF) + 4 * (v7 & 0xF) + 3 * v6) & 0xF) - (HIDWORD(v2) >> 28))) | ((((HIDWORD(v1) >> 28) + 5 * ((v7 >> 4) & 0xF) + 3 * (v7 & 0xF) + 7 * (HIBYTE(v7) & 0xF) + 2 * v6) & 0xF) - (HIDWORD(v1) >> 28)) | (((((((v4 + 7 * v6) >> 31) >> 28) + 5 * (v7 & 0xF) + 6 * ((v7 >> 4) & 0xF) + 4 * (HIBYTE(v7) & 0xF) + 7 * v6) & 0xF) - (((v4 + 7 * v6) >> 31) >> 28)) << 12)

    def next(self):
        return self.get_rand_idx()


dictionary = []

with open("dictionary.txt", "r") as f:
    for line in f.readlines():
        value = line.strip()
        dictionary.append(value)
    
def HIBYTE(v1):
    return (v1 >> 8) & 0xFF

def HIDWORD(v2):
    return (v2 >> 32) & 0xFFFFFFFF

def update_rng(rand_array):
    for i in range(8):
        v1 = (rand_array[i] & 0x8000 | rand_array[(i + 1) % 8] & 0x7FFF) >> 1
        if ((rand_array[(i + 1) % 8] & 1) != 0):
            v1 ^= 0x9908
        rand_array[i] = v1 ^ rand_array[(i + 4) % 8]
    
    return rand_array

def find_rand_idx(rand_array):
    v0 = 0
    v7 = rand_array[v0]
    v6 = v7 >> 12
    v1 = 5 * ((v7 >> 4) & 0xF) + 3 * (v7 & 0xF) + 7 * (HIBYTE(v7) & 0xF) + 2 * v6
    v2 = 6 * (HIBYTE(v7) & 0xF) + 7 * ((v7 >> 4) & 0xF) + 4 * (v7 & 0xF) + 3 * v6
    v3 = (3 * ((v7 >> 4) & 0xF) + 2 * (v7 & 0xF) + 5 * (HIBYTE(v7) & 0xF) + 4 * v6) >> 31
    v4 = 5 * (v7 & 0xF) + 6 * ((v7 >> 4) & 0xF) + 4 * (HIBYTE(v7) & 0xF);
    return (((((v3 >> 28) + 3 * ((v7 >> 4) & 0xF) + 2 * (v7 & 0xF) + 5 * (HIBYTE(v7) & 0xF) + 4 * v6) & 0xF) - (v3 >> 28)) << 8) | (16 * ((((HIDWORD(v2) >> 28) + 6 * (HIBYTE(v7) & 0xF) + 7 * ((v7 >> 4) & 0xF) + 4 * (v7 & 0xF) + 3 * v6) & 0xF) - (HIDWORD(v2) >> 28))) | ((((HIDWORD(v1) >> 28) + 5 * ((v7 >> 4) & 0xF) + 3 * (v7 & 0xF) + 7 * (HIBYTE(v7) & 0xF) + 2 * v6) & 0xF) - (HIDWORD(v1) >> 28)) | (((((((v4 + 7 * v6) >> 31) >> 28) + 5 * (v7 & 0xF) + 6 * ((v7 >> 4) & 0xF) + 4 * (HIBYTE(v7) & 0xF) + 7 * v6) & 0xF) - (((v4 + 7 * v6) >> 31) >> 28)) << 12)

def generate_sequence(seed):
    rand_array = [0] * 8
    rand_array[0] = seed
    for i in range(1, 8):
        rand_array[i] = (0x6C07 * (rand_array[i - 1] ^ (rand_array[i - 1] >> 14)) + i) & 0xFFFF
    return rand_array

def find_seed(value):
    for seed in range(0xFFFF):
        rand_array = update_rng(generate_sequence(seed))
        pos = 8
        idx = find_rand_idx(rand_array)
        gotten = dictionary[idx]
        if gotten == value:
            print(f"got seed: {hex(seed)}")
            return seed


io = remote(HOST, PORT)

io.recvuntil(b"possible: ")
leak = io.recvline().strip()
seed = find_seed(leak.decode())
rng_predict = RNGBreaker(seed)
rng_predict.update_seed()

for _ in range(20):
    idx = rng_predict.next()
    io.sendlineafter(b">", dictionary[idx].encode())

io.interactive()
