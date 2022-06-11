package com.x.doraemon.encrypt.core;

/**
 * @author AD
 * @date 2022/6/7 12:10
 */
public interface IDecryptor {

    /**
     * 解密
     * @param bs 需解密的字节数组
     * @return 解密后的字节数组
     */
    byte[] decrypt(byte[] bs)throws Exception;
}
