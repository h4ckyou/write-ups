import base64
import struct
from Crypto.Cipher import AES

blob = b"VE9LTk9STTHQy46zxLgsh9QbYkCQeqhnYUR+V1MWu/FS9JHxwv8/WJlRgH4gGgl3RhG3r/5ObI0zE2KJwkbvA9E2UAY7aOkRIL+usQO2f/aQ1O8t2oUI6uTSBcU/G4cml3cVZxZMvW4rh65Pibt6n0MdKtNCGOxGlFvOWVwqQyljtqdjgLixbEfCAhcKGdi9G3K7pBAAAAAiAAAAK/q9tx96IdkFANfNFIJT4nrqYlE5Y23m+DsfeUldS3kYFA=="
data = base64.b64decode(blob)[8:]
salt = data[:32]
nonce = data[32:44]
target_norm_sha256 = data[44:76]
target_token_multiset_hash = data[76:108]
target_merkle_root = data[108:140]
token_len = struct.unpack("<I", data[140:144])[0]
flag_count = struct.unpack("<I", data[144:148])[0] - 0x10
encrypted = data[148:]

print(f"salt: {salt.hex()}")
print(f"nonce: {nonce.hex()}")
print(f"target_norm_sha256: {target_norm_sha256.hex()}")
print(f"target_token_multiset_hash: {target_token_multiset_hash.hex()}")
print(f"target_merkle_root: {target_merkle_root.hex()}")
print(f"token_len: {hex(token_len)}")
print(f"flag_count: {hex(flag_count)}")
print(f"encrypted: {encrypted.hex()}")


