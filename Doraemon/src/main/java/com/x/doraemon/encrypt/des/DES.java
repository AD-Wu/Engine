package com.x.doraemon.encrypt.des;

import com.x.doraemon.encrypt.core.ICipher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.Charset;
import java.security.Key;

/**
 * DES加密算法
 *
 * @author AD
 * @date 2022/6/11 12:33
 */
public class DES implements ICipher {
    
    // ------------------------ 常量定义 ------------------------
    
    /**
     * 偏移量，固定8字节
     */
    // private static final String IV = "12345678";
    
    /**
     * 秘钥算法
     */
    private static final String KEY_ALGORITHM = "DES";
    
    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";
    
    // ------------------------ 变量定义 ------------------------
    
    /**
     * 秘钥
     */
    private final String password;
    
    // ------------------------ 构造方法 ------------------------
    
    /**
     * DES构造方法
     */
    public DES(String password) {
        this.password = password;
        if (password == null || password.length() < 8) {
            throw new RuntimeException("DES密码的字节长度不能小于8位");
        }
    }
    
    // ------------------------ 方法定义 ------------------------
    
    @Override
    public String algorithm() {
        return KEY_ALGORITHM;
    }
    
    @Override
    public byte[] decrypt(byte[] bs) throws Exception {
        if (bs == null || bs.length == 0) {
            return bs;
        }
        // 生成秘钥
        Key key = generateKey(password);
        // 获取解密机
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(password.getBytes());
        // 初始化加密机
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        // 加密
        return cipher.doFinal(bs);
    }
    
    @Override
    public byte[] encrypt(byte[] bs) throws Exception {
        if (bs == null || bs.length == 0) {
            return bs;
        }
        // 生成秘钥
        Key key = generateKey(password);
        // 获取加密算法
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(password.getBytes());
        // 初始化加密机
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        // 加密
        return cipher.doFinal(bs);
    }
    
    // ------------------------ 私有方法 ------------------------
    
    /**
     * 生成秘钥
     *
     * @param password 秘钥，长度不能小于8
     *
     * @return
     *
     * @throws Exception
     */
    private Key generateKey(String password) throws Exception {
        // des秘钥的字节长度固定为8
        DESKeySpec spec = new DESKeySpec(password.getBytes(Charset.defaultCharset()));
        SecretKeyFactory fact = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey key = fact.generateSecret(spec);
        return key;
    }
    
}
