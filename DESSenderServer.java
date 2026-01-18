import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Scanner;

public class DESSenderServer {
    
    public static void main(String[] args) {
        try {
            // 1. Input plaintext & key
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter plaintext: ");
            String plaintext = sc.nextLine();

            System.out.print("Enter 8-char key: ");
            String key = sc.nextLine();
            sc.close();

            if (key.length() != 8) {
                System.out.println("Key must be exactly 8 characters (64 bits).");
                return;
            }

            // 2. DES Setup
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);

            // 3. Encrypt
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

            System.out.println("Cipher Text (Base64): " + encryptedBase64);
            System.out.println("Waiting for receiver/client to connect...");

            // 4. Socket Send
            ServerSocket server = new ServerSocket(5001);
            Socket socket = server.accept();
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(encryptedBase64);
            out.writeUTF(key); // send key too (symmetric, for demo)

            System.out.println("Cipher + Key sent to receiver!");

            socket.close();
            server.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
