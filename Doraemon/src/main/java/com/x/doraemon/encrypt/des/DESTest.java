package com.x.doraemon.encrypt.des;

import com.x.doraemon.Converts;
import com.x.doraemon.Printer;
import com.x.doraemon.encrypt.core.Encrypt;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * DES加/解密测试
 * @author AD
 * @date 2022/6/11 13:19
 */
public class DESTest {
    
    public static void main(String[] args) throws Exception {
        
        byte[] msg = "hello world".getBytes(StandardCharsets.UTF_8);
        String pwd = "12345678";
        
        String msgHex = Converts.bytesToHex(msg);
        System.out.println(msgHex);
        Printer printer = new Printer();
        printer.add("mode", "algorithm","encrypt", "decrypt");
        Encrypt.Mode[] modes = Encrypt.Mode.values();
        List<Encrypt.Mode> modeList = new ArrayList<>();
        for (Encrypt.Mode mode : modes) {
            modeList.add(mode);
        }
        modeList.add(null);
        for (Encrypt.Mode mode : modeList) {
            DES des = new DES(pwd, mode);
            byte[] encrypt = des.encrypt(msg);
            byte[] decrypt = des.decrypt(encrypt);
            String enHex = Converts.bytesToHex(encrypt);
            String deHex = Converts.bytesToHex(decrypt);
            printer.add(mode, des.algorithm(),enHex, deHex);
        }
        printer.print();
    }
    
}
