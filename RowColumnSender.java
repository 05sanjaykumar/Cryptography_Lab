import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class RowColumnSender {

    // Encryption
    public static String encrypt(String text, int columns) {
        text = text.replaceAll("\\s+", "");
        int length = text.length();
        int rows = (int) Math.ceil((double) length / columns);

        char[][] matrix = new char[rows][columns];

        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (idx < length) {
                    matrix[r][c] = text.charAt(idx++);
                } else {
                    matrix[r][c] = 'X'; // padding
                }
            }
        }

        StringBuilder cipher = new StringBuilder();
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                cipher.append(matrix[r][c]);
            }
        }

        return cipher.toString();
    }

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter plaintext: ");
            String text = sc.nextLine();

            System.out.print("Enter number of columns: ");
            int columns = sc.nextInt();

            String cipher = encrypt(text, columns);
            System.out.println("Cipher generated: " + cipher);

            Socket socket = new Socket("localhost", 6000);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(columns + ":" + cipher);

            System.out.println("Cipher sent to receiver.");

            socket.close();
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
