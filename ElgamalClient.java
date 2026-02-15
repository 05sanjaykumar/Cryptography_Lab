import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;

public class ElgamalClient {

    static SecureRandom random = new SecureRandom();
    

    // ---------- ELGAMAL ENCRYPT ----------

    static BigInteger[] encrypt(BigInteger m, BigInteger g, BigInteger y, BigInteger p) {

        BigInteger k = new BigInteger(p.bitLength() - 2, random);

        BigInteger c1 = g.modPow(k, p);
        BigInteger c2 = (m.multiply(y.modPow(k, p))).mod(p);

        return new BigInteger[]{c1, c2};
    }

    // ---------- CLIENT MAIN ----------

    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("localhost", 5001);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // Receive public key
        BigInteger p = new BigInteger(in.readUTF());
        BigInteger g = new BigInteger(in.readUTF());
        BigInteger y = new BigInteger(in.readUTF());

        System.out.println("Received public key from server.");

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter message: ");
        String msg = sc.nextLine();

        BigInteger m = new BigInteger(msg.getBytes());

        // Encrypt
        BigInteger[] cipher = encrypt(m, g, y, p);

        // Send ciphertext
        out.writeUTF(cipher[0].toString());
        out.writeUTF(cipher[1].toString());

        System.out.println("Encrypted message sent.");

        socket.close();
    }
}
