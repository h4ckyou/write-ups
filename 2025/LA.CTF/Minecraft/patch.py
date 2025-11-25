from pwn import *

# Load our binary
exe = 'chall_patched'
elf = context.binary = ELF(exe, checksec=False)

# Patch out the call to ptrace ;)
elf.asm(elf.symbols.sleep, 'ret')

# Save the patched binary
elf.save('prob1')