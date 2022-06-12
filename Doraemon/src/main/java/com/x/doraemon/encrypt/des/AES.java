package com.x.doraemon.encrypt.des;

import com.x.doraemon.encrypt.core.BaseCipher;
import com.x.doraemon.encrypt.core.Encrypt;
import com.x.doraemon.encrypt.core.Encrypt.Mode;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author AD
 * @date 2022/6/12 15:51
 */
public class AES extends BaseCipher {

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
    protected Encrypt getEncrypt() {
        return Encrypt.AES;
    }

    @Override
    protected Key generateKey(byte[] password) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password, getEncrypt().toString());
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
