from pwn import *

exe = ELF("/challenge/revroundup", checksec=False)
context.log_level = "info"

def forward(x):
    return (0x1D * ((0xC5 * x + 0x71) ^ (4 * ((0xC5 * x) & 0xF)))) ^ 0xA7

def stage1():
    io.sendlineafter(b":", b"1")
    data = exe.read(0x0603100, 0x100)
    io.send(data)

def stage2():
    io.sendlineafter(b":", b"2")
    target = b"ida4ever"
    solution = []

    for t in target:
        for x in range(256):
            if forward(x) & 0xFF == t:
                solution.append(x)
                break

    io.send(bytes(solution))

def stage3():
    points = [
        (10, b"A"),
        (12, b"B"),
        (13, b"D"),
        (11, b"C"),
        (8, b"E"),
        (9, b"G"),
        (6, "F")
    ]

    io.sendlineafter(b":", b"3")

    for i in range(len(points)):
        x, y = points[i]
        io.sendlineafter(b":", b"1")
        io.sendlineafter(b":", str(x).encode())
        io.sendlineafter(b":", y)
    
    io.sendline(b"3")

def stage4():
    io.sendlineafter(b":", b"4")
    query = "GET / HTTP/1.1\n\n"
    
    p1 = remote("localhost", 1337)
    p2 = remote("localhost", 13372)

    for i in range(0, len(query), 2):
        x = query[i]
        y = query[i+1]
        p1.send(x.encode())
        p2.send(y.encode())

def stage5():
    expected = 0x6824
    io.sendlineafter(b":", b"5")
    io.sendlineafter(b"...", hex(expected).encode())


io = process(exe.path)

stage1()
stage2()
stage3()
stage4()
stage5()

io.interactive()
