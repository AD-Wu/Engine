package com.x.doraemon.encrypt.core;

/**
 * @author AD
 * @date 2022/6/7 12:15
 */
public interface ICipher extends IEncryptor, IDecryptor {

    /**
     * 加密算法名
     * @return
     */
    String algorithm();
}
