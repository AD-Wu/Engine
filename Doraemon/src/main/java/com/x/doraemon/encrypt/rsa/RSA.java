package com.x.doraemon.encrypt.rsa;

import com.x.doraemon.encrypt.core.Encrypt;
import com.x.doraemon.encrypt.core.ICipher;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * RSA算法
 * @author AD
 * @date 2022/6/12 21:54
 */
public class RSA implements ICipher {
    
    private static final String RSA = Encrypt.Algorithm.RSA.toString();
    
    private final Key publicKey;
    
    private final Key privateKey;
    
    /**
     * 构造方法
     * @param keySize 秘钥长度(bit, 512、1024...)
     * @throws Exception
     */
    public RSA(int keySize) throws Exception {
        KeyPairGenerator pairGen = KeyPairGenerator.getInstance(RSA);
        pairGen.initialize(keySize);
        KeyPair keyPair = pairGen.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }
    
    @Override
    public String algorithm() {
        return Encrypt.Algorithm.RSA.toString();
    }
    
    @Override
    public byte[] encrypt(byte[] bs) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(bs);
    }
    
    @Override
    public byte[] decrypt(byte[] bs) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(bs);
    }
    
}
