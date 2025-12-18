import java.io.PrintWriter;
import java.net.*;
import java.util.*;

public class Playfair_Cipher_Sender {

    static char[][] matrix = new char[5][5];

    static void generateMatrix(String key)
    {
        key = key.toUpperCase().replace("J", "I");
        LinkedHashSet<Character> set = new LinkedHashSet<>();

        for(char c: key.toCharArray())
        {
            if(c>='A' && c<='Z') set.add(c);
        }

        for(char i = 'A'; i<='Z'; i++)
        {
            if(i!='J') set.add(i);
        }

        Iterator<Character> it = set.iterator();
        for(int i=0;i<5;i++)
        {
            for(int j=0;j<5;j++)
            {
                matrix[i][j] = it.next();
            }
        }
    }

    static int[] find(char c)
    {
        for(int i=0;i<5;i++)
        {
            for(int j=0;j<5;j++)
            {
                if(c == matrix[i][j])
                {
                    return new int[] {i,j};
                }
            }
        }

        return null;
    }

    static String encrypt(String text)
    {
        text = text.toUpperCase().replace("J", "I").replaceAll("[^A-Z]", "");
        if (text.length() % 2 != 0) text += "X";


        StringBuilder result = new StringBuilder();

        for(int i=0;i<text.length();i+=2)
        {
            char a = text.charAt(i);
            char b = text.charAt(i+1);

            int[] p1 = find(a);
            int[] p2 = find(b);
            
            if(p1[0] == p2[0])
            {
                result.append(matrix[p1[0]][(p1[1] + 1) % 5]);
                result.append(matrix[p2[0]][(p2[1] + 1) % 5]);
            }
            else if(p1[1] == p2[1])
            {
                result.append(matrix[(p1[0]+1)%5][p1[1]]);
                result.append(matrix[(p2[0] + 1) % 5][p2[1]]);
            }
            else
            {
                result.append(matrix[p1[0]][p2[1]]);
                result.append(matrix[p2[0]][p1[1]]);
            }
        }

        return result.toString();


    }
    public static void main(String[] args)throws Exception {
        ServerSocket ss = new ServerSocket(6000);
        Socket s = ss.accept();

        Scanner sc = new Scanner(System.in);
    
        String key = "SECURITY";
        generateMatrix(key);

        System.out.println("Enter the value");
        String text = sc.nextLine();

        String encrypted = encrypt(text);
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        out.println(encrypted);

        ss.close();
        s.close();
        sc.close();


    }
}
