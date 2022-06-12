package com.x.doraemon.encrypt.core;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.x.doraemon.Strings;
import com.x.doraemon.encrypt.core.Encrypt.Mode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

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
public abstract class BaseCipher implements ICipher {

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
    protected final Encrypt encrypt;

    /**
     * 加密模式
     */
    protected final Encrypt.Mode mode;

    /**
     * 完整的算法名
     * 算法/加密模式/填充模式（DES|TripleDES|AES 或 DES|TripleDES|AES/CBC/PCKS5Padding）
     */
    protected final String algorithm;

    // ------------------------ 构造方法 ------------------------

    /**
     * 构造函数
     * @param password 密钥(DES密钥长度8, TripleDES密钥长度24, AES密钥长度16)
     */
    protected BaseCipher(byte[] password) {
        this(password, Mode.ECB, password);
    }

    /**
     * 构造函数
     * @param password 密钥(DES密钥长度8,TripleDES密钥长度24)
     * @param mode     加密模式(DES支持: ECB、CBC、PCBC、CFB、OFB、CTR; TripleDES支持: ECB、CBC)
     */
    protected BaseCipher(byte[] password, Encrypt.Mode mode) {
        this(password, mode, password);
    }

    /**
     * 构造函数
     * @param password 密钥(DES密钥长度8,TripleDES密钥长度24)
     * @param mode     加密模式(DES支持: ECB、CBC、PCBC、CFB、OFB、CTR; TripleDES支持: ECB、CBC)
     * @param iv       向量(非ECB模式需要, 长度固定为8, 默认和密码一样)
     */
    protected BaseCipher(byte[] password, Encrypt.Mode mode, byte[] iv) {
        if (password == null || password.length < minPasswordLength()) {
            throw new RuntimeException("密码不能小于" + minPasswordLength() + "位");
        }
        if (iv == null || iv.length < ivLength()) {
            throw new RuntimeException("向量必须等于" + ivLength() + "位");
        }
        this.password = password;
        this.iv = fixIV(iv);
        this.encrypt = getEncrypt();
        this.mode = mode == null ? Mode.ECB : mode;
        this.algorithm = Strings.joining("/", encrypt, mode, PADDING);
    }

    // ------------------------ 方法定义 ------------------------

    @Override
    public String algorithm() {
        return algorithm;
    }

    @Override
    public String mode() {
        return mode.toString();
    }

    @Override
    public byte[] decrypt(byte[] bs) throws Exception {
        if (bs == null || bs.length == 0) {
            return bs;
        }
        // 获取解密机
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);
        byte[] in = new byte[blockSize];
        byte[] out = new byte[outputSize];
        ByteArrayInputStream input = new ByteArrayInputStream(bs);
        ByteOutputStream output = new ByteOutputStream();
        int read = 0;
        boolean more = true;
        while (more) {
            read = input.read(in);
            if (read == blockSize) {
                cipher.update(in, 0, read, out);
                output.write(out);
            } else {
                more = false;
            }
        }
        if (read > 0) {
            out = cipher.doFinal(in, 0, read);
        } else {
            out = cipher.doFinal();
        }
        output.write(out);
        // 解密
        // return cipher.doFinal(bs);
        return output.getBytes();
    }

    @Override
    public byte[] encrypt(byte[] bs) throws Exception {
        if (bs == null || bs.length == 0) {
            return bs;
        }
        // 获取加密机
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);

        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);
        byte[] in = new byte[blockSize];
        byte[] out = new byte[outputSize];
        ByteArrayInputStream input = new ByteArrayInputStream(bs);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        int read = 0;
        boolean more = true;
        while (more) {
            read = input.read(in);
            if (read == blockSize) {
                int update = cipher.update(in, 0, blockSize, out);
                output.write(out, 0, update);
                in = new byte[blockSize];
                out = new byte[outputSize];
            } else {
                more = false;
            }
        }
        if (read > 0) {
            out = cipher.doFinal(in, 0, read);
        } else {
            out = cipher.doFinal();
        }
        output.write(out);
        // 加密
        // return cipher.doFinal(bs);
        return output.toByteArray();
    }

    // ------------------------ 保护方法 ------------------------

    protected byte[] generatePasswordKey() throws NoSuchAlgorithmException {
        KeyGenerator gen = KeyGenerator.getInstance(getEncrypt().toString());
        gen.init(minPasswordLength());
        SecretKey key = gen.generateKey();
        return key.getEncoded();
    }

    // ------------------------ 私有方法 ------------------------

    private Cipher getCipher(int cipherMode) throws Exception {
        // 生成秘钥
        Key key = generateKey(password);
        // 获取加密算法
        Cipher cipher = Cipher.getInstance(algorithm);
        // Cipher cipher = Cipher.getInstance(getEncrypt().toString());
        // ECB模式不支持IV（ECB模式是最基本的工作模式, DES=DES/ECB/PCKS5Padding）
        if (mode == null || Encrypt.Mode.ECB == mode) {
            cipher.init(cipherMode, key);
        } else {
            // 创建向量
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            // 初始化加密机
            cipher.init(cipherMode, key, ivSpec);
        }
        return cipher;
    }

    /**
     * 获取加密算法
     * @return DES 或 TripleDES
     */
    protected abstract Encrypt getEncrypt();

    /**
     * 生成秘钥
     * @param password 秘钥, DES长度不能小于8, TripleDES不能小于24
     * @return
     * @throws Exception
     */
    protected abstract Key generateKey(byte[] password) throws Exception;

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
