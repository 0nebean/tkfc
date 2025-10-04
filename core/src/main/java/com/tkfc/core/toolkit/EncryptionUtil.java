package com.tkfc.core.toolkit;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.UUID;

/**
 * 加密类
 *
 * @author 0neBean
 */
public class EncryptionUtil {

    /**
     * 产生一个36个字符的UUID
     *
     * @return UUID
     */
    public static String randomUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * md5加密
     *
     * @param value 要加密的值
     * @return md5加密后的值
     */
    public static String md5Hex(String value) {
        return DigestUtils.md5Hex(value);
    }


    /**
     * sha256加密
     *
     * @param value 要加密的值
     * @return sha256加密后的值
     */
    public static String sha256Hex(String value) {
        return DigestUtils.sha256Hex(value);
    }

    /**
     * sha1加密
     *
     * @param value 要加密的值
     * @return sha256加密后的值
     */
    public static String sha1(String value) {
        return DigestUtils.sha1Hex(value);
    }


    /**
     * md5加密
     *
     * @param values 要加密的值
     * @return md5加密后的值
     */
    public static String md5Hex(String... values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : values) {
            stringBuilder.append(value);
        }
        return DigestUtils.md5Hex(stringBuilder.toString());
    }


    /**
     * sha1加密
     *
     * @param values 要加密的值
     * @return sha256加密后的值
     */
    public static String sha1(String... values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : values) {
            stringBuilder.append(value);
        }
        return DigestUtils.sha1Hex(stringBuilder.toString());
    }


    /**
     * sha256加密
     *
     * @param values 要加密的值
     * @return sha256加密后的值
     */
    public static String sha256Hex(String... values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : values) {
            stringBuilder.append(value);
        }
        return DigestUtils.sha256Hex(stringBuilder.toString());
    }

    /**
     * 编码
     *
     * @param encodedText 字符串
     * @return 编码后的字符串
     */
    public static String encoderBase64(String encodedText) {
        String result = null;
        result = Base64.getEncoder().encodeToString(encodedText.getBytes(StandardCharsets.UTF_8));
        return result;
    }

    /**
     * 编码
     *
     * @param deEncodedText 字符串
     * @return 编码前的字符串
     */
    public static String decoderBase64(String deEncodedText) {
        byte[] base64decodedBytes = Base64.getDecoder().decode(deEncodedText);
        return new String(base64decodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 生成 RSA 密钥对
     *
     * @param keyLength key的长度
     * @return RSA 密钥对
     * @throws NoSuchAlgorithmException 不支持算法异常
     */
    public static KeyPair geneRsaKey(int keyLength) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // 密钥长度为 2048 位
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 加密字符
     *
     * @param plaintext 源字符
     * @param publicKey 公钥
     * @return 加密后的字符
     * @throws Exception 异常
     */
    public static byte[] encrypt(String plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext.getBytes());
    }

    /**
     * 解密字符
     *
     * @param ciphertext 加密的字符
     * @param privateKey 私钥
     * @return 解密的字符
     * @throws Exception 异常
     */
    public static byte[] decrypt(byte[] ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(ciphertext);
    }

}
