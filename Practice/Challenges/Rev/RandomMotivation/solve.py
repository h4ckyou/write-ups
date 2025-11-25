from pwn import *
from ctypes import CDLL

exe = context.binary = ELF('random-motivation')

from warnings import filterwarnings
filterwarnings('ignore')

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-pwndbg
break *validateVerificationCode+63
continue
'''.format(**locals())

io = start()
libc = CDLL("/lib/x86_64-linux-gnu/libc.so.6")
libc.srand(libc.time())

for _ in range(3):
    io.sendline("1")
    random = libc.rand()

secretKey = [0x6C, 0x6F, 0x6C, 0x20, 0x69, 0x74, 0x73, 0x20, 0x72, 0x61, 0x6E, 0x64]

random = libc.rand()
condition = random & 0xc0de

for _ in range(condition):
    libc.rand()

for j in range(12):
    secret = secretKey[j]
    secretKey[j] = (secret ^ libc.rand()) << (j & 0x1f)

io.sendline("1")

for i in range(12):
    io.sendline(str(secretKey[i]))

io.recvuntil("flag: \n")
flag = io.recvline().decode()

print(f"Flag: {flag}")

io.close()