from pwn import xor
import hashlib

def hashpw(val):
    return hashlib.md5(val).hexdigest()

def encpw(val):
    key = b"batman"
    return xor(key, val)[:len(val)]
    
wordlist    = "/usr/share/wordlists/rockyou.txt"
target_hash = "7b72210507bab7b9bf007d48f840e7b2"

try:
    with open(wordlist, encoding="latin-1") as file:
        for line in file:
            if line.isprintable:
                password = line.strip()
                print(f"Trying password: {password}")
                enc = hashpw(encpw(password))
                if enc == target_hash:
                    print(password)
                    break
            else:
                pass
except Exception as e:
    print(e)
