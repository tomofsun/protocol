package com.wakzz.common.utils;

import javafx.util.Pair;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * RSA非对称加密算法
 */
public class RSASignUtil {

    private static final String SIGNATURE_INSTANCE = "SHA256WithRSA";
    private static final String RSA_INSTANCE = "RSA";
    private static final int MAX_ENCRYPT_BLOCK = 245;
    private static final int MAX_DECRYPT_BLOCK = 256;

    /**
     * RSA私钥签名
     *
     * @param pvkString 秘钥
     * @param source    文本
     */
    public static String signBySHA256WithRSA(String pvkString, String source) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_INSTANCE);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.decodeBase64(pvkString.getBytes()));
        KeyFactory ky = KeyFactory.getInstance(RSA_INSTANCE);
        PrivateKey privateKey = ky.generatePrivate(spec);

        signature.initSign(privateKey);
        signature.update(source.getBytes());
        byte result[] = signature.sign();

        return Base64.encodeBase64String(result);
    }

    /**
     * RSA公钥验签
     *
     * @param pukString 公钥
     * @param signValue 签名
     * @param source    文本
     */
    public static boolean verifySignBySHA256WithRSA(String pukString, String signValue, String source) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(pukString.getBytes()));
        KeyFactory ky = KeyFactory.getInstance(RSA_INSTANCE);
        PublicKey pukKey = ky.generatePublic(spec);

        Signature signature = Signature.getInstance(SIGNATURE_INSTANCE);
        signature.initVerify(pukKey);
        signature.update(source.getBytes());
        // 验证商户签名
        return signature.verify(Base64.decodeBase64(signValue.getBytes()));
    }

    /**
     * RSA公钥加密
     *
     * @param data      文本
     * @param publicKey 公钥
     */
    public static byte[] encryptByPuk(byte[] data, String publicKey) throws Exception {
        byte[] decoded = Base64.decodeBase64(publicKey);
        PublicKey pubKey = KeyFactory.getInstance(RSA_INSTANCE).generatePublic(new X509EncodedKeySpec(decoded));
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Cipher cipher = Cipher.getInstance(RSA_INSTANCE);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            int offSet = 0;
            while (data.length > offSet) {
                int length = data.length - offSet > MAX_ENCRYPT_BLOCK ? MAX_ENCRYPT_BLOCK : data.length - offSet;
                byte[] cache = cipher.doFinal(data, offSet, length);
                out.write(cache, 0, cache.length);
                offSet += length;
            }
            return out.toByteArray();
        }
    }

    /**
     * RSA私钥解密
     *
     * @param data       文本
     * @param privateKey 私钥
     */
    public static byte[] decryptByPvk(byte[] data, String privateKey) throws Exception {
        byte[] decoded = Base64.decodeBase64(privateKey);
        PrivateKey priKey = KeyFactory.getInstance(RSA_INSTANCE).generatePrivate(new PKCS8EncodedKeySpec(decoded));
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Cipher cipher = Cipher.getInstance(RSA_INSTANCE);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            int offSet = 0;
            while (data.length > offSet) {
                int length = data.length - offSet > MAX_DECRYPT_BLOCK ? MAX_DECRYPT_BLOCK : data.length - offSet;
                byte[] cache = cipher.doFinal(data, offSet, length);
                out.write(cache, 0, cache.length);
                offSet += length;
            }
            return out.toByteArray();
        }
    }

    /**
     * 生成RSA公钥私钥
     */
    public static Pair<RSAPublicKey, RSAPrivateKey> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_INSTANCE);
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new Pair<>(publicKey, privateKey);
    }

    public static void main(String[] args) throws Exception {
        Pair<RSAPublicKey, RSAPrivateKey> pair = initKey();
        RSAPublicKey publicKey = pair.getKey();
        RSAPrivateKey privateKey = pair.getValue();

        String publicKeyValue = Base64.encodeBase64String(publicKey.getEncoded());
        String privateKeyValue = Base64.encodeBase64String(privateKey.getEncoded());

        String value = "43554656786889";

        // 加密
        byte[] encrypt = encryptByPuk(value.getBytes(), publicKeyValue);
        System.out.println("encrypt:" + Base64.encodeBase64String(encrypt));
        // 解密
        byte[] decrypt = decryptByPvk(encrypt, privateKeyValue);
        System.out.println("decrypt:" + new String(decrypt));

        // 签名
        String sign = signBySHA256WithRSA(privateKeyValue, value);
        System.out.println("sign:" + sign);
        // 验签
        boolean isSuccess = verifySignBySHA256WithRSA(publicKeyValue, sign, value);
        System.out.println(isSuccess);
    }
}