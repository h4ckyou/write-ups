import sys

class TinyMachine():
    def __init__(self, memory):
        self.memory = memory
        self.ip = 0
        self.registers = [0, 0, 0, 0]
        self.halted = False
        self.addr = 221

    def setIp(self, ip):
        self.ip = ip

    def run(self):
        if self.halted:
            return

        while True:
            try:
                opcode = self.memory[self.ip]

                if opcode in [0, 1, 2, 3, 4, 5]:
                    dest = self.memory[self.ip + 1]
                    src = self.memory[self.ip + 2]                

                if opcode == 0: #LOAD
                    print(f"{hex(self.addr)}: LOAD r{dest}, *mem[r{src}]")
                    self.registers[dest] = self.memory[self.registers[src]]
                    self.ip += 3
                elif opcode == 1: #STORE
                    self.memory[self.registers[dest]] = self.registers[src]
                    self.ip += 3
                elif opcode == 2: #MOV_R_IMM
                    self.registers[dest] = src
                    self.ip += 3
                elif opcode == 3: #MOV_R_R
                    self.registers[dest] = self.registers[src]
                    self.ip += 3
                elif opcode == 4: #ADD_R_R
                    self.registers[dest] = (self.registers[dest] + self.registers[src]) & 0xFF
                    self.ip += 3
                elif opcode == 5: #ADD_R_IMM
                    self.registers[dest] = (self.registers[dest] + src) & 0xFF
                    self.ip += 3
                elif opcode == 6: #JNZ
                    if self.registers[1] != 0:
                        dest = self.memory[self.ip + 1]
                        self.ip = (self.ip + dest) & 0xFF
                    else:
                        self.ip += 2
                elif opcode == 7: #JMP
                    dest = self.memory[self.ip + 1]
                    self.ip = (self.ip + dest) & 0xFF
                elif opcode == 8: #EXT
                    if self.registers[0] == 0:
                        self.registers[1] = sys.stdin.buffer.read(1)[0]
                    elif self.registers[0] == 1:
                        sys.stdout.write(chr(self.registers[1]))
                        sys.stdout.flush()
                    self.ip += 1
                else:
                    self.halted = True
                    return
            except Exception as e:
                halted = True
                return

FLAG = b'DH{xxxxxxxxxxxxxxxxxxxxxxxxx}'

memory = list(FLAG + b'\xFF' * 192 + b'\x02\x02\x1d\x07\x02\x08\x01\x02\x01\x05\x02\x01\x05\x01\xf6\x06\xf4\x02\x00\x01\x02\x02\x1d\x00\x01\x02\x08\x05\x02\x01\x05\x01\xf6\x06\xf6')
machine = TinyMachine(memory)
machine.ip = 221
machine.run()