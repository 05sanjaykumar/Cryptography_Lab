import socket

def encrypt(plaintext, key):
    ciphertext = ""
    for char in plaintext:
        if char.isalpha():
            shift = key % 26
            base = ord('A') if char.isupper() else ord('a')
            encrypted_char = chr((ord(char) - base + shift) % 26 + base)
            ciphertext += encrypted_char
        else:
            ciphertext += char
    return ciphertext

plaintext = input("Enter the message to encrypt: ")
key = int(input("Enter the encryption key (shift value): "))
ciphertext = encrypt(plaintext, key)
print(f"Encrypted message: {ciphertext}")

HOST = "0.0.0.0"
PORT = 5001

s = socket.socket()
s.bind((HOST, PORT))
s.listen(1)

print("Server ready, waiting for client...")

conn, addr = s.accept()
print("Client connected:", addr)

conn.send(ciphertext.encode())  # send encrypted text

conn.close()
s.close()