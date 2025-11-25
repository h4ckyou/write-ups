prefix = "HTB{"
enc = "l5{0v0Y7fVf?u>|:O!|Ly%d$Gu.b/o+>]"
key = ""

for i in range(len(enc)):
    key += chr(ord(enc[i]) ^ i)

print(key)