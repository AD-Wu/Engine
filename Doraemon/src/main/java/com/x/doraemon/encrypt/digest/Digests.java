package com.x.doraemon.encrypt.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author AD
 * @date 2022/6/7 11:23
 */
public enum Digests {

    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    SHA384("SHA-384"),
    SHA512("SHA-512");

    private MessageDigest alg;

    private Digests(String name) {
        try {
            this.alg = MessageDigest.getInstance(name);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public MessageDigest algorithm() {
        return alg;
    }

    public byte[] digest(byte[] bs) {
        // 使用指定的字节来更新摘要
        alg.update(bs);
        // 完成散列计算，返回计算所得的摘要，并复位算法对象
        byte[] digest = alg.digest();
        return digest;
    }


}
