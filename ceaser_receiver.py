# client.py
import socket

SERVER_IP = "127.0.0.1"   # change if remote machine
PORT = 5001

s = socket.socket()
s.connect((SERVER_IP, PORT))

data = s.recv(1024).decode()
print("Received encrypted message:", data)

s.close()
