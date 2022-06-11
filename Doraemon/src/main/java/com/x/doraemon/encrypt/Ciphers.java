package com.x.doraemon.encrypt;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

/**
 * @author AD
 * @date 2022/6/10 22:37
 */
public class Ciphers {
    
    public static void main(String[] args) {
        Ciphers ciphers = new Ciphers();
        Cipher des = ciphers.des();
        String algorithm = des.getAlgorithm();
        String name = des.getProvider().getName();
        System.out.println(algorithm);
        System.out.println(name);
    
    }
    
    private Cipher des() {
       return getCipher("DES/CBC/PKCS5Padding");
    }
    
    private Cipher aes(){
       return getCipher("AES");
    }
    
    private Cipher getCipher(String algorithm){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return cipher;
    }
    
}
