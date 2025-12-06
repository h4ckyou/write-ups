import sys

class TinyMachine():
    OPCODES = {
        0: "LOAD",
        1: "STORE",
        2: "MOV_R_IMM",
        3: "MOV_R_R",
        4: "ADD_R_R",
        5: "ADD_R_IMM",
        6: "JNZ",
        7: "JMP",
        8: "EXT"
    }

    def __init__(self, memory):
        self.memory = memory
        self.ip = 0
        self.registers = [0, 0, 0, 0]
        self.halted = False

    def setIp(self, ip):
        self.ip = ip

    def run(self):
        while not self.halted:
            try:
                # print(memory)
                opcode = self.memory[self.ip]
                op_str = self.OPCODES.get(opcode, f"UNKNOWN({opcode})")

                dest = src = None
                trace = ""

                if opcode in [0,1,2,3,4,5]:
                    dest = self.memory[self.ip + 1]
                    src = self.memory[self.ip + 2]
                    trace = f"{op_str} r{dest}, {src if opcode in [2,5] else f'r{src}' }"
                elif opcode == 6 and self.registers[1] != 0:
                    if self.registers[1] != 0:
                        dest = self.memory[self.ip + 1]
                        trace = f"{op_str} OFFSET={hex(dest)}"
                    else:
                        trace = f""
                elif opcode == 7:
                    dest = self.memory[self.ip + 1]
                    trace = f"{op_str} OFFSET=0x{self.ip + dest:03x}"
                elif opcode == 8:
                    trace = f"{op_str} R0={self.registers[0]} R1={self.registers[1]}"
                else:
                    trace = op_str

                print(f"0x{self.ip:03X}: {trace} | REG={self.registers}")

                # Execute
                if opcode == 0:  # LOAD
                    self.registers[dest] = self.memory[self.registers[src]]
                    self.ip += 3
                elif opcode == 1:  # STORE
                    self.memory[self.registers[dest]] = self.registers[src]
                    self.ip += 3
                elif opcode == 2:  # MOV_R_IMM
                    self.registers[dest] = src
                    self.ip += 3
                elif opcode == 3:  # MOV_R_R
                    self.registers[dest] = self.registers[src]
                    self.ip += 3
                elif opcode == 4:  # ADD_R_R
                    self.registers[dest] = (self.registers[dest] + self.registers[src]) & 0xFF
                    self.ip += 3
                elif opcode == 5:  # ADD_R_IMM
                    self.registers[dest] = (self.registers[dest] + src) & 0xFF
                    self.ip += 3
                elif opcode == 6:  # JNZ
                    if self.registers[1] != 0:
                        self.ip = (self.ip + dest) & 0xFF
                    else:
                        self.ip += 2
                elif opcode == 7:  # JMP
                    self.ip = (self.ip + dest) & 0xFF
                elif opcode == 8:  # EXT
                    if self.registers[0] == 0:
                        self.registers[1] = sys.stdin.buffer.read(1)[0]
                    elif self.registers[0] == 1:
                        sys.stdout.write(chr(self.registers[1]))
                        sys.stdout.flush()
                    self.ip += 1
                else:
                    print(f"Halted at 0x{self.ip:03X}, unknown opcode {opcode}")
                    self.halted = True

            except Exception as e:
                print(f"Exception at 0x{self.ip:03X}: {e}")
                self.halted = True

FLAG = b'DH{xxxxxxxxxxxxxxxxxxxxxxxxx}'

memory = list(FLAG + b'\xFF' * 192 + b'\x02\x02\x1d\x07\x02\x08\x01\x02\x01\x05\x02\x01\x05\x01\xf6\x06\xf4\x02\x00\x01\x02\x02\x1d\x00\x01\x02\x08\x05\x02\x01\x05\x01\xf6\x06\xf6')
machine = TinyMachine(memory)
machine.ip = 221
machine.run()