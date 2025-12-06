OPCODES = {
    0: "LOAD",
    1: "STORE",
    2: "MOV_R_IMM",
    3: "MOV_R_R",
    4: "ADD_R_R",
    5: "ADD_R_IMM",
    6: "JNZ",
    7: "JMP",
    8: "EXT",
}

def disasm(memory, start=0, end=None):
    if end is None:
        end = len(memory)

    ip = start
    while ip < end:
        opcode = memory[ip]
        mnem = OPCODES.get(opcode, f"UNK({opcode})")

        if opcode in (0,1,2,3,4,5):
            dest = memory[ip+1]
            src  = memory[ip+2]

            if opcode == 2:  # MOV_R_IMM
                op_str = f"{mnem} r{dest}, #{src}"
            elif opcode == 5:  # ADD_R_IMM
                op_str = f"{mnem} r{dest}, #{src}"
            else:  # reg‑reg
                op_str = f"{mnem} r{dest}, r{src}"

            print(f"0x{ip:03X}: {op_str}")
            ip += 3

        elif opcode == 6:  # JNZ (2 bytes)
            offset = memory[ip+1]
            print(f"0x{ip:03X}: {mnem} 0x{ip+offset:03X}")
            ip += 2

        elif opcode == 7:  # JMP (2 bytes)
            offset = memory[ip+1]
            print(f"0x{ip:03X}: {mnem} 0x{ip+offset:03x}")
            ip += 2

        elif opcode == 8:  # EXT (1 byte)
            print(f"0x{ip:03X}: EXT")
            ip += 1

        else:
            print(f"0x{ip:03X}: UNKNOWN({opcode})")
            ip += 1



FLAG = b'DH{xxxxxxxxxxxxxxxxxxxxxxxxx}'
memory = list(FLAG + b'\xFF' * 192 + b'\x02\x02\x1d\x07\x02\x08\x01\x02\x01\x05\x02\x01\x05\x01\xf6\x06\xf4\x02\x00\x01\x02\x02\x1d\x00\x01\x02\x08\x05\x02\x01\x05\x01\xf6\x06\xf6')
disasm(memory, 221)