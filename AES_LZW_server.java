import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class AES_LZW_server {
    // LZW Compression
    public static List<Integer> lzwCompress(String input) {
        Map<String, Integer> dict = new HashMap<>();
        for (int i = 0; i < 256; i++) dict.put("" + (char) i, i);
        String w = "";
        List<Integer> result = new ArrayList<>();
        int dictSize = 256;

        for (char c : input.toCharArray()) {
            String wc = w + c;
            if (dict.containsKey(wc)) w = wc;
            else {
                result.add(dict.get(w));
                dict.put(wc, dictSize++);
                w = "" + c;
            }
        }
        if (!w.equals("")) result.add(dict.get(w));
        return result;
    }

    // AES Encrypt
    public static String aesEncrypt(String data, String key) throws Exception {
        SecretKeySpec sk = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sk);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter text: ");
            String text = sc.nextLine();

            // 1. LZW Compression
            List<Integer> compressed = lzwCompress(text);
            StringBuilder sb = new StringBuilder();
            for (int v : compressed) sb.append(v).append(",");
            String compressedStr = sb.toString();
            System.out.println("Compressed: " + compressedStr);

            // 2. AES Encryption
            String key = "1234567890123456"; // 16 bytes
            String encrypted = aesEncrypt(compressedStr, key);
            System.out.println("Encrypted: " + encrypted);

            // 3. Socket Send
            Socket socket = new Socket("localhost", 9000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(encrypted);
            System.out.println("Data sent to receiver!");

            socket.close();
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
