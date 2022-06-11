package com.x.doraemon.encrypt.core;

/**
 * 加密枚举
 * @author AD
 * @date 2022/6/11 19:53
 */
public enum Encrypt {
    DES,
    AES,
    RSA;
    
    public static enum Mode {
        /**
         * 基本的工作模式
         * Electronic code book，电子密码本模式
         */
        ECB,
        /**
         * 推荐
         * Cipher-block chaining，密码分组链接模式
         */
        CBC,
        /**
         * Propagating cipher-block chaining，明文密码块链接模式
         */
        PCBC,
        /**
         * Cipher feedback，密文反馈模式
         */
        CFB,
        /**
         * Output feedback，输出反馈模式
         */
        OFB,
        /**
         * Counter mode，计算器模式
         */
        CTR;
    }
    
    public static enum Padding {
        NoPadding,
        ZeroPadding,
        Pkcs5Padding,
        Pkcs7Padding,
        Iso7816Padding,
        Ansix923Padding;
    }
}
