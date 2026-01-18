import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


public class AES_LZE_client {
    // AES Decrypt
    public static String aesDecrypt(String encrypted, String key) throws Exception {
        SecretKeySpec sk = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sk);
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        return new String(cipher.doFinal(decoded));
    }

    // LZW Decompression
    public static String lzwDecompress(List<Integer> compressed) {
        Map<Integer, String> dict = new HashMap<>();
        for (int i = 0; i < 256; i++) dict.put(i, "" + (char) i);

        String w = "" + (char) (int) compressed.remove(0);
        StringBuilder result = new StringBuilder(w);
        int dictSize = 256;

        for (int k : compressed) {
            String entry;
            if (dict.containsKey(k)) entry = dict.get(k);
            else if (k == dictSize) entry = w + w.charAt(0);
            else throw new RuntimeException("Bad compressed k: " + k);

            result.append(entry);
            dict.put(dictSize++, w + entry.charAt(0));
            w = entry;
        }
        return result.toString();
    }

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(9000);
            System.out.println("Receiver waiting...");

            Socket socket = server.accept();
            System.out.println("Client connected!");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            String encrypted = in.readUTF();
            System.out.println("Encrypted Received: " + encrypted);

            // 1. Decrypt AES
            String key = "1234567890123456";
            String compressedStr = aesDecrypt(encrypted, key);
            System.out.println("After Decryption: " + compressedStr);

            // 2. Convert back to list<int>
            String[] parts = compressedStr.split(",");
            List<Integer> compressed = new ArrayList<>();
            for (String p : parts) if (!p.isEmpty()) compressed.add(Integer.parseInt(p));

            // 3. LZW Decompress
            String decompressed = lzwDecompress(compressed);
            System.out.println("Final Output (Decompressed): " + decompressed);

            socket.close();
            server.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
