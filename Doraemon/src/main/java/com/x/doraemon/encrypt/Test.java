package com.x.doraemon.encrypt;

import com.x.doraemon.Converts;
import com.x.doraemon.Printer;

/**
 * @author AD
 * @date 2022/6/7 12:09
 */
public class Test {

    private static final byte[] bs = "Hello World".getBytes();

    public static void main(String[] args) throws Exception {
        Printer printer = new Printer();
        for (Digests helper : Digests.values()) {
            byte[] digest = helper.digest(bs);
            String hex = Converts.bytesToHex(digest);
            printer.add(helper, helper.algorithm().getDigestLength(), hex);
        }
        printer.print();
    }
}
