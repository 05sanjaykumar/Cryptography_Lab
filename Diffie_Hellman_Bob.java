import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;

public class Diffie_Hellman_Bob {
    static final int PORT = 6000;
    static BigInteger p, g, privateKey, publicKey, sharedKey;

    static String xor(String msg, String key)
    {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<msg.length(); i++)
        {
            sb.append((char)(msg.charAt(i)^key.charAt(i % key.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Bob waiting on port " + PORT);

        Socket socket = server.accept();

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        p = new BigInteger(in.readUTF());
        g = new BigInteger(in.readUTF());

        SecureRandom rand = new SecureRandom();
        privateKey = new BigInteger(10, rand);
        publicKey = g.modPow(privateKey, p);
        System.out.println("Bob Shared Key: " + sharedKey);

        BigInteger fakeAlice = new BigInteger(in.readUTF());
        // send Bob public key

        out.writeUTF(publicKey.toString());

        sharedKey = fakeAlice.modPow(privateKey, p);
        System.out.println("Bob Shared Key: " + sharedKey);

        // receive encrypted message
        String encrypted = in.readUTF();
        String decrypted = xor(encrypted, sharedKey.toString());

        System.out.println("Message from Alice: " + decrypted);

        socket.close();
        server.close();



    }
}
