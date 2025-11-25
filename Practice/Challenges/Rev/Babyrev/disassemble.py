from enum import Enum
import os
import time
from struct import pack

"""
VM Context

00000000 struct context // sizeof=0x407
00000000 {                                       // XREF: .data:vm_code/r main/r
00000000     char code[0x300];
00000300     char mem[0x100];
00000400     uint8_t registers[0x7];
00000407 };

The interpreter loop fetches the current instruction from the pc register and it's operand from the code

structure of code = ARG2 | ARG1 | OPCODE
"""

class Register(Enum):
    r1 = 1
    r2 = 32
    r3 = 64
    r4 = 8
    sp = 4
    pc = 2
    r5 = 16
    NONE = 0
    ERROR = -1
    

class Syscall(Enum):
    SYS_OPEN = 0x40000
    SYS_READ_CODE = 0x200000
    SYS_READ_MEM = 0x80000
    SYS_WRITE = 0x10000
    SYS_SLEEP = 0x100000
    SYS_EXIT = 0x20000

    
class FD(Enum):
    STDIN = 0
    STDOUT = 1
    STDERR = 2
    

class Instr(Enum):
    IMM = 0x400
    ADD = 0x4000
    STK = 0x8000
    STM = 0x100
    LDM = 0x800
    CMP = 0x2000
    JMP = 0x1000
    SYS = 0x200


class VM():
    def __init__(self, code):       
        self.code = code.ljust(0x300, b"\x00")
        self.mem = bytearray(0x100)
        self.registers = [0] * 7
        self.file_descriptors = {}
        self.flag_description = {}

        self.stdin = FD.STDIN.value
        self.stdout = FD.STDOUT.value
        self.stderr = FD.STDERR.value

        self.file_descriptors[self.stdin] = self.stdin
        self.file_descriptors[self.stdout] = self.stdout
        self.file_descriptors[self.stderr] = self.stderr


    def fill_up(self):
        array = [ 
            0x90737F6B00000000, 0x0DED643A17B1BA3EC, 0x0BFF81381E56C90B, 
            0x88CF93C68A7DBE, 0x2154434552524F43, 0x7369206572654820, 
            0x6C662072756F7920, 0x4F434E490A3A6761, 0x454B215443455252,
            0x67616C662F203A59
        ]

        offset = 112

        for value in array:
            self.mem[offset:offset + 8] = pack("<Q", value) 
            offset += 8 


    def disassemble(self):

        self.fill_up()

        while True:
            pc = self.registers[5]
            addr = 3 * pc

            arg1 = self.code[addr]
            arg2 = self.code[addr + 2]
            opcode = self.code[addr + 1] << 8
            
            self.registers[5] += 1
            self.registers[5] &= 0xff

            self.handler(addr, opcode, arg2, arg1)
                

    def handler(self, addr: int, opcode: int, arg1: int, arg2: int):
        """
        Handles the instruction based on the opcode.
        """

        # print(f"[regs] r1:{hex(self.registers[0])}, r2:{hex(self.registers[1])}, r3:{hex(self.registers[2])}, r4:{hex(self.registers[3])}, sp:{hex(self.registers[4])}, pc:{hex(self.registers[5])}, r5:{hex(self.registers[6])}")  
        # print(f"[instr] opcode:{hex(opcode)}, arg1:{hex(arg1)}, arg2:{hex(arg2)}")     

        if opcode & Instr.IMM.value:
            self.interpret_imm(arg1, arg2)

        elif opcode & Instr.ADD.value:
            self.interpret_add(arg1, arg2)

        elif opcode  & Instr.STK.value:
            self.interpret_stk(arg1, arg2)

        elif opcode & Instr.STM.value:
            self.interpret_stm(arg1, arg2)

        elif opcode & Instr.LDM.value:
            self.interpret_ldm(arg1, arg2)

        elif opcode & Instr.CMP.value:
            self.interpret_cmp(arg1, arg2)

        elif opcode & Instr.JMP.value:
            self.interpret_jmp(arg1, arg2)

        elif opcode & Instr.SYS.value:
            self.interpret_sys(arg1 << 16, arg2)
        else:
            print(f"Unknown opcode: {hex(opcode)}")
        
    
    def describe_flags(self, flag):
        flag_map = {
            4: "L",
            1: "G",
            16: "E",
            8: "N",
            2: "Z"
        }

        description = [char for bit, char in flag_map.items() if flag & bit]
        if not description:
            description.append("*")
        
        return "".join(description)

    
    def write_register(self, reg: int, val: int):
        try:
            reg_name = Register(reg)
        except ValueError:
            raise ValueError(f"Unknown register: {reg}")

        index = list(Register).index(reg_name)
        self.registers[index] = val & 0xFF

   
    def read_register(self, reg: int) -> int:
        try:
            reg_name = Register(reg)
        except ValueError:
            if reg:
                reg_name = Register(0)
            else:
                reg_name = Register(-1)

        index = list(Register).index(reg_name)
        return self.registers[index]
    
    
    def read_memory(self, idx: int, size: int = 1) -> int:
        """
        Read size bytes from the stack starting at idx and return result as bytes.
        """
        if not (0 <= idx < len(self.mem)):
            raise ValueError(f"Invalid memory index {hex(idx)}")

        if idx + size > len(self.mem):
            raise ValueError(f"Read exceeds stack boundary (idx={hex(idx)}, size={size})")

        data = self.mem[idx:idx+size]
        return int.from_bytes(data, "big")
    

    def write_memory(self, idx: int, val: int):
        if not (0 <= idx < len(self.mem)):
            raise ValueError("Memory index out of bounds")
        self.mem[idx] = val


    def interpret_imm(self, reg: int, val: int):
        """
        Write value to register
        """
        try:
            reg_name = Register(reg)
        except ValueError:
            raise ValueError(f"Unknown register: {reg}")

        print(f"[vm] MOV {reg_name.name}, {hex(val)}")

        self.write_register(reg, val)


    def interpret_stm(self, reg1: int, reg2: int):
        """
        Store the value in reg2 into the stack location pointed to by reg1
        """
        val1 = self.read_register(reg1)
        val2 = self.read_register(reg2)

        print(f"[vm] STM stack[{hex(val1)}] = {hex(val2)}")

        self.write_memory(val1, val2)


    def interpret_add(self, reg1: int, reg2: int):
        """
        Add the value of register one and two and store the the result in register one
        """
        
        reg1_name = Register(reg1)
        reg2_name = Register(reg2)  
        print(f"[vm] ADD {reg1_name.name}, {reg2_name.name}")

        val1 = self.read_register(reg1)
        val2 = self.read_register(reg2)

        self.write_register(reg1, val1 + val2)

    def sys_open(self, filepath: bytes, flags: int, mode: int) -> int:
        """
        Open a file and return a file descriptor.
        """
	
        path_str = filepath.decode(errors="ignore").split("\x00")[0]
        
        try:
            fd = os.open(path_str, flags, mode)
            self.file_descriptors[fd] = fd
            return fd
        except OSError as e:
            print(f"[vm] sys_open error: {e}")
            return -1
	
	
    def sys_read(self, fd: int, buf_ptr: int, size: int) -> int:
        """
        Read size bytes from a file descriptor into memory.
        """

        if fd not in self.file_descriptors:
            print(f"[vm] sys_read error: Invalid file descriptor {fd}")
            return -1 

        try:
            data = os.read(fd, size)
            self.mem[buf_ptr:buf_ptr + len(data)] = data
            return len(data)
        except OSError as e:
            print(f"[vm] sys_read error: {e}")
            return -1
	
	
    def sys_write(self, fd: int, buf_ptr: int, size: int) -> int:
        """
        Write size bytes from memory to a file descriptor.
        """

        if fd not in self.file_descriptors:
            print(f"[vm] sys_write error: Invalid file descriptor {fd}")
            return -1

        try:
            data = self.mem[buf_ptr:buf_ptr + size]
            bytes_written = os.write(fd, data)
            return bytes_written
        except OSError as e:
            print(f"[vm] sys_write error: {e}")
            return -1
	

    def sys_sleep(self, seconds: int) -> int:
        """
        Pause execution for seconds and return the time slept.
        """

        if seconds < 0:
            print("[vm] sys_sleep error: Invalid sleep time")
            return -1

        time.sleep(seconds)
        return seconds


    def interpret_sys(self, sys_num: int, reg: int):
        """
        Handle system calls based on the sys_num.
        The result is stored in the given register.
        """

        syscall = Syscall(sys_num)
        reg_name = Register(reg)

        if sys_num & Syscall.SYS_OPEN.value:
            print(f"[vm] {syscall.name}")
            fd = self.sys_open(self.mem[self.registers[0]:], self.registers[1], self.registers[2]) # sys_open(filename, oflags, mode)
            self.write_register(reg, fd)

        if sys_num & Syscall.SYS_READ_CODE.value:
            size = self.registers[2]
            if 3 * (256 - self.registers[1]) <= size:
                size = 3 * (256 - self.registers[1])
            size &= 0xff

            print(f"[vm] {syscall.name}")

            bytes_read = self.sys_read(self.registers[0], 3 * self.registers[1], size) # sys_read(fd, code, size)
            self.write_register(reg, bytes_read)

        if sys_num & Syscall.SYS_READ_MEM.value:
            size = self.registers[2]
            if (256 - self.registers[1]) <= size:
                size = -self.registers[1]
            size &= 0xff

            print(f"[vm] {syscall.name}")

            bytes_read = self.sys_read(self.registers[0], self.registers[1], size) # sys_read(fd, mem, size)
            self.write_register(reg, bytes_read)

        if sys_num & Syscall.SYS_WRITE.value:
            size = self.registers[2]
            if (256 - self.registers[1]) <= size:
                size = -self.registers[1]

            print(f"[vm] {syscall.name}")

            bytes_written = self.sys_write(self.registers[0], self.registers[1], size) # sys_write(fd, mem, size)
            self.write_register(reg, bytes_written)

        if sys_num & Syscall.SYS_SLEEP.value:
            print(f"[vm] {syscall.name}")
            slept_time = self.sys_sleep(self.registers[0]) # sys_sleep(seconds)
            self.write_register(reg, slept_time)

        if sys_num & Syscall.SYS_EXIT.value:
            print(f"[vm] {syscall.name}")
            os._exit(self.registers[0])

        if reg:
            result = self.read_register(reg)
            print(f"[vm] ... return value (in register {reg_name.name}): {hex(result)}")


    def interpret_ldm(self, reg1: int, reg2: int):
        """
        Reads the value stored in reg2, uses it as an index into the stack,
        and stores the fetched value into reg1.
        """

        reg1_name = Register(reg1)
        reg2_name = Register(reg2)  

        val = self.read_register(reg2)
        mem = self.read_memory(val)

        print(f"[vm] LDM {reg1_name.name} = stack[{reg1_name.name}] -> ({hex(mem)})")
        self.write_register(reg1, mem)


    def interpret_cmp(self, reg1: int, reg2: int):
        """
        Compares the values in reg1 and reg2 and sets reg7 as the flag register.
        """

        val1 = self.read_register(reg1)
        val2 = self.read_register(reg2)
        reg1 = Register(reg1)
        reg2 = Register(reg2)

        self.registers[6] = 0

        if val1 < val2:
            self.registers[6] |= 0x8  # Less than
        if val1 > val2:
            self.registers[6] |= 0x2  # Greater than
        if val1 == val2:
            self.registers[6] |= 0x1  # Equal
        if val1 != val2:
            self.registers[6] |= 0x10  # Not equal
        if not val1 and not val2:
            self.registers[6] |= 0x4  # Both zero

        print(f"[vm] CMP {reg1.name}, {reg2.name} -> [is ({hex(val1)} == {hex(val2)}) Flags: {hex(self.registers[6])}]")


    def interpret_jmp(self, reg1: int, reg2: int):
        """
        Conditional jmp to reg2
        """

        reg2_val = Register(reg2)
        print(f"[vm] JMP {reg2_val.name}")

        if reg1 and (reg1 & self.registers[6] == 0):
            print("[vm] ... NOT TAKEN")
            return
    
        print("[vm] ... TAKEN")
        value = self.read_register(reg2)
        self.registers[5] = value


    def interpret_stk(self, reg1: int, reg2: int):
        """
        Does some stack operation where register[4] is the stack pointer
        """

        reg1_val = Register(reg1)
        reg2_val = Register(reg2)

        print(f"[vm] STK {reg1_val.name} {reg2_val.name}")

        if reg2:
            print(f"[DEBUG]: ... pushing {reg2_val.name}")
            self.registers[4] += 1
            value = self.read_register(reg2)
            self.write_memory(self.registers[4], value)

        if reg1:
            print(f"[DEBUG]: ... popping {reg1_val.name}")
            data = self.read_memory(self.registers[4])
            self.write_register(reg1, data)
            self.registers[4] -= 1


code = [
    0x73, 0x04, 0x01, 0x96, 0x04, 0x08, 0x08, 0x01, 0x01, 0x01, 
    0x08, 0x02, 0x30, 0x04, 0x01, 0x7A, 0x04, 0x20, 0x15, 0x04, 
    0x40, 0x02, 0x04, 0x08, 0x02, 0x40, 0x08, 0x08, 0x80, 0x00, 
    0xAB, 0x04, 0x02, 0x00, 0x04, 0x40, 0x40, 0x20, 0x08, 0x11, 
    0x04, 0x08, 0x08, 0x10, 0x01, 0xA5, 0x04, 0x08, 0x08, 0x10, 
    0x0A, 0x90, 0x04, 0x20, 0x1C, 0x04, 0x40, 0x01, 0x04, 0x01, 
    0x08, 0x02, 0x01, 0xBB, 0x04, 0x01, 0x00, 0x04, 0x20, 0x08, 
    0x02, 0x04, 0x00, 0x04, 0x20, 0x04, 0x40, 0x20, 0xFF, 0x04, 
    0x40, 0x00, 0x04, 0x01, 0x08, 0x40, 0x01, 0x08, 0x02, 0x08, 
    0x00, 0x04, 0x20, 0x04, 0x40, 0x20, 0x00, 0x04, 0x40, 0x08, 
    0x40, 0x40, 0x01, 0x04, 0x01, 0x08, 0x02, 0x01, 0x00, 0x04, 
    0x01, 0x00, 0x02, 0x02, 0x01, 0x80, 0x00, 0x20, 0x80, 0x00, 
    0x40, 0x80, 0x00, 0x30, 0x04, 0x01, 0x2A, 0x04, 0x40, 0x01, 
    0x08, 0x20, 0x40, 0x40, 0x20, 0x20, 0x01, 0x01, 0x31, 0x04, 
    0x01, 0xED, 0x04, 0x40, 0x01, 0x08, 0x20, 0x40, 0x40, 0x20, 
    0x20, 0x01, 0x01, 0x32, 0x04, 0x01, 0xAE, 0x04, 0x40, 0x01, 
    0x08, 0x20, 0x40, 0x40, 0x20, 0x20, 0x01, 0x01, 0x33, 0x04, 
    0x01, 0x21, 0x04, 0x40, 0x01, 0x08, 0x20, 0x40, 0x40, 0x20, 
    0x20, 0x01, 0x01, 0x34, 0x04, 0x01, 0x4F, 0x04, 0x40, 0x01, 
    0x08, 0x20, 0x40, 0x40, 0x20, 0x20, 0x01, 0x01, 0x35, 0x04, 
    0x01, 0x5A, 0x04, 0x40, 0x01, 0x08, 0x20, 0x40, 0x40, 0x20, 
    0x20, 0x01, 0x01, 0x36, 0x04, 0x01, 0xEC, 0x04, 0x40, 0x01, 
    0x08, 0x20, 0x40, 0x40, 0x20, 0x20, 0x01, 0x01, 0x37, 0x04, 
    0x01, 0xF5, 0x04, 0x40, 0x01, 0x08, 0x20, 0x40, 0x40, 0x20, 
    0x20, 0x01, 0x01, 0x38, 0x04, 0x01, 0x5F, 0x04, 0x40, 0x01, 
    0x08, 0x20, 0x40, 0x40, 0x20, 0x20, 0x01, 0x01, 0x39, 0x04, 
    0x01, 0x49, 0x04, 0x40, 0x01, 0x08, 0x20, 0x40, 0x40, 0x20, 
    0x20, 0x01, 0x01, 0x3A, 0x04, 0x01, 0xFA, 0x04, 0x40, 0x01, 
    0x08, 0x20, 0x40, 0x40, 0x20, 0x20, 0x01, 0x01, 0x3B, 0x04, 
    0x01, 0x18, 0x04, 0x40, 0x01, 0x08, 0x20, 0x40, 0x40, 0x20, 
    0x20, 0x01, 0x01, 0x3C, 0x04, 0x01, 0xD5, 0x04, 0x40, 0x01, 
    0x08, 0x20, 0x40, 0x40, 0x20, 0x20, 0x01, 0x01, 0x3D, 0x04, 
    0x01, 0xCF, 0x04, 0x40, 0x01, 0x08, 0x20, 0x40, 0x40, 0x20, 
    0x20, 0x01, 0x01, 0x3E, 0x04, 0x01, 0x86, 0x04, 0x40, 0x01, 
    0x08, 0x20, 0x40, 0x40, 0x20, 0x20, 0x01, 0x01, 0x3F, 0x04, 
    0x01, 0x6B, 0x04, 0x40, 0x01, 0x08, 0x20, 0x40, 0x40, 0x20, 
    0x20, 0x01, 0x01, 0x40, 0x04, 0x01, 0x56, 0x04, 0x40, 0x01, 
    0x08, 0x20, 0x40, 0x40, 0x20, 0x20, 0x01, 0x01, 0x41, 0x04, 
    0x01, 0xB9, 0x04, 0x40, 0x01, 0x08, 0x20, 0x40, 0x40, 0x20, 
    0x20, 0x01, 0x01, 0x42, 0x04, 0x01, 0x8E, 0x04, 0x40, 0x01, 
    0x08, 0x20, 0x40, 0x40, 0x20, 0x20, 0x01, 0x01, 0x43, 0x04, 
    0x01, 0xD8, 0x04, 0x40, 0x01, 0x08, 0x20, 0x40, 0x40, 0x20, 
    0x20, 0x01, 0x01, 0x44, 0x04, 0x01, 0x52, 0x04, 0x40, 0x01, 
    0x08, 0x20, 0x40, 0x40, 0x20, 0x20, 0x01, 0x01, 0x00, 0x80, 
    0x40, 0x00, 0x80, 0x20, 0x00, 0x80, 0x01, 0x04, 0x04, 0x02, 
    0xB6, 0x04, 0x20, 0x05, 0x04, 0x40, 0x01, 0x04, 0x01, 0x08, 
    0x02, 0x01, 0x01, 0x80, 0x00, 0x20, 0x80, 0x00, 0x40, 0x80, 
    0x00, 0x30, 0x04, 0x20, 0x1B, 0x04, 0x40, 0x00, 0x04, 0x01, 
    0x08, 0x02, 0x08, 0x00, 0x80, 0x40, 0x00, 0x80, 0x20, 0x00, 
    0x80, 0x01, 0x26, 0x04, 0x02, 0xAC, 0x04, 0x20, 0x0A, 0x04, 
    0x40, 0x01, 0x04, 0x01, 0x08, 0x02, 0x01, 0x01, 0x04, 0x01, 
    0x00, 0x02, 0x02, 0x40, 0x40, 0x01, 0x40, 0x40, 0x20, 0xFF, 
    0x04, 0x08, 0x08, 0x40, 0x01, 0x08, 0x40, 0x20, 0x01, 0x80, 
    0x00, 0x20, 0x80, 0x00, 0x01, 0x08, 0x01, 0x20, 0x08, 0x20, 
    0x20, 0x20, 0x01, 0x00, 0x80, 0x20, 0x00, 0x80, 0x01, 0xBF, 
    0x04, 0x08, 0x08, 0x10, 0x10, 0xFF, 0x04, 0x08, 0x08, 0x40, 
    0x40, 0x00, 0x04, 0x08, 0x08, 0x20, 0x40, 0xAD, 0x04, 0x08, 
    0x08, 0x10, 0x10, 0x40, 0x80, 0x08, 0x00, 0x80, 0x02, 0x00
]

code = bytes(code)

vm = VM(code)
vm.disassemble()
