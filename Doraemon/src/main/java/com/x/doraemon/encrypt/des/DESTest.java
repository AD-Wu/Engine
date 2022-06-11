package com.x.doraemon.encrypt.des;

import com.x.doraemon.Converts;
import com.x.doraemon.Printer;

/**
 * DES加/解密测试
 *
 * @author AD
 * @date 2022/6/11 13:19
 */
public class DESTest {
    
    public static void main(String[] args) throws Exception {
        
        String msg = "Hello World";
        DES des = new DES("12345678");
        byte[] data = msg.getBytes();
        byte[] encrypt = des.encrypt(data);
        byte[] decrypt = des.decrypt(encrypt);
        
        Printer printer = new Printer();
        printer.add("data", Converts.bytesToHex(data));
        printer.add("encrypt", Converts.bytesToHex(encrypt));
        printer.add("decrypt", Converts.bytesToHex(decrypt));
        printer.print();
    }
    
}
