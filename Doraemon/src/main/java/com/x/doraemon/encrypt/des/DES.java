package com.x.doraemon.encrypt.des;

import com.x.doraemon.Strings;
import com.x.doraemon.encrypt.core.Encrypt;
import com.x.doraemon.encrypt.core.ICipher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * DES加密算法
 * @author AD
 * @date 2022/6/11 12:33
 */
public class DES implements ICipher {
    
    // ------------------------ 常量定义 ------------------------
    
    /**
     * 向量，固定8字节。使用CBC模式时，必须有向量，可增加加密算法的强度
     */
    private static final byte[] IV = "12345678".getBytes(StandardCharsets.UTF_8);
    
    /**
     * 算法名
     */
    private static final String DES = Encrypt.DES.toString();
    
    /**
     * 算法DES等价于DES/ECB/PCKS5Padding，默认模式为ECB
     */
    private static final Encrypt.Mode DEFAULT_MODE = Encrypt.Mode.ECB;
    
    /**
     * 默认填充模式，Java仅支持PCKS5Padding
     */
    private static final Encrypt.Padding PADDING = Encrypt.Padding.Pkcs5Padding;
    
    // ------------------------ 变量定义 ------------------------
    
    /**
     * 秘钥（字节数组）
     */
    private final byte[] password;
    
    /**
     * 加密模式
     */
    private final Encrypt.Mode mode;
    
    /**
     * 算法/加密模式/填充模式（DES或DES/CBC/PCKS5Padding）
     */
    private final String algorithm;
    
    // ------------------------ 构造方法 ------------------------
    
    public DES(String password) {
        this(password.getBytes(StandardCharsets.UTF_8), DEFAULT_MODE);
    }
    
    public DES(String password, Encrypt.Mode mode) {
        this(password.getBytes(StandardCharsets.UTF_8), mode);
    }
    
    // 默认的是ECB模式的加密，mode为空时等价与ECB模式
    public DES(byte[] password) {
        this(password, DEFAULT_MODE);
    }
    
    public DES(byte[] password, Encrypt.Mode mode) {
        if (password == null || password.length < 8) {
            throw new RuntimeException("密码字节长度不能小于8位");
        }
        this.password = password;
        this.mode = mode;
        this.algorithm = mode == null ? DES : Strings.joining("/", DES, mode, PADDING);
    }
    
    // ------------------------ 方法定义 ------------------------
    
    @Override
    public String algorithm() {
        return algorithm;
    }
    
    @Override
    public byte[] decrypt(byte[] bs) throws Exception {
        if (bs == null || bs.length == 0) {
            return bs;
        }
        // 获取解密机
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        // 解密
        return cipher.doFinal(bs);
    }
    
    @Override
    public byte[] encrypt(byte[] bs) throws Exception {
        if (bs == null || bs.length == 0) {
            return bs;
        }
        // 获取加密机
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        // 加密
        return cipher.doFinal(bs);
    }
    
    // ------------------------ 私有方法 ------------------------
    
    private Cipher getCipher(int cipherMode) throws Exception {
        // 生成秘钥
        Key key = generateKey(password);
        // 获取加密算法
        Cipher cipher = Cipher.getInstance(algorithm);
        // ECB模式不支持IV（ECB模式是最基本的工作模式）
        if (mode == null || Encrypt.Mode.ECB == mode) {
            cipher.init(cipherMode, key);
        } else {
            // 创建向量
            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            // 初始化加密机
            cipher.init(cipherMode, key, ivSpec);
        }
        return cipher;
    }
    
    /**
     * 生成秘钥
     * @param password 秘钥，长度不能小于8
     * @return
     * @throws Exception
     */
    private Key generateKey(byte[] password) throws Exception {
        // des秘钥的字节长度固定为8
        DESKeySpec spec = new DESKeySpec(password);
        SecretKeyFactory fact = SecretKeyFactory.getInstance(DES);
        SecretKey key = fact.generateSecret(spec);
        return key;
    }
    
}
