package com.x.doraemon.encrypt.core;

/**
 * @author AD
 * @date 2022/6/7 12:15
 */
public interface ICipher {
    
    /**
     * 加密算法
     * @return
     */
    String algorithm();
   
    
    /**
     * 加密
     * @param bs 需加密的字节数组
     * @return 加密后的字节数组
     */
    byte[] encrypt(byte[] bs) throws Exception;
    
    /**
     * 解密
     * @param bs 需解密的字节数组
     * @return 解密后的字节数组
     */
    byte[] decrypt(byte[] bs) throws Exception;
    
}
