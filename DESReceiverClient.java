import java.io.DataInputStream;
import java.net.Socket;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DESReceiverClient {
    public static void main(String[] args) {
        try {
            // 1. Connect to sender (server)
            Socket socket = new Socket("localhost", 5001);
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // 2. Receive cipher + key
            String encryptedBase64 = in.readUTF();
            String key = in.readUTF();

            System.out.println("Received Cipher: " + encryptedBase64);
            System.out.println("Received Key: " + key);

            // 3. DES Setup for decrypt
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);

            // 4. Decrypt
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedBase64));
            String decryptedText = new String(decryptedBytes);

            System.out.println("Decrypted Text: " + decryptedText);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
