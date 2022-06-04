package com.x.jdk8.locale;

import java.nio.charset.Charset;

/**
 * @author AD
 * @date 2022/5/31 14:52
 */
public class TxtDemo {

    public static void main(String[] args) {
        defaultCharset();
    }

    private static void defaultCharset(){
        Charset charset = Charset.defaultCharset();
        System.out.println(charset);
    }
}
