package com.x.jdk8.io;

import com.x.doraemon.Printer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author AD
 * @date 2022/4/19 19:38
 */
public class IODemo {

    private static final String txtPath = "io-test.txt";

    private static void readTxt() {
        try (InputStream in = IODemo.class.getClassLoader().getResourceAsStream(txtPath);) {
            int available = in.available();
            byte[] bytes = new byte[available];
            in.read(bytes);
            String txt = new String(bytes);
            System.out.println(txt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fileOutputStream() throws Exception {
        // 这个写法时写到target文件夹中的文件
        URL txtURL = IODemo.class.getClassLoader().getResource(txtPath);
        File file = new File(txtURL.getFile());
        // File file = new File("/" + txtPath);
        // append:true表示不删除文件,将内容进行追加;反之则删除文件
        try (FileOutputStream out = new FileOutputStream(file, true);
             // 启用自动冲刷机制
             PrintWriter writer = new PrintWriter(out, true);) {
            String content = "\n这是美丽的一天.";
            writer.write(content);
            writer.flush();
        }
    }

    private static void inputStreamReader() throws IOException {
        Reader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        int a = -1;
        while ((a = reader.read()) != -1) {
            System.out.println(a + " ");
        }
    }

    private static void getCharset() {
        Charset charset = Charset.defaultCharset();
        System.out.println(charset);
        System.out.println("----------------");
        SortedMap<String, Charset> charsets = Charset.availableCharsets();
        charsets.forEach((k, v) -> {
            System.out.println(k + "  " + v);
        });
    }

    private static void dataOutput() throws Exception {
        URL txtURL = IODemo.class.getClassLoader().getResource(txtPath);
        File file = new File(txtURL.getFile());
        try (FileOutputStream out = new FileOutputStream(file);
             DataOutputStream writer = new DataOutputStream(out);) {
            writer.writeByte(1);
            writer.writeShort(32766);
            writer.writeInt(12345678);
            writer.writeBoolean(false);
            writer.writeDouble(1.234);
            writer.writeChar('\n');
            int c = Character.MIN_VALUE;
            while (c <= Character.MAX_VALUE) {
                writer.writeChar(c++);
            }
            writer.writeUTF("\n这是美丽的一天\n");
            writer.flush();
        }

        try (FileInputStream in = new FileInputStream(file);
             DataInputStream reader = new DataInputStream(in);) {
            byte b = reader.readByte();
            short s = reader.readShort();
            int i = reader.readInt();
            boolean bool = reader.readBoolean();
            double d = reader.readDouble();
            char c = reader.readChar();
            System.out.printf("%d %d %d %s %f", b, s, i, bool, d);
            System.out.println(c);
            int cc = Character.MIN_VALUE;
            while (cc++ <= Character.MAX_VALUE) {
                char c1 = reader.readChar();
                System.out.print(c1);
                System.out.print(" ");
                if (cc % 100 == 0) {
                    System.out.println();
                }
            }
            String str = reader.readUTF();
            System.out.println(str);

        }
    }

    /**
     * randomAccessFile可读写文件的任何位置,适用于每行大小都一致的文件
     * @throws Exception
     */
    private static void randomAccessFile() throws Exception {
        URL txtURL = IODemo.class.getClassLoader().getResource(txtPath);
         /*
             “r”表示只读模式；
             “rw”表示读/写模式；
             “rws”表示每次更新时，都对数据和元数据的写磁盘操作进行同步的读/写模式；
             “rwd”表示每次更新时，只对数据的写磁盘操作进行同步的读/写模式
         */
        try (RandomAccessFile file = new RandomAccessFile(txtURL.getFile(), "rws");) {
            String line = "this is a sunday.";
            for (int i = 1; i < 10; i++) {
                file.writeUTF(i + ". " + line + "\n");
            }
            long length = file.length();
            long rowLength = length / 9;
            Printer printer = new Printer();
            printer.add("total", length);
            printer.add("rowLength", rowLength);
            printer.print();

            System.out.println("before set pointer:" + file.getFilePointer());
            file.seek(3 * rowLength);
            System.out.println("after set pointer:" + file.getFilePointer());
            String s = file.readLine();
            System.out.println(s + "  " + s.length());
            file.writeUTF("【The row will be modified】");
        }
    }

    private static void zipInputStream() throws Exception {
        String zipFile = "";
        try (FileInputStream fin = new FileInputStream(zipFile);
             ZipInputStream zin = new ZipInputStream(fin)) {
            ZipEntry entry = null;
            while ((entry = zin.getNextEntry()) != null) {
                // TODO read entry
                zin.closeEntry();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        randomAccessFile();
    }
}
