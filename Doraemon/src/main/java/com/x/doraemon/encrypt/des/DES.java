package com.x.doraemon.encrypt.des;

import com.x.doraemon.encrypt.core.Encrypt;
import com.x.doraemon.encrypt.core.Encrypt.Mode;
import com.x.doraemon.encrypt.core.ICipher;
import java.security.Key;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES加密算法
 * 模式支持: ECB、CBC、PCBC、CFB、OFB、CTR
 * 填充支持: PCKS5Padding
 * @author AD
 * @date 2022/6/11 12:33
 */
public final class DES extends BaseDES {

    // ------------------------ 静态方法 ------------------------

    public static ICipher ecb(String password) {
        return mode(password, Encrypt.Mode.ECB, null);
    }

    public static ICipher cbc(String password) {
        return mode(password, Encrypt.Mode.CBC, password);
    }

    public static ICipher cbc(String password, String iv) {
        return mode(password, Encrypt.Mode.CBC, iv);
    }

    public static ICipher mode(String password, Encrypt.Mode mode) {
        return mode(password, mode, password);
    }

    public static ICipher mode(String password, Encrypt.Mode mode, String iv) {
        return new DES(password.getBytes(), mode, iv.getBytes());
    }

    // ------------------------ 构造方法 ------------------------

    /**
     * 构造函数
     * @param password 密钥(DES密钥长度8,TripleDES密钥长度24)
     * @param mode     加密模式(DES支持: ECB、CBC、PCBC、CFB、OFB、CTR; TripleDES支持: ECB、CBC)
     * @param iv       向量(非ECB模式需要, 长度固定为8, 默认和密码一样)
     */
    protected DES(byte[] password, Mode mode, byte[] iv) {
        super(password, mode, iv);
    }

    // ------------------------ 方法定义 ------------------------

    @Override
    protected Encrypt getEncrypt() {
        return Encrypt.DES;
    }

    /**
     * 生成秘钥
     * @param password 秘钥，长度不能小于8
     * @return
     * @throws Exception
     */
    protected Key generateKey(byte[] password) throws Exception {
        // des秘钥的字节长度固定为8
        DESKeySpec spec = new DESKeySpec(password);
        SecretKeyFactory fact = SecretKeyFactory.getInstance(getEncrypt().toString());
        SecretKey key = fact.generateSecret(spec);
        return key;
    }

    @Override
    protected int minPasswordLength() {
        return 8;
    }

}
