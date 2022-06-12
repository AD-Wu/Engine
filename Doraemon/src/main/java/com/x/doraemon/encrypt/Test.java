package com.x.doraemon.encrypt;

import com.x.doraemon.Converts;
import com.x.doraemon.Printer;
import com.x.doraemon.encrypt.core.Encrypt;
import com.x.doraemon.encrypt.core.ICipher;
import com.x.doraemon.encrypt.des.AES;
import com.x.doraemon.encrypt.des.DES;
import com.x.doraemon.encrypt.des.DES3;
import com.x.doraemon.encrypt.digest.Digests;
import java.nio.charset.StandardCharsets;

/**
 * @author AD
 * @date 2022/6/12 15:25
 */
public class Test {

    public static void main(String[] args)throws Exception {
        byte[] msg = "hello world".getBytes(StandardCharsets.UTF_8);
        String desPwd = "DES-1234";
        String tripleDesPwd = "DES-12345678DES-12345678";
        String aesPwd = "AES-12345678AES-12345678";
        testDigest(msg);
        System.out.println("---------------------------------");
        testDES(msg, desPwd);
        System.out.println("---------------------------------");
        testDES3(msg, tripleDesPwd);
        System.out.println("---------------------------------");
        testAES(msg, aesPwd);
        System.out.println("---------------------------------");
    }

    private static void testDigest(byte[] msg) {
        Printer printer = new Printer();
        for (Digests helper : Digests.values()) {
            byte[] digest = helper.digest(msg);
            String hex = Converts.bytesToHex(digest);
            printer.add(helper, helper.algorithm().getDigestLength(), hex);
        }
        printer.print();
    }

    private static void testDES(byte[] msg, String pwd) throws Exception {
        String msgHex = Converts.bytesToHex(msg);
        System.out.println(msgHex);

        Printer printer = new Printer();
        printer.add("mode", "algorithm", "encrypt", "decrypt");
        for (Encrypt.Mode mode : Encrypt.Mode.values()) {
            ICipher des = new DES(pwd.getBytes(), mode);
            byte[] encrypt = des.encrypt(msg);
            byte[] decrypt = des.decrypt(encrypt);
            String enHex = Converts.bytesToHex(encrypt);
            String deHex = Converts.bytesToHex(decrypt);
            printer.add(mode, des.algorithm(), enHex, deHex);
        }
        printer.print();
    }

    private static void testDES3(byte[] msg, String pwd) throws Exception {
        String msgHex = Converts.bytesToHex(msg);
        System.out.println(msgHex);

        Printer printer = new Printer();
        printer.add("mode", "algorithm", "encrypt", "decrypt");
        for (Encrypt.Mode mode : Encrypt.Mode.values()) {
            ICipher des3 = new DES3(pwd.getBytes(), mode);
            byte[] encrypt = des3.encrypt(msg);
            byte[] decrypt = des3.decrypt(encrypt);
            String enHex = Converts.bytesToHex(encrypt);
            String deHex = Converts.bytesToHex(decrypt);
            printer.add(mode, des3.algorithm(), enHex, deHex);
        }
        printer.print();
    }

    private static void testAES(byte[] msg, String pwd) throws Exception {
        String msgHex = Converts.bytesToHex(msg);
        System.out.println(msgHex);

        Printer printer = new Printer();
        printer.add("mode", "algorithm", "encrypt", "decrypt");
        for (Encrypt.Mode mode : Encrypt.Mode.values()) {
            ICipher des3 = new AES(pwd.getBytes(),mode);
            byte[] encrypt = des3.encrypt(msg);
            byte[] decrypt = des3.decrypt(encrypt);
            String enHex = Converts.bytesToHex(encrypt);
            String deHex = Converts.bytesToHex(decrypt);
            printer.add(mode, des3.algorithm(), enHex, deHex);
        }
        printer.print();
    }
}
