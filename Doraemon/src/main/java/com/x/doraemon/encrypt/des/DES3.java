package com.x.doraemon.encrypt.des;

import com.x.doraemon.encrypt.core.SymmetryCipher;
import com.x.doraemon.encrypt.core.Encrypt;
import com.x.doraemon.encrypt.core.Encrypt.Mode;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * TripleDES加密算法
 * 模式支持: ECB、CBC
 * 填充支持: PCKS5Padding
 * @author AD
 * @date 2022/6/12 14:08
 */
public final class DES3 extends SymmetryCipher {
    
    // ------------------------ 构造方法 ------------------------
    
    public DES3(byte[] password) {
        super(password);
    }
    
    public DES3(byte[] password, Mode mode) {
        super(password, mode);
    }
    
    public DES3(byte[] password, Mode mode, byte[] iv) {
        super(password, mode, iv);
    }
    
    // ------------------------ 方法定义 ------------------------
    
    @Override
    public String algorithm() {
        return Encrypt.Algorithm.TripleDES.toString();
    }
    
    // ------------------------ 保护方法 ------------------------
    
    @Override
    protected Key generateKey(int cipherMode, byte[] password) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password, algorithm);
        return key;
    }
    
    @Override
    protected int minPasswordLength() {
        return 24;
    }
    
    @Override
    protected int ivLength() {
        return 8;
    }
    
}
