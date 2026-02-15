import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;

public class ElgamalServer {

    static SecureRandom random = new SecureRandom();
    static final BigInteger TWO = new BigInteger("2");


    // ---------- ELGAMAL FUNCTIONS ----------

    static BigInteger generatePrime() {
        return BigInteger.probablePrime(512, random);
    }

    static BigInteger generateGenerator(BigInteger p) {
        return new BigInteger(p.bitLength() - 1, random)
                .mod(p.subtract(TWO))
                .add(TWO);
    }

    static BigInteger privateKey(BigInteger p) {
        return new BigInteger(p.bitLength() - 2, random);
    }

    static BigInteger publicKey(BigInteger g, BigInteger x, BigInteger p) {
        return g.modPow(x, p);
    }

    static BigInteger decrypt(BigInteger c1, BigInteger c2, BigInteger x, BigInteger p) {
        BigInteger s = c1.modPow(x, p);
        BigInteger sInv = s.modInverse(p);
        return c2.multiply(sInv).mod(p);
    }

    // ---------- SERVER MAIN ----------

    public static void main(String[] args) throws Exception {

        ServerSocket server = new ServerSocket(5001);
        System.out.println("Server waiting for connection...");

        Socket socket = server.accept();
        System.out.println("Client connected!");

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // Key generation
        BigInteger p = generatePrime();
        BigInteger g = generateGenerator(p);
        BigInteger x = privateKey(p);
        BigInteger y = publicKey(g, x, p);

        System.out.println("\nPublic Key:");
        System.out.println("p=" + p);
        System.out.println("g=" + g);
        System.out.println("y=" + y);

        // Send public key
        out.writeUTF(p.toString());
        out.writeUTF(g.toString());
        out.writeUTF(y.toString());

        // Receive ciphertext
        BigInteger c1 = new BigInteger(in.readUTF());
        BigInteger c2 = new BigInteger(in.readUTF());

        System.out.println("\nCipher received:");
        System.out.println("c1=" + c1);
        System.out.println("c2=" + c2);

        // Decrypt
        BigInteger m = decrypt(c1, c2, x, p);
        System.out.println("\nDecrypted message:");
        System.out.println(new String(m.toByteArray()));

        socket.close();
        server.close();
    }
}
