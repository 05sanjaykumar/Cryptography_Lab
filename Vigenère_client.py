# vigenere_client.py
import socket

def vigenere_decrypt(ciphertext, key):
    plaintext = ""
    key = key.lower()
    ki = 0
    for ch in ciphertext:
        if ch.isalpha():
            shift = ord(key[ki % len(key)]) - ord('a')
            base = ord('A') if ch.isupper() else ord('a')
            plaintext += chr((ord(ch) - base - shift) % 26 + base)
            ki += 1
        else:
            plaintext += ch
    return plaintext

SERVER_IP = "127.0.0.1"
PORT = 5001

key = input("Enter Vigenère key to decrypt: ")

s = socket.socket()
s.connect((SERVER_IP, PORT))

data = s.recv(1024).decode()
print("Received ciphertext:", data)

plaintext = vigenere_decrypt(data, key)
print("Decrypted plaintext:", plaintext)

s.close()
