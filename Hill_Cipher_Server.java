import java.net.*;
import java.io.*;
import java.util.*;

public class Hill_Cipher_Server {

    static int[][] key = {
        {3,3},
        {2,5}
    };

    static String encrypt(String text)
    {
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        if(text.length()%2!=0)
        {
            text+="X";
        }
        StringBuilder cipher = new StringBuilder();

        for(int i=0;i<text.length();i+=2)
        {
            int[] vector = {
                text.charAt(i)- 'A',
                text.charAt(i+1) - 'A'
            };
            for(int j=0;j<2;j++)
            {
                int value = (key[j][0]*vector[0])+(key[j][1]*vector[1])%26;
                cipher.append((char) (value + 'A'));
            }   
        }

        return cipher.toString();

    }
    public static void main(String[] args)  throws Exception
    {
        ServerSocket ss = new ServerSocket(5001);
        Socket s = ss.accept();

        Scanner sc = new Scanner(System.in);
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        System.out.print("Enter plaintext: ");
        String plaintext = sc.nextLine();

        String encrypted = encrypt(plaintext);
        System.out.println("Encrypted text: " + encrypted);

        out.println(encrypted);

        ss.close();
        sc.close();



    }
}
