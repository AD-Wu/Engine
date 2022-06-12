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
import java.util.ArrayList;
import java.util.List;

/**
 * @author AD
 * @date 2022/6/12 15:25
 */
public class Test {

    public static void main(String[] args) throws Exception {
        byte[] msg = "hello world".getBytes(StandardCharsets.UTF_8);
        byte[] desPwd = "12345678".getBytes();
        byte[] tripleDesPwd = "12345678|2345678|2345678".getBytes();
        byte[] aesPwd = "12345678|2345678".getBytes();
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

    private static void testDES(byte[] msg, byte[] pwd) throws Exception {
        List<ICipher> ciphers = new ArrayList<>();
        for (Encrypt.Mode mode : Encrypt.Mode.values()) {
            ICipher cipher = new DES(pwd, mode);
            ciphers.add(cipher);
        }
        testCipher(msg, ciphers);
    }

    private static void testDES3(byte[] msg, byte[] pwd) throws Exception {
        List<ICipher> ciphers = new ArrayList<>();
        for (Encrypt.Mode mode : Encrypt.Mode.values()) {
            ICipher cipher = new DES3(pwd, mode);
            ciphers.add(cipher);
        }
        testCipher(msg, ciphers);
    }

    private static void testAES(byte[] msg, byte[] pwd) throws Exception {
        List<ICipher> ciphers = new ArrayList<>();
        for (Encrypt.Mode mode : Encrypt.Mode.values()) {
            ICipher cipher = new AES(pwd, mode);
            ciphers.add(cipher);
        }
        testCipher(msg, ciphers);
    }

    private static void testCipher(byte[] msg, List<ICipher> ciphers) throws Exception {
        Printer printer = new Printer();
        printer.add("mode", "algorithm", "encrypt");
        for (ICipher cipher : ciphers) {
            byte[] encrypt = cipher.encrypt(msg);
            // byte[] decrypt = cipher.decrypt(encrypt);
            String enHex = Converts.bytesToHex(encrypt);
            // String deHex = Converts.bytesToHex(decrypt);
            printer.add(cipher.mode(), cipher.algorithm(), enHex);
        }
        printer.print();


    }
}
