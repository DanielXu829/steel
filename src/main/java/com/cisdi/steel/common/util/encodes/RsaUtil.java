package com.cisdi.steel.common.util.encodes;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 非对称加密
 *
 * @author yangpeng
 * @version 1.0
 * @Description RSA工具类 需要依赖commons-codec
 * @date 2017/11/13
 */
public class RsaUtil {
    /**
     * 算法
     */
    private static final String KEY_ALGORITHM = "RSA";
    /**
     * 签名
     */
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * key size
     */
    private static final int KEY_SIZE = 1024;

    /**
     * base64 解密
     * @param key
     * @return
     */
    public static byte[] decryptBASE64(String key) {
        return Base64.decodeBase64(key);
    }

    /**
     * base64 编码传输
     * @param bytes
     * @return
     */
    public static String encryptBASE64(byte[] bytes) {
        return Base64.encodeBase64URLSafeString(bytes);
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       加密数据
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        // 解密由base64编码的私钥
        byte[] keyBytes = decryptBASE64(privateKey);
        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);
        return encryptBASE64(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return 校验成功返回true 失败返回false
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        // 解密由base64编码的公钥
        byte[] keyBytes = decryptBASE64(publicKey);
        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);
        // 验证签名是否正常
        return signature.verify(decryptBASE64(sign));
    }

    /**
     * 通过私钥解密
     *
     * @param data 需要解密的数据
     * @param key  私钥
     * @return 返回解密的结果
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 解密<br>
     * 用私钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(String data, String key)
            throws Exception {
        return decryptByPrivateKey(decryptBASE64(data), key);
    }

    /**
     * 解密<br>
     * 用公钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 加密<br>
     * 用公钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(String data, String key)
            throws Exception {
        // 对公钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 取得私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Key> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return encryptBASE64(key.getEncoded());
    }

    /**
     * 取得公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Key> keyMap)
            throws Exception {
        Key key = keyMap.get(PUBLIC_KEY);
        return encryptBASE64(key.getEncoded());
    }

    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Key> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        Map<String, Key> keyMap = new HashMap(2);
        // 公钥
        keyMap.put(PUBLIC_KEY, keyPair.getPublic());
        // 私钥
        keyMap.put(PRIVATE_KEY, keyPair.getPrivate());
        return keyMap;
    }

    public static void main(String[] args) throws Exception {
        // 生成秘钥
        Map<String, Key> keyMap = initKey();
        // 获取公匙
        String publicKey = getPublicKey(keyMap);
        // 获取私匙
        String privateKey = getPrivateKey(keyMap);

        // 1
        String data= "tSArqZBYMc2MVD/Ityri5J1atoDxx12dXkkzYaRJzo/mHzM7XA78FpqUvGp1Nmn9jUtomN307OWYv52Cv3LvfYvbm27RN2I5Oi82YllSPA2/oO8nJ32XlRboCwEL4S9NkXlul5dMcvGC3aO/yseh8m+LUhWgoKu+GoteynI1vRo=";
        byte[] bytes = Base64.decodeBase64(data);
        privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALvEdzmPsh/v32Fg\n" +
                "L/TR+vKgUGOOsjIaUmRuAVMPf0tq/xH79J5G+RPmpm6wjBvvkvAG4mDj6iT6su0L\n" +
                "CYkudbwgWfH4gWnXXLo7jZxlEa9ghFWaAE+L8aOSbyltfeFGyi7bKecIPodZ4Grz\n" +
                "iIBIMfr3Q9M+reNiEAd3SFVHvCqxAgMBAAECgYEAs3zHl0+EL0FGIDgpP3bl0r5q\n" +
                "efCy6G/+6J+7RmHfBgHel5top1Ai0uI9oDvFgwLNTALYrVPQw86d8TgGwoqV4T9G\n" +
                "X8+9/yRI8DN2w7HaKome23GtugyKBCKrKcS3e1M/0KNV6W9uSB/vIodQLVFDWmfQ\n" +
                "fJOaeOrffONUQtUKTG0CQQDbkQq923rA6MxuTzNeVJ+ulgcofmDHUhBMrP+E1PlR\n" +
                "+QOlzV8GudKEKXnYVZ1L2EQAU3HcRN0rkDW9h0czQGnLAkEA2uyhU6yL0vc/QyLh\n" +
                "LhMdbzHH1ea4clTVHRoMQivOIaX0p4+/Ni5LGDQEIGXahm5NpC6PtyyMNEQ3VZu+\n" +
                "4Ftd8wJAIbQl/fbO5QXXO6eUrwo2aMpG1wRvPqluLxbvMiivDNI4qR7ZU2L62aa0\n" +
                "OW0K9DWCWrp0Y+d+O82rMiit2UQt+QJAEu8MNNRjc2d2zTOjDf0ROqCvi8xcf2be\n" +
                "15l9Hevz8+0Kb8N64hC25ez5vmOQtrerd1ufilQL/Ck6L+k8ZOMCCQJBAIiut+Z1\n" +
                "VCBD1beT8cUNLbLK5SC9mKdxlnASNNuD8GXK+JfGERsN2kzi4cdLorwRUBfYOr7P\n" +
                "uQMhHeUAEQ56gw4=";
        byte[] bytes1 = decryptByPrivateKey(bytes, privateKey);
        System.err.println(new String(bytes1));
//        String data = "?`1`11`我市实施发塞弗茶赛肯红安来肯/+345345?\\]\\[\\][\\][";
//
//        // 用公钥加密
//        byte[] bytes = encryptByPublicKey(data, publicKey);
//        System.out.println(new String(bytes));
//        System.out.println(bytes.length);
//
//        // 用私钥解密
//        byte[] bytes1 = decryptByPrivateKey(bytes, privateKey);
//        System.out.println(new String(bytes1));
//        System.out.println(bytes1.length);

        // 2

        // 原始数据 明文
//        String data= "?afskdufyaso23423./。、·1·、=-324.+-*/21312fadfa;/.\\asdfa";
//        // aes 的key
//        String key= "asdfasdf";
//        // 先对明文进行对称加密
//        String result = AesUtil.encrypt(data, key);
//        System.out.println("AES加密结果:"+result);
//        System.out.println(result.length());
//        // 对key进行加密
//        byte[] bytes = encryptByPublicKey(key, publicKey);
//        // 对key进行base64传输
//        String s = encryptBASE64(bytes);
//        // 传输的数据
//        System.out.println("传输:"+s);
//        System.out.println(s.length());
//
//        /**
//         * 对key进行解密
//         */
//        byte[] bytes1 = decryptByPrivateKey(s, privateKey);
//        // 用aes解密
//        String decrypt = AesUtil.decrypt(result, new String(bytes1));
//        // 最后结果
//        System.out.println(decrypt);
//        // 判断是否相等
//        System.out.println(decrypt.equals(data));
//
//        System.out.println(keyMap);
//        System.out.println("-------------------------------");
//        System.out.println(publicKey);
//        System.out.println("--------------------------------");
//        System.out.println(privateKey);
//        System.out.println("-----------------------------------");

        // 3

        // 用私钥加密
//        byte[] encryptByPrivateKey = encryptByPrivateKey("123456".getBytes(),privateKey);
//        // 用公钥加密
//        byte[] encryptByPublicKey = encryptByPublicKey("123456",publicKey);
//        // 加密后的字符串
//        System.out.println(new String(encryptByPrivateKey));
//        System.out.println("--------------------------------");
//        System.out.println(new String(encryptByPublicKey));
//        System.out.println("--------------------------------");
//
//        // 用私钥生成数字签名
//        String sign = sign(encryptByPrivateKey,privateKey);
//        System.out.println("签名："+sign);
//        System.out.println("--------------------------------");
//        // 验证签名是否正确
//        boolean verify = verify(encryptByPrivateKey,publicKey,sign);
//        System.out.println("结果:"+verify);
//        System.out.println("-----------------------------------");
//        // 用公钥解密
//        byte[] decryptByPublicKey = decryptByPublicKey(encryptByPrivateKey,publicKey);
//        // 用私钥解密
//        byte[] decryptByPrivateKey = decryptByPrivateKey(encryptByPublicKey,privateKey);
//        System.out.println(new String(decryptByPublicKey));
//        System.out.println("-----------------------------------");
//        System.out.println(new String(decryptByPrivateKey));

    }
}