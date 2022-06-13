package com.x.doraemon.encrypt;

import com.x.doraemon.Converts;
import com.x.doraemon.Printer;
import com.x.doraemon.Strings;
import com.x.doraemon.encrypt.aes.AES;
import com.x.doraemon.encrypt.core.Encrypt;
import com.x.doraemon.encrypt.core.ICipher;
import com.x.doraemon.encrypt.core.ISymmetryCipher;
import com.x.doraemon.encrypt.des.DES;
import com.x.doraemon.encrypt.des.DES3;
import com.x.doraemon.encrypt.digest.Digests;
import com.x.doraemon.encrypt.rsa.RSA;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author AD
 * @date 2022/6/12 15:25
 */
public class Test {
    
    public static void main(String[] args) throws Exception {
        byte[] msg = "hello world".getBytes(StandardCharsets.UTF_8);
        System.out.println(msg.length);
        byte[] desPwd = "12345678".getBytes();
        byte[] tripleDesPwd = "12345678|2345678|2345678".getBytes();
        byte[] aesPwd = "12345678|2345678".getBytes();
        testRSA(msg);
        System.out.println("---------------------------------");
        testDigest(msg);
        System.out.println("---------------------------------");
        testDES(msg, desPwd);
        System.out.println("---------------------------------");
        testDES3(msg, tripleDesPwd);
        System.out.println("---------------------------------");
        testAES(msg, aesPwd);
        System.out.println("---------------------------------");
        testPassword(128);
        System.out.println("---------------------------------");
        testPassword(192);
        System.out.println("---------------------------------");
        testPassword(256);
        System.out.println("---------------------------------");
    }
    
    private static void testRSA(byte[] msg) throws Exception {
        String ctx = Strings.format("明文: {}, Hex: {}", new String(msg), Converts.bytesToHex(msg));
        System.out.println(ctx);
        Printer printer = new Printer();
        printer.add("Algorithm", "Encrypt", "Decrypt", "Plain");
        ICipher rsa = new RSA(512);
        byte[] encrypt = rsa.encrypt(msg);
        byte[] decrypt = rsa.decrypt(encrypt);
        String enHex = Converts.bytesToHex(encrypt);
        String deHex = Converts.bytesToHex(decrypt);
        String plain = new String(decrypt);
        printer.add(rsa.algorithm(), enHex, deHex, plain);
        
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
        List<ISymmetryCipher> ciphers = new ArrayList<>();
        for (Encrypt.Mode mode : Encrypt.Mode.values()) {
            ISymmetryCipher cipher = new DES(pwd, mode);
            ciphers.add(cipher);
        }
        testCipher(msg, ciphers, pwd);
    }
    
    private static void testDES3(byte[] msg, byte[] pwd) throws Exception {
        List<ISymmetryCipher> ciphers = new ArrayList<>();
        for (Encrypt.Mode mode : Encrypt.Mode.values()) {
            ISymmetryCipher cipher = new DES3(pwd, mode);
            ciphers.add(cipher);
        }
        testCipher(msg, ciphers, pwd);
    }
    
    private static void testAES(byte[] msg, byte[] pwd) throws Exception {
        List<ISymmetryCipher> ciphers = new ArrayList<>();
        for (Encrypt.Mode mode : Encrypt.Mode.values()) {
            ISymmetryCipher cipher = new AES(pwd, mode);
            ciphers.add(cipher);
        }
        testCipher(msg, ciphers, pwd);
    }
    
    private static void testCipher(byte[] msg, List<ISymmetryCipher> ciphers, byte[] pwd) throws Exception {
        String ctx = Strings.format("明文: {}, Hex: {}", new String(msg), Converts.bytesToHex(msg));
        System.out.println(ctx);
        Printer printer = new Printer();
        printer.add("Algorithm", "Mode", "Padding", "Encrypt", "Decrypt", "Hex");
        for (ISymmetryCipher cipher : ciphers) {
            byte[] encrypt = cipher.encrypt(msg);
            byte[] decrypt = cipher.decrypt(encrypt);
            String enHex = Converts.bytesToHex(encrypt);
            String deHex = Converts.bytesToHex(decrypt);
            String plain = new String(decrypt);
            printer.add(cipher.algorithm(), cipher.mode(), cipher.padding(), enHex, plain, deHex);
        }
        printer.print();
    }
    
    private static void testPassword(int bit) throws Exception {
        byte[] bytes = AES.generatePassword(bit);
        StringBuilder byteBuf = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            byteBuf.append(b + "|");
            sb.append((char) b);
        }
        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println(bytes.length);
        System.out.println(sb.toString());
        System.out.println(byteBuf.toString());
        System.out.println(encoder.encodeToString(bytes));
        System.out.println(Converts.bytesToHex(bytes));
    }
    
}
