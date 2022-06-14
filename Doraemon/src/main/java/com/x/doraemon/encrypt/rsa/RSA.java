package com.x.doraemon.encrypt.rsa;

import com.x.doraemon.Converts;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;

public class RSA {
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    public RSA() {
    }

    public static KeyPair generateKeyPair(int bit) throws Exception {
        KeyPairGenerator pairGen = KeyPairGenerator.getInstance("RSA");
        pairGen.initialize(bit);
        KeyPair pair = pairGen.genKeyPair();
        return pair;
    }

    public static RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] exponent) throws Exception {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(exponent));
        return (RSAPublicKey)fact.generatePublic(spec);
    }

    public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus, byte[] exponent) throws Exception {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec spec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(exponent));
        return (RSAPrivateKey)fact.generatePrivate(spec);
    }

    public static Map<String,String> generateKeys(int bit) throws Exception {
        KeyPair pair = generateKeyPair(bit);
        RSAPublicKey publicKey = (RSAPublicKey)pair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey)pair.getPrivate();
        byte[] pubModulus = publicKey.getModulus().toByteArray();
        byte[] pubExponent = publicKey.getPublicExponent().toByteArray();
        byte[] priModulus = privateKey.getModulus().toByteArray();
        byte[] priExponent = privateKey.getPrivateExponent().toByteArray();
        RSAPublicKey pubKey = generateRSAPublicKey(pubModulus, pubExponent);
        RSAPrivateKey priKey = generateRSAPrivateKey(priModulus, priExponent);
        Encoder encoder = Base64.getEncoder();

        byte[] pubKeyEncoded = encoder.encode(pubKey.getEncoded());
        byte[] base64PubModulus = encoder.encode(pubModulus);
        byte[] base64PubExponent = encoder.encode(pubExponent);

        byte[] priKeyEncoded = encoder.encode(priKey.getEncoded());
        byte[] base64PriModulus = encoder.encode(priModulus);
        byte[] base64PriExponent = encoder.encode(priExponent);
        Map<String,String> keys = new HashMap<>();
        keys.put("PublicKey", new String(pubKeyEncoded, StandardCharsets.US_ASCII));
        keys.put("PublicModules", new String(base64PubModulus, StandardCharsets.US_ASCII));
        keys.put("PublicExponent", new String(base64PubExponent, StandardCharsets.US_ASCII));
        keys.put("PrivateKey", new String(priKeyEncoded, StandardCharsets.US_ASCII));
        keys.put("PrivateModulus", new String(base64PriModulus, StandardCharsets.US_ASCII));
        keys.put("PrivateExponent", new String(base64PriExponent, StandardCharsets.US_ASCII));
        return keys;
    }

    public static RSAPublicKey decodePublicKey(String publicKey) throws Exception {
        Decoder decoder = Base64.getDecoder();
        byte[] keyBytes = decoder.decode(publicKey.getBytes());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey)factory.generatePublic(spec);
    }

    public static RSAPrivateKey decodePrivateKey(String privateKey) throws Exception {
        Decoder decoder = Base64.getDecoder();
        byte[] keyBytes = decoder.decode(privateKey.getBytes());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey)factory.generatePrivate(spec);
    }

    public void setKey(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public byte[] encode(byte[] msg) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
        int modulusBits = this.publicKey.getModulus().bitLength() / 8 - 11;
        int outputSize = cipher.getOutputSize(msg.length);
        int mod = msg.length % modulusBits;
        int blockCount = mod != 0 ? msg.length / modulusBits + 1 : msg.length / modulusBits;
        byte[] total = new byte[outputSize * blockCount];

        for(int i = 0; msg.length - i * modulusBits > 0; ++i) {
            if (msg.length - i * modulusBits > modulusBits) {
                cipher.doFinal(msg, i * modulusBits, modulusBits, total, i * outputSize);
            } else {
                cipher.doFinal(msg, i * modulusBits, msg.length - i * modulusBits, total, i * outputSize);
            }
        }

        return total;
    }

    public byte[] decode(byte[] encrypts) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
        int modulusBits = this.privateKey.getModulus().bitLength() / 8;
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);

        for(int i = 0; encrypts.length - i * modulusBits > 0; ++i) {
            out.write(cipher.doFinal(encrypts, i * modulusBits, modulusBits));
        }

        return out.toByteArray();
    }

    public static String rsaEncodeTest() throws Exception {
        String msg = "admin|E10ADC3949BA59ABBE56E057F20F883E|1456145396000";
        byte[] msgBytes = msg.getBytes("UTF-8");
        String var2 = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJJfsLg+gyTR8HyylVVbwDk8zbCr8eDMP7mdg3QUePLcVYS4+qOfwkrgEAB+1bXXZ5oHz4emplPpqlTFuOneenMCAwEAAQ==";
        Decoder decoder = Base64.getDecoder();
        byte[] var4 = decoder.decode(var2.getBytes());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(var4);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(spec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int modulusBits = publicKey.getModulus().bitLength() / 8 - 11;
        int outputSize = cipher.getOutputSize(msgBytes.length);
        int mod = msgBytes.length % modulusBits;
        int blockCount = mod != 0 ? msgBytes.length / modulusBits + 1 : msgBytes.length / modulusBits;
        byte[] total = new byte[outputSize * blockCount];

        for(int i = 0; msgBytes.length - i * modulusBits > 0; ++i) {
            if (msgBytes.length - i * modulusBits > modulusBits) {
                cipher.doFinal(msgBytes, i * modulusBits, modulusBits, total, i * outputSize);
            } else {
                cipher.doFinal(msgBytes, i * modulusBits, msgBytes.length - i * modulusBits, total, i * outputSize);
            }
        }
        return Converts.bytesToHex(total);
    }

    public static void main(String[] var0) throws Exception {
        try {
            Map<String,String> keys = generateKeys(512);
            System.out.println("-------------- 公钥 --------------");
            String publicKey = keys.get("PublicKey");
            System.out.println("PublicKey:\r\n" + publicKey);
            System.out.println("PublicModules:\r\n" + keys.get("PublicModules"));
            System.out.println("PublicExponent:\r\n" + keys.get("PublicExponent"));
            System.out.println("-------------- 私钥 --------------");
            String privateKey = keys.get("PrivateKey");
            System.out.println("PrivateKey:\r\n" + privateKey);
            System.out.println("PrivateModulus:\r\n" + keys.get("PrivateModulus"));
            System.out.println("PrivateExponent:\r\n" + keys.get("PrivateExponent"));
            System.out.println("-------------- 加密 --------------");
            String msg = "admin|E10ADC3949BA59ABBE56E057F20F883E|1456145396000";
            byte[] msgBytes = msg.getBytes("UTF-8");
            RSAPublicKey rsaPublicKey = decodePublicKey(publicKey);
            RSAPrivateKey rsaPrivateKey = decodePrivateKey(privateKey);
            System.out.println("公钥长度: " + rsaPublicKey.getEncoded().length);
            System.out.println("私钥长度: " + rsaPrivateKey.getEncoded().length);
            RSA rsa = new RSA();
            rsa.setKey(rsaPublicKey, rsaPrivateKey);
            byte[] encrypt = rsa.encode(msgBytes);
            System.out.println("加密结果: " + Converts.bytesToHex(encrypt));
            byte[] decrypt = rsa.decode(encrypt);
            String plain = new String(decrypt, "UTF-8");
            System.out.println("加密前: " + msg);
            System.out.println("解密后: " + plain);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
