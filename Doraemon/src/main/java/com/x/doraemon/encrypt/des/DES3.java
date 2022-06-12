package com.x.doraemon.encrypt.des;

import com.x.doraemon.encrypt.core.Encrypt;
import com.x.doraemon.encrypt.core.Encrypt.Mode;
import com.x.doraemon.encrypt.core.ICipher;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

/**
 * TripleDES加密算法
 * 模式支持: ECB、CBC
 * 填充支持: PCKS5Padding
 * @author AD
 * @date 2022/6/12 14:08
 */
public final class DES3 extends BaseDES {

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

    public static ICipher mode(String password, Encrypt.Mode mode, String iv) {
        return new DES3(password.getBytes(), mode, iv.getBytes());
    }

    // ------------------------ 构造方法 ------------------------

    /**
     * 构造函数
     * @param password 密钥(DES密钥长度8,TripleDES密钥长度24)
     * @param mode     加密模式(DES支持: ECB、CBC、PCBC、CFB、OFB、CTR; TripleDES支持: ECB、CBC)
     * @param iv       向量(非ECB模式需要, 长度固定为8, 默认和密码一样)
     */
    protected DES3(byte[] password, Mode mode, byte[] iv) {
        super(password, mode, iv);
    }

    // ------------------------ 方法定义 ------------------------

    @Override
    protected Encrypt getEncrypt() {
        return Encrypt.TripleDES;
    }

    @Override
    protected Key generateKey(byte[] password) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password, getEncrypt().toString());
        return key;
    }

    @Override
    protected int minPasswordLength() {
        return 24;
    }
}
