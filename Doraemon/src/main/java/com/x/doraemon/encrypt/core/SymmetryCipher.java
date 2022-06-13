package com.x.doraemon.encrypt.core;

import com.x.doraemon.Strings;
import com.x.doraemon.encrypt.core.Encrypt.Mode;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

/**
 * DES加密算法
 * 模式支持: ECB、CBC、CFB、OFB、CTR
 * 填充支持: PCKS5Padding
 * </br>
 * TripleDES加密算法
 * 模式支持: ECB、CBC
 * 填充支持: PCKS5Padding
 * @author AD
 * @date 2022/6/11 12:33
 */
public abstract class SymmetryCipher implements ISymmetryCipher {
    
    // ------------------------ 常量定义 ------------------------
    
    /**
     * 默认填充模式，Java仅支持PCKS5Padding
     */
    protected static final Encrypt.Padding PADDING = Encrypt.Padding.PKCS5Padding;
    
    // ------------------------ 变量定义 ------------------------
    
    /**
     * 秘钥(DES密钥长度不能小于8字节, 超过的话也是8字节有效)
     */
    protected final byte[] password;
    
    /**
     * 向量，必须等于8字节。使用非ECB模式时，必须有向量，可增加加密算法的强度
     */
    protected final byte[] iv;
    
    /**
     * 加密算法
     */
    protected final String algorithm;
    
    /**
     * 加密模式
     */
    protected final String mode;
    
    // ------------------------ 构造方法 ------------------------
    
    protected SymmetryCipher(byte[] password) {
        this(password, Mode.ECB, password);
    }
    
    protected SymmetryCipher(byte[] password, Encrypt.Mode mode) {
        this(password, mode, password);
    }
    
    protected SymmetryCipher(byte[] password, Encrypt.Mode mode, byte[] iv) {
        this(password, mode.toString(), iv);
    }
    
    /**
     * 构造函数
     * @param password 密钥(DES密钥长度8,TripleDES密钥长度24)
     * @param mode     加密模式(DES支持: ECB、CBC、PCBC、CFB、OFB、CTR; TripleDES支持: ECB、CBC)
     * @param iv       向量(非ECB模式需要, 长度固定为8, 默认和密码一样)
     */
    protected SymmetryCipher(byte[] password, String mode, byte[] iv) {
        if (password == null || password.length < minPasswordLength()) {
            throw new RuntimeException("密码不能小于" + minPasswordLength() + "位");
        }
        if (iv == null || iv.length < ivLength()) {
            throw new RuntimeException("向量必须等于" + ivLength() + "位");
        }
        this.password = password;
        this.iv = fixIV(iv);
        this.algorithm = algorithm();
        this.mode = Strings.isNull(mode) ? Mode.ECB.toString() : mode;
    }
    
    // ------------------------ 方法定义 ------------------------
    
    @Override
    public String mode() {
        return mode;
    }
    
    @Override
    public String padding() {
        return PADDING.toString();
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
    
    // ------------------------ 私有方法 ------------------------
    
    private Cipher getCipher(int cipherMode) throws Exception {
        // 生成秘钥
        Key key = generateKey(cipherMode, password);
        // 获取加密算法(算法名/加密模式/填充模式)
        String algorithm = Strings.joining("/", algorithm(), mode, PADDING);
        Cipher cipher = Cipher.getInstance(algorithm);
        // ECB模式不支持IV（ECB模式是最基本的工作模式, 如：DES=DES/ECB/PCKS5Padding）
        if (Strings.isNull(mode) || Mode.ECB == Mode.valueOf(mode)) {
            cipher.init(cipherMode, key);
        } else {
            // 创建向量
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            // 初始化加密机
            cipher.init(cipherMode, key, ivSpec);
        }
        return cipher;
    }
    
    // ------------------------ 保护方法 ------------------------
    
    /**
     * 生成秘钥
     * @param cipherMode 加密|解密模式 ${@link Cipher#ENCRYPT_MODE,Cipher#DECRYPT_MODE}
     * @param password   秘钥, DES>=8位, TripleDES>=24位, AES>=16位
     * @return
     * @throws Exception
     */
    protected abstract Key generateKey(int cipherMode, byte[] password) throws Exception;
    
    /**
     * 最小密码长度(DES: 8位, TripleDES: 24位, AES: 16位)
     * @return
     */
    protected abstract int minPasswordLength();
    
    /**
     * 向量长度(DES|TripleDES: 8位, AES: 16位)
     * @return
     */
    protected abstract int ivLength();
    
    // ------------------------ 私有方法 ------------------------
    
    private byte[] fixIV(byte[] iv) {
        byte[] ivs = new byte[ivLength()];
        System.arraycopy(iv, 0, ivs, 0, ivLength());
        return ivs;
    }
    
}
