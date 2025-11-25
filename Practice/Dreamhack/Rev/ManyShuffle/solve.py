from pwn import *
from ctypes import CDLL

matrix = "0B08030401000E0D0F090C060205070A0F04080B06070D020C03050E0A000109040C0E050D06090A01000B0F020703080A080F030406000B010D090705020C0E0B06090F02010A0E030C0D000504080709040B05060F080003010A0D020E0C070A0E0907080D030B0C0F02000405060105040D010002090B0C07080A060E0F03040805020A0F0B0700010C030E06090D0D0E0F0B00020A04070609010503080C0E0203050A010700090D0C0B04060F08030B0E0A06040701020D0F000C0905080D0F01020C0A03070906080500040B0E000E040D06010A05030C070B0F0208090B0208070503090D040F0001060C0E0A0B0108000C0D040E0A060F0709050302"
table = bytes.fromhex(matrix)

def generate():
    libc = CDLL("libc.so.6")
    seed = int(time.time())
    libc.srand(seed)

    array = []

    for i in range(16):
        value = libc.rand() % 26 + 65
        array.append(value)
        
    return array
    

def transpose(value):
    shuffled = bytearray(value) + b"\x00" * 16
    array =  bytearray(64)

    for row in range(16):
        for col in range(16):
            if (row & 1) != 0:
                shuffled[table[row * 16 + col]] = array[col + 32]
            else:
                array[table[row * 16 + col] + 32] = shuffled[col] 

    
# io = process("./many-shuffle")
io = remote("host1.dreamhack.games", "16757")

# value = generate()

io.recvuntil(b"String: ")
r = io.recv(16) + b"\x00" * 16
value = bytearray(r)
array = bytearray(64)

for row in range(15, -1, -1):
    for col in range(16):
        if (row & 1) != 0:
            array[col + 32] = value[table[row * 16 + col]]
        else:
            value[col] = array[table[row * 16 + col] + 32]

original_str = value[:16]

io.sendline(original_str)

io.interactive()