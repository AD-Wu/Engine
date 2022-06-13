package com.x.doraemon.encrypt.des;

import com.x.doraemon.encrypt.core.SymmetryCipher;
import com.x.doraemon.encrypt.core.Encrypt;
import com.x.doraemon.encrypt.core.Encrypt.Mode;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;

/**
 * DES加密算法
 * 模式支持: ECB、CBC、PCBC、CFB、OFB、CTR
 * 填充支持: PCKS5Padding
 * @author AD
 * @date 2022/6/11 12:33
 */
public final class DES extends SymmetryCipher {
    
    // ------------------------ 构造方法 ------------------------
    
    public DES(byte[] password) {
        super(password);
    }
    
    public DES(byte[] password, Mode mode) {
        super(password, mode);
    }
    
    public DES(byte[] password, Mode mode, byte[] iv) {
        super(password, mode, iv);
    }
    
    // ------------------------ 方法定义 ------------------------
    
    @Override
    public String algorithm() {
        return Encrypt.Algorithm.DES.toString();
    }
    
    // ------------------------ 保护方法 ------------------------
    
    /**
     * 生成秘钥
     * @param password 秘钥，长度不能小于8
     * @return
     * @throws Exception
     */
    @Override
    protected Key generateKey(int cipherMode, byte[] password) throws Exception {
        // des秘钥的字节长度固定为8
        DESKeySpec spec = new DESKeySpec(password);
        SecretKeyFactory fact = SecretKeyFactory.getInstance(algorithm);
        SecretKey key = fact.generateSecret(spec);
        return key;
    }
    
    @Override
    protected int minPasswordLength() {
        return 8;
    }
    
    @Override
    protected int ivLength() {
        return 8;
    }
    
}
