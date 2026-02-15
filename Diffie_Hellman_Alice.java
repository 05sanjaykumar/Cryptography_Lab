import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.util.Scanner;

public class Diffie_Hellman_Alice {
    static final int PORT = 5001;
    
    static String xor(String msg, String key)
    {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<msg.length();i++)
        {
            sb.append((char)(msg.charAt(i)^key.charAt(i % key.length())));
        }
        return sb.toString();
    }
    public static void main(String[] args) throws Exception {
        
        Socket socket = new Socket("localhost",PORT);
        Scanner sc = new Scanner(System.in);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        System.out.print("Enter prime (p): ");
        BigInteger p = new BigInteger(sc.nextLine());

        System.out.print("Enter primitive root (g): ");
        BigInteger g = new BigInteger(sc.nextLine());

        SecureRandom rand = new SecureRandom();
        BigInteger privateKey = new BigInteger(10, rand);
        BigInteger publicKey = g.modPow(privateKey, p);

        out.writeUTF(p.toString());
        out.writeUTF(g.toString());
        out.writeUTF(publicKey.toString());

        BigInteger fakeBob = new BigInteger(in.readUTF());

        BigInteger sharedKey = fakeBob.modPow(privateKey, p);
        System.out.println("Alice Shared Key: " + sharedKey);

        System.out.print("Enter message: ");
        String msg = sc.nextLine();

        String encrypted = xor(msg, sharedKey.toString());
        out.writeUTF(encrypted);

        socket.close();



    }
}
