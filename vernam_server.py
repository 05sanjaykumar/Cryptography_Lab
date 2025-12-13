# server.py
import socket
import struct

def vernam_bytes(data, key):
    return bytes(d ^ k for d, k in zip(data, key))

def recv_exact(conn, n):
    data = b""
    while len(data) < n:
        packet = conn.recv(n - len(data))
        if not packet:
            raise ConnectionError("Connection closed")
        data += packet
    return data

HOST = "127.0.0.1"
PORT = 5001

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    print("Server listening...")

    conn, addr = s.accept()
    with conn:
        print("Connected:", addr)

        # read message length
        msg_len = struct.unpack("!I", recv_exact(conn, 4))[0]

        # read key and cipher safely
        key = recv_exact(conn, msg_len)
        cipher = recv_exact(conn, msg_len)

        plain = vernam_bytes(cipher, key)
        print("Decrypted:", plain.decode())
