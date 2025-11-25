def encrypte_c(text):
    encrypted_text = ''
    key1 = 1101101
    key2 = 5

    for char in text:
        encrypted_char = ord(char) ^ key1
        encrypted_char = (encrypted_char << key2) | (encrypted_char >> (8 - key2))
        encrypted_char = (encrypted_char + 42) % 256
        binary_representation = bin(encrypted_char)[2:]
        binary_representation = binary_representation.zfill(8)
        inverted_binary = ''.join('1' if bit == '0' else '0' for bit in binary_representation)
        encrypted_text += inverted_binary + ' '

    return encrypted_text

char = "f"
key2 = 5
print((ord(char) << key2) | (ord(char) >> (8 - key2)))

'''
Fonction de chiffrement du flag.
'''

# flag.txt: 00010000 10101110 01110000 10001111 11110000 01010000 10110000 10001111 01001010 01110001 00101110 11010010 10101110 10001111 00001110 11010100 01110001 10101110