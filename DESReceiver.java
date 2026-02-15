import java.io.*;
import java.net.*;
import java.util.*;

public class DESReceiver {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 12345;
    
    // Permutation tables
    private static final int[] PC1 = {
        57,49,41,33,25,17,9,1,58,50,42,34,26,18,10,2,59,51,43,35,27,19,11,3,60,52,44,36,
        63,55,47,39,31,23,15,7,62,54,46,38,30,22,14,6,61,53,45,37,29,21,13,5,28,20,12,4
    };
    
    private static final int[] PC2 = {
        14,17,11,24,1,5,3,28,15,6,21,10,23,19,12,4,26,8,16,7,27,20,13,2,
        41,52,31,37,47,55,30,40,51,45,33,48,44,49,39,56,34,53,46,42,50,36,29,32
    };
    
    private static final int[] EP = {
        32,1,2,3,4,5,4,5,6,7,8,9,8,9,10,11,12,13,12,13,14,15,16,17,16,17,18,19,20,21,20,21,22,23,24,
        25,24,25,26,27,28,29,28,29,30,31,32,1
    };
    
    private static final int[] P4 = {
        16,7,20,21,29,12,28,17,1,15,23,26,5,18,31,10,2,8,24,14,32,27,3,9,19,13,30,6,22,11,4,25
    };
    
    private static final int[] IP = {
        58,50,42,34,26,18,10,2,60,52,44,36,28,20,12,4,62,54,46,38,30,22,14,6,64,56,48,40,32,24,16,
        8,57,49,41,33,25,17,9,1,59,51,43,35,27,19,11,3,61,53,45,37,29,21,13,5,63,55,47,39,31,23,15,7
    };
    
    private static final int[] IP_INV = {
        40,8,48,16,56,24,64,32,39,7,47,15,55,23,63,31,38,6,46,14,54,22,62,30,37,5,45,13,53,21,61,
        29,36,4,44,12,52,20,60,28,35,3,43,11,51,19,59,27,34,2,42,10,50,18,58,26,33,1,41,9,49,17,57,25
    };
    
    private static final int[] SHIFTS = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
    
    private static final int[][][] S_BOXES = {
        {{14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},{0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
         {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},{15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}},
        {{15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},{3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
         {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},{13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}},
        {{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},{13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
         {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},{1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}},
        {{7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},{13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
         {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},{3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}},
        {{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},{14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
         {4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},{11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}},
        {{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},{10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
         {9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},{4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}},
        {{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},{13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
         {1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},{6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}},
        {{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},{1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
         {7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},{2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}}
    };
    
    private static String permute(String bits, int[] table) {
        StringBuilder result = new StringBuilder();
        for (int i : table) {
            result.append(bits.charAt(i - 1));
        }
        return result.toString();
    }
    
    private static String leftShift(String bits, int n) {
        return bits.substring(n) + bits.substring(0, n);
    }
    
    private static List<String> generateSubkeys(String key64) {
        String p10 = permute(key64, PC1);
        String left = p10.substring(0, 28);
        String right = p10.substring(28);
        
        List<String> subkeys = new ArrayList<>();
        for (int shift : SHIFTS) {
            left = leftShift(left, shift);
            right = leftShift(right, shift);
            subkeys.add(permute(left + right, PC2));
        }
        return subkeys;
    }
    
    private static String[] fk(String left4, String right4, String subkey) {
        String expanded = permute(right4, EP);
        StringBuilder xor = new StringBuilder();
        for (int i = 0; i < 48; i++) {
            xor.append(expanded.charAt(i) == subkey.charAt(i) ? '0' : '1');
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            String bits6 = xor.substring(i * 6, (i + 1) * 6);
            int row = Integer.parseInt("" + bits6.charAt(0) + bits6.charAt(5), 2);
            int col = Integer.parseInt(bits6.substring(1, 5), 2);
            int val = S_BOXES[i][row][col];
            result.append(String.format("%4s", Integer.toBinaryString(val)).replace(' ', '0'));
        }
        
        String p4Out = permute(result.toString(), P4);
        StringBuilder newRight = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            newRight.append(p4Out.charAt(i) == left4.charAt(i) ? '0' : '1');
        }
        
        return new String[]{right4, newRight.toString()};
    }
    
    private static Map<String, String> decryptByte(String cipher64, List<String> subkeys) {
        Map<String, String> result = new LinkedHashMap<>();
        String ip = permute(cipher64, IP);
        result.put("IP", ip);
        
        String L = ip.substring(0, 32);
        String R = ip.substring(32);
        
        for (int round = 0; round < 16; round++) {
            String[] temp = fk(L, R, subkeys.get(15 - round));
            L = temp[0];
            R = temp[1];
            result.put("After Round " + (round + 1), L + R);
        }
        
        String preoutput = R + L;
        String plain = permute(preoutput, IP_INV);
        result.put("plain", plain);
        
        return result;
    }
    
    private static boolean validateBits(String s, int length) {
        if (s.length() != length) return false;
        for (char c : s.toCharArray()) {
            if (c != '0' && c != '1') return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {
            
            String cipherText = in.readLine();
            System.out.println("Received Cipher Text (bits): " + cipherText);
            
            System.out.print("Enter the 64-bit Key: ");
            String key = scanner.nextLine();
            
            if (!validateBits(key, 64)) {
                System.out.println("Invalid key. Expected 64-bit binary.");
                return;
            }
            
            List<String> subkeys = generateSubkeys(key);
            System.out.println("Key (64-bit): " + key);
            System.out.println("Subkeys:");
            for (int i = 0; i < subkeys.size(); i++) {
                System.out.println("K" + (i + 1) + " = " + subkeys.get(i));
            }
            
            if (cipherText.length() % 64 != 0) {
                System.out.println("Warning: received length is not a multiple of 64.");
            }
            
            List<String> blocks = new ArrayList<>();
            for (int i = 0; i < cipherText.length(); i += 64) {
                blocks.add(cipherText.substring(i, Math.min(i + 64, cipherText.length())));
            }
            
            StringBuilder plainText = new StringBuilder();
            for (int idx = 0; idx < blocks.size(); idx++) {
                Map<String, String> res = decryptByte(blocks.get(idx), subkeys);
                System.out.println("Block " + (idx + 1) + ":");
                System.out.println("  IP: " + res.get("IP"));
                for (int round = 1; round <= 16; round++) {
                    System.out.println("  After Round " + round + ": " + res.get("After Round " + round));
                }
                System.out.println("  Decrypted (bits): " + res.get("plain"));
                
                String pbits = res.get("plain");
                for (int i = 0; i < pbits.length(); i += 8) {
                    String byteBits = pbits.substring(i, Math.min(i + 8, pbits.length()));
                    if (byteBits.length() == 8 && byteBits.matches("[01]+")) {
                        int val = Integer.parseInt(byteBits, 2);
                        if (val > 0) {
                            plainText.append((char) val);
                        }
                    }
                }
            }
            
            System.out.println("Decrypted Plain Text: " + plainText);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}