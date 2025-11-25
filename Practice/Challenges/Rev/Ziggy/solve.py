from z3 import *

flag = [BitVec(f"f_{i}", 32) for i in range(48)]
tmp = [BitVec(f"f_{i}", 32) for i in range(48)]
s = Solver()

s.add((flag[4] + flag[24]) % 128 == 0x5a)
s.add((flag[35] + flag[46]) % 0x80 == 0x69) 
s.add((flag[40] + flag[1]) % 0x80 == 0x4c) 
s.add((flag[45] + flag[19]) % 0x80 == 0x27) 
s.add((flag[13] + flag[1]) % 0x80 == 0x49) 
s.add((flag[26] + flag[20]) % 0x80 == 0x5c) 


s.add((flag[32] + flag[15]) % 0x80 == 0x57) 


s.add((flag[17] + flag[25]) % 0x80 == 0x5e) 


s.add((flag[30] + flag[17]) % 0x80 == 0x54) 


s.add((flag[28] + flag[7]) % 0x80 == 0x61) 


s.add((flag[2] + flag[1]) % 0x80 == 0x57) 


s.add((flag[16] + flag[4]) % 0x80 == 0x6c) 


s.add((flag[31] + flag[2]) % 0x80 == 100) 


s.add((flag[34] + flag[19]) % 0x80 == 0x56) 


s.add((flag[0] + flag[42]) % 0x80 == 0x51) 


s.add((flag[30] + flag[12]) % 0x80 == 0x4e) 


s.add((flag[6] + flag[41]) % 0x80 == 0x6d) 


s.add((flag[7] + flag[33]) % 0x80 == 0x62) 


s.add((flag[4] + flag[12]) % 0x80 == 0x6a) 


s.add((flag[12] + flag[9]) % 0x80 == 0x54) 


s.add((flag[33] + flag[46]) % 0x80 == 0x6c) 


s.add((flag[38] + flag[13]) % 0x80 == 0x45) 


s.add((flag[18] + flag[32]) % 0x80 == 0x53) 


s.add((flag[25] + flag[9]) % 0x80 == 0x4e) 


s.add((flag[3] + flag[28]) % 0x80 == 0x54) 


s.add((flag[31] + flag[13]) % 0x80 == 0x56) 


s.add((flag[31] + flag[17]) % 0x80 == 0x65) 


s.add((flag[26] + flag[9]) % 0x80 == 0x58) 


s.add((flag[10] + flag[40]) % 0x80 == 0x56) 


s.add((flag[25] + flag[34]) % 0x80 == 0x4b) 


s.add((flag[17] + flag[44]) % 0x80 == 0x6f) 


s.add((flag[40] + flag[12]) % 0x80 == 0x58) 


s.add((flag[13] + flag[26]) % 0x80 == 0x59) 


s.add((flag[12] + flag[9]) % 0x80 == 0x54) 


s.add((flag[42] + flag[25]) % 0x80 == 0x51) 


s.add((flag[13] + flag[37]) % 0x80 == 0x53) 


s.add((flag[12] + flag[43]) % 0x80 == 0x4e) 


s.add((flag[18] + flag[12]) % 0x80 == 0x50) 


s.add((flag[15] + flag[18]) % 0x80 == 0x46) 


s.add((flag[36] + flag[14]) % 0x80 == 0x44) 


s.add((flag[29] + flag[4]) % 0x80 == 0x6a) 


s.add((flag[11] + flag[21]) % 0x80 == 0x4e) 


s.add((flag[27] + flag[20]) % 0x80 == 0x48) 


s.add((flag[19] + flag[43]) % 0x80 == 0x53) 


s.add((flag[27] + flag[17]) % 0x80 == 0x54) 


s.add((flag[11] + flag[0]) % 0x80 == 0x48) 


s.add((flag[15] + flag[13]) % 0x80 == 0x4b) 


s.add((flag[11] + flag[8]) % 0x80 == 0x53) 


s.add((flag[29] + flag[10]) % 0x80 == 0x5c) 


s.add((flag[45] + flag[37]) % 0x80 == 0x20) 


s.add((flag[8] + flag[29]) % 0x80 == 99) 


s.add((flag[17] + flag[43]) % 0x80 == 0x54) 


s.add((flag[17] + flag[29]) % 0x80 == 100) 


s.add((flag[43] + flag[42]) % 0x80 == 0x47) 


s.add((flag[35] + flag[17]) % 0x80 == 0x61) 


s.add((flag[15] + flag[21]) % 0x80 == 0x54) 


s.add((flag[24] + flag[12]) % 0x80 == 0x4e) 


s.add((flag[13] + flag[14]) % 0x80 == 0x45) 


s.add((flag[38] + flag[43]) % 0x80 == 0x3e) 


s.add((flag[34] + flag[36]) % 0x80 == 0x47) 


s.add((flag[24] + flag[43]) % 0x80 == 0x3e) 


s.add((flag[17] + flag[21]) % 0x80 == 100) 


s.add((flag[42] + flag[27]) % 0x80 == 0x47) 


s.add((flag[18] + flag[4]) % 0x80 == 0x5c) 


s.add((flag[12] + flag[43]) % 0x80 == 0x4e) 


s.add((flag[14] + flag[24]) % 0x80 == 0x3e) 


s.add((flag[25] + flag[18]) % 0x80 == 0x4a) 


s.add((flag[0] + flag[6]) % 0x80 == 0x62) 


s.add((flag[22] * 2) % 0x80 == 0x5c) 


s.add((flag[4] + flag[20]) % 0x80 == 100) 


s.add((flag[21] + flag[9]) % 0x80 == 0x54) 


s.add((flag[2] + flag[6]) % 0x80 == 0x6d) 


s.add((flag[5] + flag[19]) % 0x80 == 0x67) 


s.add((flag[3] + flag[38]) % 0x80 == 0x45) 


s.add((flag[19] + flag[42]) % 0x80 == 0x5c) 


s.add((flag[11] + flag[1]) % 0x80 == 0x42) 


s.add((flag[46] + flag[39]) % 0x80 == 0x74) 


s.add((flag[35] + flag[2]) % 0x80 == 0x60) 


s.add((flag[15] + flag[1]) % 0x80 == 0x48) 


s.add((flag[3] + flag[1]) % 0x80 == 0x49) 


s.add((flag[43] + flag[12]) % 0x80 == 0x4e) 


s.add((flag[13] + flag[40]) % 0x80 == 0x4f) 


s.add((flag[45] + flag[31]) % 0x80 == 0x23) 


s.add((flag[11] + flag[32]) % 0x80 == 0x51) 


s.add((flag[46] + flag[6]) % 0x80 == 0x76) 


s.add((flag[46] + flag[6]) % 0x80 == 0x76) 


s.add((flag[12] + flag[25]) % 0x80 == 0x58) 


s.add((flag[1] + flag[37]) % 0x80 == 0x50) 


s.add((flag[19] + flag[26]) % 0x80 == 0x67) 


s.add((flag[10] + flag[31]) % 0x80 == 0x5d) 


s.add((flag[37] + flag[3]) % 0x80 == 0x53) 


s.add((flag[46] + flag[44]) % 0x80 == 0x77) 


s.add((flag[23] + flag[28]) % 0x80 == 0x61) 


s.add((flag[9] + flag[46]) % 0x80 == 0x62) 


s.add((flag[5] + flag[39]) % 0x80 == 0x6a) 


s.add((flag[11] + flag[46]) % 0x80 == 0x5c) 


s.add((flag[46] + flag[24]) % 0x80 == 0x5c) 


s.add((flag[35] + flag[29]) % 0x80 == 0x5b) 


s.add((flag[36] + flag[33]) % 0x80 == 0x54) 


s.add((flag[12] + flag[2]) % 0x80 == 99) 


s.add((flag[3] + flag[46]) % 0x80 == 99) 


s.add((flag[27] + flag[13]) % 0x80 == 0x45) 


s.add((flag[21] + flag[44]) % 0x80 == 0x69) 


s.add((flag[37] + flag[35]) % 0x80 == 0x59) 


s.add((flag[11] + flag[8]) % 0x80 == 0x53) 


s.add((flag[14] + flag[29]) % 0x80 == 0x4e) 


s.add((flag[19] * 2) % 0x80 == 0x68) 


s.add((flag[33] + flag[42]) % 0x80 == 0x57) 


s.add((flag[22] + flag[9]) % 0x80 == 0x53) 


s.add((flag[44] + flag[46]) % 0x80 == 0x77) 


s.add((flag[12] + flag[6]) % 0x80 == 0x68) 


s.add((flag[2] + flag[29]) % 0x80 == 99) 


s.add((flag[28] + flag[26]) % 0x80 == 0x61) 


s.add((flag[37] * 2) % 0x80 == 0x5a) 


s.add((flag[1] + flag[7]) % 0x80 == 0x56) 


s.add((flag[21] + flag[43]) % 0x80 == 0x4e) 


s.add((flag[8] + flag[28]) % 0x80 == 0x62) 


s.add((flag[8] + flag[28]) % 0x80 == 0x62) 


s.add((flag[7] + flag[16]) % 0x80 == 100) 


s.add((flag[18] + flag[1]) % 0x80 == 0x44) 


s.add((flag[21] + flag[13]) % 0x80 == 0x55) 


s.add((flag[8] + flag[25]) % 0x80 == 0x5d) 


s.add((flag[10] + flag[31]) % 0x80 == 0x5d) 


s.add((flag[10] + flag[8]) % 0x80 == 0x61) 


s.add((flag[3] + flag[41]) % 0x80 == 0x5a) 


s.add((flag[1] + flag[30]) % 0x80 == 0x42) 


s.add((flag[40] + flag[5]) % 0x80 == 0x5c) 


s.add((flag[5] + flag[1]) % 0x80 == 0x56) 


s.add((flag[8] + flag[46]) % 0x80 == 0x71) 


s.add((flag[31] + flag[45]) % 0x80 == 0x23) 


s.add((flag[38] + flag[8]) % 0x80 == 0x53) 


s.add((flag[46] + flag[21]) % 0x80 == 0x6c) 


s.add((flag[43] * 2) % 0x80 == 0x3e) 


s.add((flag[30] + flag[36]) % 0x80 == 0x44) 


s.add((flag[13] + flag[11]) % 0x80 == 0x45) 


s.add((flag[3] + flag[20]) % 0x80 == 0x4f) 


s.add((flag[18] + flag[46]) % 0x80 == 0x5e) 


s.add((flag[5] + flag[34]) % 0x80 == 0x55) 


s.add((flag[41] + flag[17]) % 0x80 == 0x69) 


s.add((flag[7] + flag[41]) % 0x80 == 0x67) 


s.add((flag[22] + flag[41]) % 0x80 == 0x62) 


s.add((flag[6] + flag[1]) % 0x80 == 0x5c) 


s.add((flag[34] + flag[24]) % 0x80 == 0x41) 


s.add((flag[20] + flag[34]) % 0x80 == 0x4b) 


s.add((flag[29] + flag[2]) % 0x80 == 99) 


s.add((flag[6] + flag[36]) % 0x80 == 0x5e) 


s.add((flag[30] + flag[13]) % 0x80 == 0x45) 


s.add((flag[10] + flag[6]) % 0x80 == 0x66) 


s.add((flag[13] + flag[9]) % 0x80 == 0x4b) 


s.add((flag[13] + flag[16]) % 0x80 == 0x57) 


s.add((flag[25] + flag[41]) % 0x80 == 0x5d) 


s.add((flag[37] + flag[28]) % 0x80 == 0x5b) 


s.add((flag[13] + flag[17]) % 0x80 == 0x5b) 


s.add((flag[45] + flag[37]) % 0x80 == 0x20) 


s.add((flag[30] + flag[23]) % 0x80 == 0x52) 


s.add((flag[7] + flag[33]) % 0x80 == 0x62) 


s.add((flag[16] + flag[0]) % 0x80 == 0x5a) 


s.add((flag[6] + flag[25]) % 0x80 == 0x62) 


s.add((flag[44] + flag[4]) % 0x80 == 0x75) 


s.add((flag[3] + flag[20]) % 0x80 == 0x4f) 


s.add((flag[21] + flag[38]) % 0x80 == 0x4e) 


s.add((flag[15] + flag[41]) % 0x80 == 0x59) 


s.add((flag[27] + flag[34]) % 0x80 == 0x41) 


s.add((flag[46] + flag[12]) % 0x80 == 0x6c) 


s.add((flag[7] + flag[30]) % 0x80 == 0x52) 


s.add((flag[9] + flag[38]) % 0x80 == 0x44) 


s.add((flag[5] + flag[39]) % 0x80 == 0x6a) 


s.add((flag[32] + flag[44]) % 0x80 == 0x6c) 


s.add((flag[13] + flag[46]) % 0x80 == 99) 


s.add((flag[20] + flag[28]) % 0x80 == 0x57) 


s.add((flag[17] + flag[14]) % 0x80 == 0x54) 


s.add((flag[7] + flag[14]) % 0x80 == 0x52) 


s.add((flag[25] + flag[4]) % 0x80 == 100) 


s.add((flag[24] + flag[10]) % 0x80 == 0x4c) 


s.add((flag[46] + flag[2]) % 0x80 == 0x71) 


s.add((flag[40] + flag[14]) % 0x80 == 0x48) 


s.add((flag[42] + flag[18]) % 0x80 == 0x49) 


s.add((flag[41] + flag[38]) % 0x80 == 0x53) 


s.add((flag[35] + flag[4]) % 0x80 == 0x67) 


s.add((flag[38] + flag[39]) % 0x80 == 0x56) 


s.add((flag[9] + flag[17]) % 0x80 == 0x5a) 


s.add((flag[23] + flag[1]) % 0x80 == 0x56) 


s.add((flag[37] + flag[32]) % 0x80 == 0x5f) 


s.add((flag[27] + flag[19]) % 0x80 == 0x53) 


s.add((flag[29] + flag[35]) % 0x80 == 0x5b) 


s.add((flag[38] + flag[21]) % 0x80 == 0x4e) 


s.add((flag[18] + flag[0]) % 0x80 == 0x4a) 


s.add((flag[39] + flag[23]) % 0x80 == 0x6a) 


s.add((flag[6] + flag[35]) % 0x80 == 0x65) 


s.add((flag[21] + flag[29]) % 0x80 == 0x5e) 


s.add((flag[7] + flag[18]) % 0x80 == 0x54) 


s.add((flag[1] + flag[27]) % 0x80 == 0x42) 


s.add((flag[8] + flag[30]) % 0x80 == 0x53) 


s.add((flag[40] + flag[9]) % 0x80 == 0x4e) 


s.add((flag[40] + flag[12]) % 0x80 == 0x58) 


s.add((flag[28] + flag[18]) % 0x80 == 0x4f) 


s.add((flag[15] + flag[46]) % 0x80 == 0x62) 


s.add((flag[13] + flag[15]) % 0x80 == 0x4b) 


s.add((flag[43] + flag[25]) % 0x80 == 0x48) 


s.add((flag[39] + flag[18]) % 0x80 == 0x58) 


s.add((flag[32] + flag[30]) % 0x80 == 0x51) 


s.add((flag[24] + flag[34]) % 0x80 == 0x41) 


s.add((flag[30] + flag[2]) % 0x80 == 0x53) 


s.add((flag[5] + flag[9]) % 0x80 == 0x58) 


s.add((flag[7] + flag[38]) % 0x80 == 0x52) 


s.add((flag[1] + flag[21]) % 0x80 == 0x52) 


s.add((flag[8] + flag[44]) % 0x80 == 0x6e) 


s.add((flag[23] + flag[46]) % 0x80 == 0x70) 


s.add((flag[6] + flag[32]) % 0x80 == 0x6b) 


s.add((flag[17] + flag[24]) % 0x80 == 0x54) 


s.add((flag[23] + flag[32]) % 0x80 == 0x65) 


s.add((flag[26] + flag[12]) % 0x80 == 0x62) 


s.add((flag[32] + flag[44]) % 0x80 == 0x6c) 


s.add((flag[21] + flag[8]) % 0x80 == 99) 


s.add((flag[35] + flag[14]) % 0x80 == 0x4b) 


s.add((flag[37] + flag[28]) % 0x80 == 0x5b) 


s.add((flag[4] + flag[28]) % 0x80 == 0x69) 


s.add((flag[10] + flag[30]) % 0x80 == 0x4c) 


s.add((flag[2] + flag[23]) % 0x80 == 0x67) 


s.add((flag[24] + flag[39]) % 0x80 == 0x56) 


s.add((flag[11] + flag[38]) % 0x80 == 0x3e) 


s.add((flag[17] + flag[6]) % 0x80 == 0x6e) 


s.add((flag[8] + flag[44]) % 0x80 == 0x6e) 


s.add((flag[15] + flag[46]) % 0x80 == 0x62) 


s.add((flag[20] + flag[2]) % 0x80 == 0x5d) 


s.add((flag[28] + flag[17]) % 0x80 == 99) 


s.add((flag[15] + flag[4]) % 0x80 == 0x60) 


s.add((flag[20] + flag[42]) % 0x80 == 0x51) 


s.add((flag[3] + flag[45]) % 0x80 == 0x19) 


s.add((flag[38] + flag[13]) % 0x80 == 0x45) 


s.add((flag[1] + flag[9]) % 0x80 == 0x48) 


s.add((flag[3] + flag[41]) % 0x80 == 0x5a) 


s.add((flag[36] + flag[6]) % 0x80 == 0x5e) 


s.add((flag[45] + flag[5]) % 0x80 == 0x26) 


s.add((flag[7] + flag[14]) % 0x80 == 0x52) 


s.add((flag[46] + flag[19]) % 0x80 == 0x71) 


s.add((flag[0] + flag[1]) % 0x80 == 0x4c) 


s.add((flag[5] + flag[41]) % 0x80 == 0x67) 


s.add((flag[19] + flag[21]) % 0x80 == 99) 


s.add((flag[9] + flag[38]) % 0x80 == 0x44) 


s.add((flag[16] + flag[27]) % 0x80 == 0x50) 


s.add((flag[17] + flag[35]) % 0x80 == 0x61) 


s.add((flag[27] + flag[20]) % 0x80 == 0x48) 


s.add((flag[19] + flag[3]) % 0x80 == 0x5a) 


s.add((flag[20] + flag[19]) % 0x80 == 0x5d) 


s.add((flag[11] + flag[2]) % 0x80 == 0x53) 


s.add((flag[5] + flag[40]) % 0x80 == 0x5c) 


s.add((flag[45] + flag[33]) % 0x80 == 0x22) 


s.add((flag[0] + flag[15]) % 0x80 == 0x4e) 


s.add((flag[15] + flag[42]) % 0x80 == 0x4d) 


s.add((flag[45] + flag[3]) % 0x80 == 0x19) 


s.add((flag[11] + flag[0]) % 0x80 == 0x48) 


s.add((flag[33] + flag[27]) % 0x80 == 0x4e) 


s.add((flag[25] + flag[10]) % 0x80 == 0x56) 


s.add((flag[32] + flag[23]) % 0x80 == 0x65) 


s.add((flag[12] + flag[35]) % 0x80 == 0x5b) 


s.add((flag[17] + flag[43]) % 0x80 == 0x54) 


s.add((flag[39] + flag[18]) % 0x80 == 0x58) 


s.add((flag[28] + flag[6]) % 0x80 == 0x67) 


s.add((flag[12] + flag[16]) % 0x80 == 0x60) 


s.add((flag[39] + flag[17]) % 0x80 == 0x6c) 


s.add((flag[3] + flag[16]) % 0x80 == 0x57) 


s.add((flag[14] + flag[5]) % 0x80 == 0x52) 


s.add((flag[4] + flag[20]) % 0x80 == 100) 


s.add((flag[24] + flag[10]) % 0x80 == 0x4c) 


s.add((flag[46] + flag[23]) % 0x80 == 0x70) 


s.add((flag[40] + flag[14]) % 0x80 == 0x48) 


s.add((flag[17] + flag[46]) % 0x80 == 0x72) 


s.add((flag[28] + flag[38]) % 0x80 == 0x4d) 


s.add((flag[14] + flag[45]) % 0x80 == 0x12) 


s.add((flag[44] + flag[22]) % 0x80 == 0x68) 


s.add((flag[31] + flag[26]) % 0x80 == 99) 


s.add((flag[32] + flag[13]) % 0x80 == 0x58) 


s.add((flag[37] + flag[32]) % 0x80 == 0x5f) 


s.add((flag[23] + flag[32]) % 0x80 == 0x65) 


s.add((flag[45] + flag[5]) % 0x80 == 0x26) 


s.add((flag[7] + flag[15]) % 0x80 == 0x58) 


s.add((flag[46] + flag[35]) % 0x80 == 0x69) 


s.add((flag[20] + flag[26]) % 0x80 == 0x5c) 


s.add((flag[14] + flag[45]) % 0x80 == 0x12) 


s.add((flag[20] + flag[31]) % 0x80 == 0x59) 


s.add((flag[15] + flag[31]) % 0x80 == 0x55) 


s.add((flag[4] + flag[14]) % 0x80 == 0x5a) 


s.add((flag[38] + flag[18]) % 0x80 == 0x40) 


s.add((flag[8] + flag[6]) % 0x80 == 0x6d) 


s.add((flag[44] + flag[33]) % 0x80 == 0x69) 


s.add((flag[32] + flag[7]) % 0x80 == 0x65) 


s.add((flag[4] + flag[2]) % 0x80 == 0x6f) 


s.add((flag[34] + flag[38]) % 0x80 == 0x41) 


s.add((flag[9] + flag[14]) % 0x80 == 0x44) 


s.add((flag[42] + flag[27]) % 0x80 == 0x47) 


s.add((flag[14] + flag[41]) % 0x80 == 0x53) 


s.add((flag[27] + flag[11]) % 0x80 == 0x3e) 


s.add((flag[35] + flag[8]) % 0x80 == 0x60) 


s.add((flag[18] + flag[31]) % 0x80 == 0x51) 


s.add((flag[1] + flag[9]) % 0x80 == 0x48) 


s.add((flag[9] + flag[42]) % 0x80 == 0x4d) 


s.add((flag[12] + flag[22]) % 0x80 == 0x5d) 


s.add((flag[15] + flag[8]) % 0x80 == 0x59) 


s.add((flag[18] + flag[34]) % 0x80 == 0x43) 


s.add((flag[8] + flag[28]) % 0x80 == 0x62) 


s.add((flag[2] + flag[20]) % 0x80 == 0x5d) 


s.add((flag[30] + flag[46]) % 0x80 == 0x5c) 


s.add((flag[43] + flag[15]) % 0x80 == 0x44) 


s.add((flag[26] + flag[1]) % 0x80 == 0x56) 


s.add((flag[35] + flag[44]) % 0x80 == 0x66) 


s.add((flag[20] * 2) % 0x80 == 0x52) 


s.add((flag[18] + flag[28]) % 0x80 == 0x4f) 


s.add((flag[16] + flag[25]) % 0x80 == 0x5a) 


s.add((flag[21] + flag[5]) % 0x80 == 0x62) 


s.add((flag[38] + flag[45]) % 0x80 == 0x12) 


s.add((flag[38] + flag[30]) % 0x80 == 0x3e) 


s.add((flag[11] + flag[14]) % 0x80 == 0x3e) 


s.add((flag[42] + flag[6]) % 0x80 == 0x61) 


s.add((flag[24] + flag[27]) % 0x80 == 0x3e) 


s.add((flag[24] + flag[35]) % 0x80 == 0x4b) 


s.add((flag[36] + flag[43]) % 0x80 == 0x44) 


s.add((flag[29] + flag[31]) % 0x80 == 0x5f) 


s.add((flag[28] + flag[46]) % 0x80 == 0x6b) 


s.add((flag[38] + flag[22]) % 0x80 == 0x4d) 


s.add((flag[45] + flag[19]) % 0x80 == 0x27) 


s.add((flag[1] + flag[4]) % 0x80 == 0x5e) 


s.add((flag[8] + flag[28]) % 0x80 == 0x62) 


s.add((flag[43] + flag[11]) % 0x80 == 0x3e) 


s.add((flag[33] + flag[2]) % 0x80 == 99) 


s.add((flag[5] + flag[46]) % 0x80 == 0x70) 


s.add((flag[38] + flag[3]) % 0x80 == 0x45) 


s.add((flag[12] + flag[8]) % 0x80 == 99) 


s.add((flag[9] + flag[11]) % 0x80 == 0x44) 


s.add((flag[20] + flag[2]) % 0x80 == 0x5d) 


s.add((flag[26] + flag[27]) % 0x80 == 0x52) 


s.add((flag[38] + flag[42]) % 0x80 == 0x47) 


s.add((flag[11] + flag[46]) % 0x80 == 0x5c) 


s.add((flag[40] + flag[8]) % 0x80 == 0x5d) 


s.add((flag[19] + flag[27]) % 0x80 == 0x53) 


s.add((flag[27] + flag[24]) % 0x80 == 0x3e) 


s.add((flag[2] + flag[19]) % 0x80 == 0x68) 


s.add((flag[29] + flag[38]) % 0x80 == 0x4e) 


s.add((flag[39] * 2) % 0x80 == 0x6e) 


s.add((flag[3] + flag[23]) % 0x80 == 0x59) 


s.add((flag[39] + flag[24]) % 0x80 == 0x56) 


s.add((flag[36] + flag[26]) % 0x80 == 0x58) 


s.add((flag[5] + flag[29]) % 0x80 == 0x62) 


s.add((flag[18] + flag[12]) % 0x80 == 0x50) 


s.add((flag[10] + flag[19]) % 0x80 == 0x61) 


s.add((flag[0] + flag[18]) % 0x80 == 0x4a) 


s.add((flag[28] + flag[10]) % 0x80 == 0x5b) 


s.add((flag[32] + flag[40]) % 0x80 == 0x5b) 


s.add((flag[26] + flag[24]) % 0x80 == 0x52) 


s.add((flag[34] + flag[9]) % 0x80 == 0x47) 


s.add((flag[23] + flag[16]) % 0x80 == 100) 


s.add((flag[3] + flag[27]) % 0x80 == 0x45) 


s.add((flag[37] + flag[41]) % 0x80 == 0x61) 


s.add((flag[7] + flag[38]) % 0x80 == 0x52) 


s.add((flag[43] + flag[22]) % 0x80 == 0x4d) 


s.add((flag[39] + flag[23]) % 0x80 == 0x6a) 


s.add((flag[20] + flag[21]) % 0x80 == 0x58) 


s.add((flag[43] + flag[34]) % 0x80 == 0x41) 


s.add((flag[42] + flag[29]) % 0x80 == 0x57) 


s.add((flag[15] + flag[31]) % 0x80 == 0x55) 


s.add((flag[22] + flag[25]) % 0x80 == 0x57) 


s.add((flag[42] + flag[45]) % 0x80 == 0x1b) 


s.add((flag[45] + flag[0]) % 0x80 == 0x1c) 


s.add((flag[22] + flag[0]) % 0x80 == 0x57) 


s.add((flag[35] + flag[32]) % 0x80 == 0x5e) 


s.add((flag[6] + flag[34]) % 0x80 == 0x5b) 


s.add((flag[1] + flag[3]) % 0x80 == 0x49) 


s.add((flag[14] + flag[22]) % 0x80 == 0x4d) 


s.add((flag[6] + flag[16]) % 0x80 == 0x6a) 


s.add((flag[6] + flag[14]) % 0x80 == 0x58) 


s.add((flag[38] + flag[41]) % 0x80 == 0x53) 


s.add((flag[2] + flag[30]) % 0x80 == 0x53) 


s.add((flag[2] + flag[19]) % 0x80 == 0x68) 


s.add((flag[39] + flag[22]) % 0x80 == 0x65) 


s.add((flag[41] + flag[44]) % 0x80 == 0x6e) 


s.add((flag[34] + flag[13]) % 0x80 == 0x48) 


s.add((flag[32] + flag[17]) % 0x80 == 0x67) 


s.add((flag[15] + flag[1]) % 0x80 == 0x48) 


s.add((flag[30] + flag[19]) % 0x80 == 0x53) 


s.add((flag[1] + flag[2]) % 0x80 == 0x57) 


s.add((flag[6] + flag[23]) % 0x80 == 0x6c) 


s.add((flag[40] + flag[8]) % 0x80 == 0x5d) 


s.add((flag[28] + flag[11]) % 0x80 == 0x4d) 


s.add((flag[38] + flag[34]) % 0x80 == 0x41) 


s.add((flag[40] + flag[34]) % 0x80 == 0x4b) 


s.add((flag[18] + flag[19]) % 0x80 == 0x55) 


s.add((flag[1] + flag[14]) % 0x80 == 0x42) 


s.add((flag[25] + flag[4]) % 0x80 == 100) 


s.add((flag[36] + flag[44]) % 0x80 == 0x5f) 


s.add((flag[42] + flag[7]) % 0x80 == 0x5b) 


s.add((flag[30] + flag[24]) % 0x80 == 0x3e) 


s.add((flag[37] + flag[17]) % 0x80 == 0x62) 


s.add((flag[7] + flag[34]) % 0x80 == 0x55) 


s.add((flag[33] + flag[3]) % 0x80 == 0x55) 


s.add((flag[29] + flag[42]) % 0x80 == 0x57) 


s.add((flag[44] + flag[35]) % 0x80 == 0x66) 


s.add((flag[41] + flag[24]) % 0x80 == 0x53) 


s.add((flag[13] + flag[6]) % 0x80 == 0x5f) 


s.add((flag[39] + flag[46]) % 0x80 == 0x74) 


s.add((flag[28] + flag[42]) % 0x80 == 0x56) 


s.add((flag[2] + flag[15]) % 0x80 == 0x59) 


s.add((flag[5] + flag[21]) % 0x80 == 0x62) 


s.add((flag[23] + flag[44]) % 0x80 == 0x6d) 


s.add((flag[40] + flag[4]) % 0x80 == 100) 


s.add((flag[18] + flag[11]) % 0x80 == 0x40) 


s.add((flag[15] + flag[27]) % 0x80 == 0x44) 


s.add((flag[31] + flag[11]) % 0x80 == 0x4f) 


s.add((flag[46] + flag[36]) % 0x80 == 0x62) 


s.add((flag[12] + flag[7]) % 0x80 == 0x62) 


s.add((flag[27] + flag[40]) % 0x80 == 0x48) 


s.add((flag[24] + flag[10]) % 0x80 == 0x4c) 


s.add((flag[14] + flag[15]) % 0x80 == 0x44) 


s.add((flag[1] + flag[15]) % 0x80 == 0x48) 


s.add((flag[11] + flag[43]) % 0x80 == 0x3e) 


s.add((flag[8] + flag[13]) % 0x80 == 0x5a) 


s.add((flag[32] + flag[17]) % 0x80 == 0x67) 


s.add((flag[16] + flag[1]) % 0x80 == 0x54) 


s.add((flag[0] + flag[27]) % 0x80 == 0x48) 


s.add((flag[32] + flag[34]) % 0x80 == 0x54) 


s.add((flag[11] + flag[27]) % 0x80 == 0x3e) 


s.add((flag[17] + flag[29]) % 0x80 == 100) 


s.add((flag[32] + flag[15]) % 0x80 == 0x57) 


s.add((flag[5] + flag[30]) % 0x80 == 0x52) 


s.add((flag[39] + flag[19]) % 0x80 == 0x6b) 


s.add((flag[10] + flag[25]) % 0x80 == 0x56) 


s.add((flag[28] + flag[17]) % 0x80 == 99) 


s.add((flag[8] + flag[0]) % 0x80 == 0x5d) 


s.add((flag[18] + flag[8]) % 0x80 == 0x55) 


s.add((flag[10] + flag[45]) % 0x80 == 0x20) 


s.add((flag[25] + flag[32]) % 0x80 == 0x5b) 


s.add((flag[15] + flag[33]) % 0x80 == 0x54) 


s.add((flag[22] + flag[38]) % 0x80 == 0x4d) 


s.add((flag[31] + flag[9]) % 0x80 == 0x55) 


s.add((flag[14] + flag[25]) % 0x80 == 0x48) 


s.add((flag[21] + flag[46]) % 0x80 == 0x6c) 


s.add((flag[32] + flag[35]) % 0x80 == 0x5e) 


s.add((flag[43] + flag[37]) % 0x80 == 0x4c) 


s.add((flag[46] + flag[37]) % 0x80 == 0x6a) 


s.add((flag[41] + flag[2]) % 0x80 == 0x68) 


s.add((flag[36] + flag[30]) % 0x80 == 0x44) 


s.add((flag[13] + flag[31]) % 0x80 == 0x56) 


s.add((flag[43] + flag[8]) % 0x80 == 0x53) 


s.add((flag[36] + flag[39]) % 0x80 == 0x5c) 


s.add((flag[43] + flag[11]) % 0x80 == 0x3e) 


s.add((flag[26] + flag[21]) % 0x80 == 0x62) 


s.add((flag[36] * 2) % 0x80 == 0x4a) 


s.add((flag[46] + flag[15]) % 0x80 == 0x62) 


s.add((flag[46] + flag[32]) % 0x80 == 0x6f) 


s.add((flag[10] + flag[37]) % 0x80 == 0x5a) 


s.add((flag[31] + flag[0]) % 0x80 == 0x59) 


s.add((flag[19] + flag[22]) % 0x80 == 0x62) 


s.add((flag[20] + flag[16]) % 0x80 == 0x5a) 


s.add((flag[2] + flag[23]) % 0x80 == 0x67) 


s.add((flag[16] + flag[27]) % 0x80 == 0x50) 


s.add((flag[1] + flag[31]) % 0x80 == 0x53) 


s.add((flag[42] + flag[27]) % 0x80 == 0x47) 


s.add((flag[38] + flag[15]) % 0x80 == 0x44) 


s.add((flag[3] + flag[33]) % 0x80 == 0x55) 


s.add((flag[26] + flag[12]) % 0x80 == 0x62) 


s.add((flag[41] + flag[31]) % 0x80 == 100) 


s.add((flag[27] + flag[23]) % 0x80 == 0x52) 


s.add((flag[23] + flag[25]) % 0x80 == 0x5c) 


s.add((flag[35] + flag[7]) % 0x80 == 0x5f) 


s.add((flag[19] + flag[9]) % 0x80 == 0x59) 


s.add((flag[13] + flag[36]) % 0x80 == 0x4b) 


s.add((flag[18] + flag[1]) % 0x80 == 0x44) 


s.add((flag[24] + flag[23]) % 0x80 == 0x52) 


s.add((flag[20] + flag[35]) % 0x80 == 0x55) 


s.add((flag[24] + flag[15]) % 0x80 == 0x44) 


s.add((flag[24] + flag[32]) % 0x80 == 0x51) 


s.add((flag[4] + flag[36]) % 0x80 == 0x60) 


s.add((flag[26] + flag[16]) % 0x80 == 100) 


s.add((flag[42] + flag[26]) % 0x80 == 0x5b) 


s.add((flag[17] + flag[9]) % 0x80 == 0x5a) 


s.add((flag[35] + flag[34]) % 0x80 == 0x4e) 


s.add((flag[35] + flag[36]) % 0x80 == 0x51) 


s.add((flag[25] + flag[2]) % 0x80 == 0x5d) 


s.add((flag[40] + flag[10]) % 0x80 == 0x56) 


s.add((flag[15] + flag[12]) % 0x80 == 0x54) 


s.add((flag[6] + flag[26]) % 0x80 == 0x6c) 


s.add((flag[28] + flag[12]) % 0x80 == 0x5d) 


s.add((flag[3] + flag[19]) % 0x80 == 0x5a) 


s.add((flag[32] + flag[7]) % 0x80 == 0x65) 


s.add((flag[36] + flag[43]) % 0x80 == 0x44) 


s.add((flag[42] + flag[12]) % 0x80 == 0x57) 


s.add((flag[39] + flag[40]) % 0x80 == 0x60) 


s.add((flag[21] + flag[8]) % 0x80 == 99) 


s.add((flag[25] + flag[9]) % 0x80 == 0x4e) 


s.add((flag[44] * 2) % 0x80 == 0x74) 


s.add((flag[18] + flag[33]) % 0x80 == 0x50) 


s.add((flag[15] + flag[33]) % 0x80 == 0x54) 


s.add((flag[5] + flag[29]) % 0x80 == 0x62) 


s.add((flag[40] + flag[6]) % 0x80 == 0x62) 


s.add((flag[38] + flag[46]) % 0x80 == 0x5c) 


s.add((flag[21] + flag[28]) % 0x80 == 0x5d) 


s.add((flag[18] + flag[43]) % 0x80 == 0x40) 


s.add((flag[37] + flag[44]) % 0x80 == 0x67) 


s.add((flag[0] + flag[17]) % 0x80 == 0x5e) 


s.add((flag[25] + flag[15]) % 0x80 == 0x4e) 


s.add((flag[23] + flag[25]) % 0x80 == 0x5c)


if s.check() == sat:
    m = s.model()
    flag =  ""

    for i in range(len(tmp)-1):
        flag += chr(m[tmp[i]].as_long() % 0x80)
    
    print(flag)