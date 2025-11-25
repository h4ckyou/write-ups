from capstone import *
from pwn import *

context.arch = 'amd64'
# md = Cs(CS_ARCH_X86, CS_MODE_64)
# base = 0x7ffff6c00000

with open("dump.bin", "rb") as f:
    bytecode = f.read()

# for insn in md.disasm(bytecode, base):
#     offset = insn.address - base
#     print(f"{hex(offset)}: {insn.mnemonic} {insn.op_str}")

disassembled = disasm(bytecode)

with open("dis.out", "w") as f:
    f.write(disassembled)