import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RowColumnReceiver {

    // Decryption
    public static String decrypt(String cipher, int columns) {
        cipher = cipher.replaceAll("\\s+", "");
        int length = cipher.length();
        int rows = (int) Math.ceil((double) length / columns);

        char[][] matrix = new char[rows][columns];

        int idx = 0;

        // Fill column-wise (reverse of encryption)
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                if (idx < length) {
                    matrix[r][c] = cipher.charAt(idx++);
                }
            }
        }

        // Read row-wise
        StringBuilder plain = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                plain.append(matrix[r][c]);
            }
        }

        return plain.toString();
    }

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(6000);
            System.out.println("Row-Column Receiver waiting...");

            Socket socket = server.accept();
            System.out.println("Sender connected!");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            String data = in.readUTF();
            System.out.println("Received: " + data);

            String[] parts = data.split(":");
            int columns = Integer.parseInt(parts[0]);
            String cipher = parts[1];

            String plain = decrypt(cipher, columns);
            System.out.println("Decrypted plaintext: " + plain);

            socket.close();
            server.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
