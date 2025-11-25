from pwn import process, info, remote
import re

def generate_spell(target):
    spell = []
    v7 = target

    while v7 > 0:
        if v7 % 2 == 0:
            r = v7 // 2
            spell.append("B")
        else:
            r = v7 - 1
            spell.append("A")

        v7 = r
    
    return ''.join(spell[::-1])


def solve():

    # io = process("./prob")
    io = remote("host3.dreamhack.games", "8741")

    for i in range(10):
        io.recvuntil(b"[INFO]")
        output = io.recvline()
        numbers = list(map(int, re.findall(r"\d+", output.decode())))[::-1]
        urandom = [numbers[-1]] + [i for i in numbers][:-1]
        value = "0x"

        for i in urandom:
            value += hex(i)[2:].zfill(2)

        urandom = int(value, 16)
        info("urandom: %#x", urandom)

        io.sendline(generate_spell(urandom).encode())

    io.interactive()


def main():
    solve()


if __name__ == '__main__':
    main()
