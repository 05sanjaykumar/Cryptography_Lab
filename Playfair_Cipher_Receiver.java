import java.net.*;
import java.io.*;
import java.util.*;

public class Playfair_Cipher_Receiver {

    static char[][] matrix = new char[5][5];

    static void generateMatrix(String key) {
        key = key.toUpperCase().replace("J", "I");
        LinkedHashSet<Character> set = new LinkedHashSet<>();

        for (char c : key.toCharArray())
            if (c >= 'A' && c <= 'Z') set.add(c);

        for (char c = 'A'; c <= 'Z'; c++)
            if (c != 'J') set.add(c);

        Iterator<Character> it = set.iterator();
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                matrix[i][j] = it.next();
    }

    static int[] find(char c) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (matrix[i][j] == c) return new int[]{i, j};
        return null;
    }

    static String decrypt(String text) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i += 2) {
            char a = text.charAt(i);
            char b = text.charAt(i + 1);

            int[] p1 = find(a);
            int[] p2 = find(b);

            if (p1[0] == p2[0]) {
                result.append(matrix[p1[0]][(p1[1] + 4) % 5]);
                result.append(matrix[p2[0]][(p2[1] + 4) % 5]);
            } else if (p1[1] == p2[1]) {
                result.append(matrix[(p1[0] + 4) % 5][p1[1]]);
                result.append(matrix[(p2[0] + 4) % 5][p2[1]]);
            } else {
                result.append(matrix[p1[0]][p2[1]]);
                result.append(matrix[p2[0]][p1[1]]);
            }
        }
        return result.toString();
    }

    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 6000);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream()));

        String key = "SECURITY";
        generateMatrix(key);

        String encrypted = in.readLine();
        System.out.println("Encrypted text: " + encrypted);
        System.out.println("Decrypted text: " + decrypt(encrypted));

        s.close();
    }
}
