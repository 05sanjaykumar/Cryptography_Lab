import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;

public class Diffie_Hellman_Attacker {
    static final int LISTEN_PORT = 5001;
    static final int BOB_PORT = 6000;

    static String xor(String msg, String key) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < msg.length(); i++)
            out.append((char)(msg.charAt(i) ^ key.charAt(i % key.length())));
        return out.toString();
    }

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(LISTEN_PORT);

        System.out.println("Attacker waiting for Alice...");

        Socket alice = server.accept();
        System.out.println("Alice connected");


        DataInputStream ain = new DataInputStream(alice.getInputStream());
        DataOutputStream aout = new DataOutputStream(alice.getOutputStream());

        Socket bob = new Socket("localhost",BOB_PORT);

        DataInputStream bin = new DataInputStream(bob.getInputStream());
        DataOutputStream bout = new DataOutputStream(bob.getOutputStream());

        BigInteger p = new BigInteger(ain.readUTF());
        BigInteger g = new BigInteger(ain.readUTF());
        BigInteger alicePub = new BigInteger(ain.readUTF());

        SecureRandom rand = new SecureRandom();

        BigInteger x1 = new BigInteger(10, rand); // with Alice
        BigInteger x2 = new BigInteger(10, rand); // with Bob

        BigInteger y1 = g.modPow(x1, p);
        BigInteger y2 = g.modPow(x2, p);

         // send fake key to Alice
        aout.writeUTF(y1.toString());

        // send fake key to Bob pretending to be Alice
        bout.writeUTF(p.toString());
        bout.writeUTF(g.toString());
        bout.writeUTF(y2.toString());

        BigInteger bobPub = new BigInteger(bin.readUTF());

        BigInteger keyAlice = alicePub.modPow(x1, p);
        BigInteger keyBob = bobPub.modPow(x2, p);

        System.out.println("Key with Alice: " + keyAlice);
        System.out.println("Key with Bob: " + keyBob);

        String encryptedFromAlice = ain.readUTF();
        String decrypted = xor(encryptedFromAlice, keyAlice.toString());

        System.out.println("Intercepted message: " + decrypted);

        // re-encrypt for Bob
        String reEncrypted = xor(decrypted, keyBob.toString());
        bout.writeUTF(reEncrypted);

        alice.close();
        bob.close();
        server.close();
        
    }
}
