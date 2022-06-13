package com.x.doraemon.encrypt.aes;

import com.x.doraemon.encrypt.core.SymmetryCipher;
import com.x.doraemon.encrypt.core.Encrypt;
import com.x.doraemon.encrypt.core.Encrypt.Mode;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * @author AD
 * @date 2022/6/12 15:51
 */
public class AES extends SymmetryCipher {
    
    // ------------------------ 静态方法 ------------------------
    
    /**
     * 生成AES的秘钥
     * @param bit 比特位数（128，192，256）
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] generatePassword(int bit) throws Exception {
        if (bit != 128 && bit != 192 && bit != 256) {
            throw new RuntimeException("比特位数128、192、256");
        }
        KeyGenerator gen = KeyGenerator.getInstance(Encrypt.Algorithm.AES.toString());
        gen.init(bit);
        SecretKey key = gen.generateKey();
        return key.getEncoded();
    }
    
    // ------------------------ 构造方法 ------------------------
    public AES(byte[] password) {
        super(password);
    }
    
    public AES(byte[] password, Mode mode) {
        super(password, mode);
    }
    
    public AES(byte[] password, Mode mode, byte[] iv) {
        super(password, mode, iv);
    }
    
    // ------------------------ 方法定义 ------------------------
    
    @Override
    public String algorithm() {
        return Encrypt.Algorithm.AES.toString();
    }
    
    @Override
    protected Key generateKey(int cipherMode, byte[] password) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password, algorithm);
        return key;
    }
    
    @Override
    protected int minPasswordLength() {
        return 16;
    }
    
    @Override
    protected int ivLength() {
        return 16;
    }
    
}
