package com.x.doraemon.encrypt.core;

/**
 * @author AD
 * @date 2022/6/7 12:10
 */
public interface IEncryptor {
    /**
     * 加密
     * @param bs 需加密的字节数组
     * @return 加密后的字节数组
     */
    byte[] encrypt(byte[] bs)throws Exception;
}
