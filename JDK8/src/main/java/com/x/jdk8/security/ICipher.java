package com.x.jdk8.security;

/**
 * 加密机接口
 *
 * @author AD
 * @date 2022/6/5 22:02
 */
public interface ICipher {

    /**
     * 加密
     *
     * @param bs 需加密的字节数组
     *
     * @return 加密后的字节数组
     */
    byte[] encrypt(byte[] bs);

    /**
     * 解密
     *
     * @param bs 需解密的字节数组
     *
     * @return 解密后的字节数组
     */
    byte[] decrypt(byte[] bs);

}
