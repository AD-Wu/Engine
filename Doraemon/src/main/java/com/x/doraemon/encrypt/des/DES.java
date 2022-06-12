package com.x.doraemon.encrypt.des;

import com.x.doraemon.encrypt.core.Encrypt;
import com.x.doraemon.encrypt.core.Encrypt.Mode;
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
