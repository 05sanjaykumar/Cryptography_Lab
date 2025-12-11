# vigenere chiper server

import socket

def vigenere_encrypt(plaintext, key):
    ciphertext = ""
    key = key.lower()
    ki = 0
    for ch in plaintext:
        if ch.isalpha():
            shift = ord(key[ki % len(key)]) - ord('a')
            base = ord('A') if ch.isupper() else ord('a')
            ciphertext += chr((ord(ch) - base + shift) % 26 + base)
            ki += 1
        else:
            ciphertext += ch
    return ciphertext


plaintext = input("Enter message to encrypt: ")
key = input("Enter Vigenère key (letters only): ")


ciphertext = vigenere_encrypt(plaintext, key)
print("Encrypted:", ciphertext)


# ---- Send over socket ----
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