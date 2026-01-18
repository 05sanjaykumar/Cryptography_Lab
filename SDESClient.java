import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SDESClient {

    // ---- S-DES Helper Methods ----

    public static int[] P10 = {3,5,2,7,4,10,1,9,8,6};
    public static int[] P8  = {6,3,7,4,8,5,10,9};
    public static int[] P4  = {2,4,3,1};
    public static int[] IP  = {2,6,3,1,4,8,5,7};
    public static int[] IP1 = {4,1,3,5,7,2,8,6};
    public static int[] EP  = {4,1,2,3,2,3,4,1};

    static int[][] S0 = {
        {1,0,3,2},
        {3,2,1,0},
        {0,2,1,3},
        {3,1,3,2}
    };

    static int[][] S1 = {
        {0,1,2,3},
        {2,0,1,3},
        {3,0,1,0},
        {2,1,0,3}
    };

    public static int permute(int bits, int[] p, int n) {
        int out = 0;
        for (int i = 0; i < p.length; i++) {
            out <<= 1;
            out |= (bits >> (n - p[i])) & 1;
        }
        return out;
    }

    public static int ls(int x, int n) {
        return ((x << n) & 0x1F) | (x >> (5 - n));
    }

    public static int[] genKeys(int key) {
        int p10 = permute(key, P10, 10);
        int left = (p10 >> 5) & 0x1F;
        int right = p10 & 0x1F;

        left = ls(left, 1);
        right = ls(right, 1);
        int k1 = permute((left << 5) | right, P8, 10);

        left = ls(left, 2);
        right = ls(right, 2);
        int k2 = permute((left << 5) | right, P8, 10);

        return new int[]{k1, k2};
    }

    public static int f(int right, int key) {
        int ep = permute(right, EP, 4);
        int xor = ep ^ key;

        int left = (xor >> 4) & 0xF;
        int right4 = xor & 0xF;

        int r1 = S0[((left & 0x8)>>2)|(left&1)][(left&0x6)>>1];
        int r2 = S1[((right4&0x8)>>2)|(right4&1)][(right4&0x6)>>1];

        int out = (r1 << 2) | r2;
        return permute(out, P4, 4);
    }

    public static int encrypt(int pt, int k1, int k2) {
        int ip = permute(pt, IP, 8);
        int left = (ip >> 4) & 0xF;
        int right = ip & 0xF;

        int temp = f(right, k1) ^ left;
        int swapped = (right << 4) | temp;

        left = (swapped >> 4) & 0xF;
        right = swapped & 0xF;

        int out = (f(right, k2) ^ left) << 4 | right;
        return permute(out, IP1, 8);
    }

    // ---- CLIENT SIDE MAIN ----

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter 8-bit plaintext: ");
            int pt = Integer.parseInt(sc.next(), 2);

            System.out.print("Enter 10-bit key: ");
            int key = Integer.parseInt(sc.next(), 2);

            int[] keys = genKeys(key);
            int ct = encrypt(pt, keys[0], keys[1]);

            System.out.println("Cipher: " + String.format("%8s", Integer.toBinaryString(ct)).replace(' ', '0'));

            Socket socket = new Socket("localhost", 6000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(ct + "," + key);

            socket.close();
            sc.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
