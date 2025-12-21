import java.net.*;
import java.io.*;


public class Hill_Cipher_Client {
    static int[][] inverseKey = {
        {15, 17},
        {20, 9}
    };
    static String decrypt(String text)
    {
        StringBuilder plain = new StringBuilder();

        for(int i=0;i<text.length();i+=2)
        {
            int vector[] = {
                text.charAt(i) - 'A',
                text.charAt(i + 1) - 'A'
            };
            for(int j=0;j<2;j++)
            {
                int value = (inverseKey[j][0] * vector[0] +
                             inverseKey[j][1] * vector[1]) % 26;
                    plain.append((char) (value + 'A')); 
            }
        }

        return plain.toString();
    }
    public static void main(String[] args) throws Exception{
        Socket s = new Socket("localhost", 5001);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream()));

        String encrypted = in.readLine();
        System.out.println("Received encrypted text: " + encrypted);
        System.out.println("Decrypted text: " + decrypt(encrypted));

            in.close();
        s.close();
    }
}
