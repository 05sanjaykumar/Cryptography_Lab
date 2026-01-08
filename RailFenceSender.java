import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class RailFenceSender {

    // Encryption method
    public static String encrypt(String text, int rails) {

        if (rails <= 1) return text;

        StringBuilder[] sb = new StringBuilder[rails];
        for (int i = 0; i < rails; i++) {
            sb[i] = new StringBuilder();
        }

        int row = 0;
        boolean down = true;

        for (char c : text.toCharArray()) {
            sb[row].append(c);

            if (down) {
                row++;
                if (row == rails - 1) down = false;
            } else {
                row--;
                if (row == 0) down = true;
            }
        }

        StringBuilder cipher = new StringBuilder();
        for (StringBuilder sbr : sb) cipher.append(sbr);

        return cipher.toString();
    }

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter plaintext: ");
            String text = sc.nextLine();

            System.out.print("Enter rails: ");
            int rails = sc.nextInt();

            String cipher = encrypt(text, rails);
            System.out.println("Cipher generated: " + cipher);

            Socket socket = new Socket("localhost", 5001);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(rails + ":" + cipher);

            System.out.println("Cipher sent to receiver.");

            socket.close();
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
