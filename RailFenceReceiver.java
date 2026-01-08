import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RailFenceReceiver {

    // Decryption method
    public static String decrypt(String cipher, int rails) {

        if (rails <= 1) return cipher;

        boolean[][] mark = new boolean[rails][cipher.length()];

        int row = 0;
        boolean down = true;

        // First pass: mark zig-zag pattern
        for (int i = 0; i < cipher.length(); i++) {
            mark[row][i] = true;

            if (down) {
                row++;
                if (row == rails - 1) down = false;
            } else {
                row--;
                if (row == 0) down = true;
            }
        }

        char[] result = new char[cipher.length()];
        int index = 0;

        for (int r = 0; r < rails; r++) {
            for (int c = 0; c < cipher.length(); c++) {
                if (mark[r][c]) {
                    result[c] = cipher.charAt(index++);
                }
            }
        }

        StringBuilder plain = new StringBuilder();
        row = 0;
        down = true;

        for (int i = 0; i < cipher.length(); i++) {
            plain.append(result[i]);

            if (down) {
                row++;
                if (row == rails - 1) down = false;
            } else {
                row--;
                if (row == 0) down = true;
            }
        }

        return plain.toString();
    }

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5001);
            System.out.println("Receiver waiting for sender...");

            Socket socket = server.accept();
            System.out.println("Sender connected!");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            String data = in.readUTF();
            System.out.println("Received: " + data);

            String[] parts = data.split(":");
            int rails = Integer.parseInt(parts[0]);
            String cipher = parts[1];

            String plain = decrypt(cipher, rails);
            System.out.println("Decrypted Plaintext: " + plain);

            socket.close();
            server.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
