package com.x.doraemon.encrypt.core;

/**
 * 对称加密
 * @author AD
 * @date 2022/6/12 22:53
 */
public interface ISymmetryCipher extends ICipher {
    
    /**
     * 加密模式
     * @return
     */
    String mode();
    
    /**
     * 填充模式
     * @return
     */
    String padding();
    
}
