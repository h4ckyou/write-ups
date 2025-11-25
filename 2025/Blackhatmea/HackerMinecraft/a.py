import base64, struct, hashlib
from Crypto.Cipher import AES

blob_b64 = b"VE9LTk9STTHQy46zxLgsh9QbYkCQeqhnYUR+V1MWu/FS9JHxwv8/WJlRgH4gGgl3RhG3r/5ObI0zE2KJwkbvA9E2UAY7aOkRIL+usQO2f/aQ1O8t2oUI6uTSBcU/G4cml3cVZxZMvW4rh65Pibt6n0MdKtNCGOxGlFvOWVwqQyljtqdjgLixbEfCAhcKGdi9G3K7pBAAAAAiAAAAK/q9tx96IdkFANfNFIJT4nrqYlE5Y23m+DsfeUldS3kYFA=="
data = base64.b64decode(blob_b64)[8:]

salt   = data[:32]
nonce  = data[32:44]
encrypted = data[148:] 

print("Salt: ", salt.hex())
print("Nonce:", nonce.hex())

qwords_hex = [
    0x896213338d6c4efe
]

v72 = b"".join(struct.pack("<Q", q) for q in qwords_hex)
print("v72:", v72.hex())

key = hashlib.sha256(v72).digest()
print("Key:", key.hex())

ct, tag = encrypted[:-16], encrypted[-16:]

cipher = AES.new(key, AES.MODE_GCM, nonce=nonce)
plaintext = cipher.decrypt_and_verify(ct, tag)

print("flag:", plaintext)
