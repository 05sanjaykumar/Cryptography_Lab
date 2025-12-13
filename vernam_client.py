# client.py
import socket
import os
import struct

def vernam_bytes(data, key):
    return bytes(d ^ k for d, k in zip(data, key))

HOST = "127.0.0.1"
PORT = 5001

message = b"HELLO SERVER"
key = os.urandom(len(message))
cipher = vernam_bytes(message, key)

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))

    # send message length (4 bytes)
    s.sendall(struct.pack("!I", len(message)))

    # send key and cipher
    s.sendall(key)
    s.sendall(cipher)

print("Sent:", message)
