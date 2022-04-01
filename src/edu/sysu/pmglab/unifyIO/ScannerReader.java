package edu.sysu.pmglab.unifyIO;

import java.io.*;

/**
 * @Data        :2021/02/22
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :键盘传入数据的快速实现
 */

public class ScannerReader {

    static ScannerReader in;
    static PrintWriter out;

    private final BufferedReader br;

    public ScannerReader(InputStream stream) {
        this.br = new BufferedReader(new InputStreamReader(stream));
    }

    public int nextInt() throws IOException {
        int c = br.read();
        while (c <= 32) {
            c = br.read();
        }
        boolean negative = false;
        if (c == '-') {
            negative = true;
            c = br.read();
        }
        int x = 0;
        while (c > 32) {
            x = x * 10 + c - '0';
            c = br.read();
        }
        return negative ? -x : x;
    }

    public long nextLong() throws IOException {
        int c = br.read();
        while (c <= 32) {
            c = br.read();
        }
        boolean negative = false;
        if (c == '-') {
            negative = true;
            c = br.read();
        }
        long x = 0;
        while (c > 32) {
            x = x * 10 + c - '0';
            c = br.read();
        }
        return negative ? -x : x;
    }

    public String next() throws IOException {
        int c = br.read();
        while (c <= 32) {
            c = br.read();
        }
        StringBuilder sb = new StringBuilder();
        while (c > 32) {
            sb.append((char) c);
            c = br.read();
        }
        return sb.toString();
    }

    public double nextDouble() throws IOException {
        return Double.parseDouble(next());
    }

    public void close() throws IOException {
        this.br.close();
    }

    public static void main(String[] args) throws IOException {
        in = new ScannerReader(System.in);
        out = new PrintWriter(System.out);
        /*
         *
         * 這裡補充你的實際程式碼   讀取輸入採用   in.nextInt();的形式
         *
         * */
        out.close();
    }
}
