#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from warnings import filterwarnings

# Set up pwntools for the correct architecture
exe = context.binary = ELF('about_time')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']

filterwarnings("ignore")
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-pwndbg
break *main+158
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()

def decrypt1(enc, key):
    decrypted = ""
    
    for idx, char in enumerate(enc):
        if idx % 3 == 0:
            if ord(char) >= ord('a') and ord(char) <= ord('z'):
                decrypted += chr((((ord(char) - ord('a')) + 13 - key) % 26) + ord('a'))
            
            elif ord(char) >= ord('A') and ord(char) <= ord("Z"):
                decrypted += chr((((ord(char) - ord('A')) + 13 - key) % 26)+ ord('A'))
            
            else:
                decrypted += char
        else:
            decrypted += char
    
    return decrypted


def decrypt2(enc, key):
    buffer = list(enc)
    length = len(buffer)

    for i in range(length):
        buffer[i] = enc[(i - key) % length]

    return "".join(buffer)


def decrypt3(enc, key):
    decrypted = ""

    for idx, char in enumerate(enc):
        if ord(char) >= ord('0') and ord(char) < ord('A'):
            decrypted += chr(ord(char) - key)
        else:
            decrypted += char

    return decrypted


def solve():
    original = io.read().decode().split(' ')
    minute = int(original[0].split('T')[1].split(':')[1])
    enc_flag = original[3].split('\n')[0]
    value = (minute % 6) + 2

    print(f"encryped flag: {enc_flag}")

    func_ptr = [decrypt1, decrypt2, decrypt3]

    intermediate = func_ptr[(value + 2) % 3](enc_flag, value)
    intermediate2 = func_ptr[(value + 1) % 3](intermediate, value)
    decrypted = func_ptr[(value % 3)](intermediate2, value)

    io.sendline(decrypted)

    io.interactive()

def main():
    
    init()
    solve()

if __name__ == '__main__':
    main()

