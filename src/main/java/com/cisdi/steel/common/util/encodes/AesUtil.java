package com.cisdi.steel.common.util.encodes;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * 对称加密
 * @author yangpeng
 * @version 1.0
 * @Description AES工具类对称加密 需要依赖commons-codec
 * @date 2017/11/13
 */
public class AesUtil {

    /**
     * 加密算法
     */
    private static final String KEY_ALGORITHM = "AES";
    /**
     * 统一编码格式
     */
    private static final String CHARSET_NAME = StandardCharsets.UTF_8.name();

    /**
     * 缓存大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * key大小
     */
    private static final int KEY_SIZE = 128;

    /**
     * AES对称加密
     *
     * @param data 数据
     * @param key key
     * @return 结果
     */
    public static String encrypt(String data, String key) {
        try {
            Cipher cipher = initAESCipher(key, Cipher.ENCRYPT_MODE);
            byte[] bs = cipher.doFinal(data.getBytes(CHARSET_NAME));
            return Base64.encodeBase64URLSafeString(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * AES对称解密
     *
     * @param data 数据
     * @param key key
     * @return 结果
     */
    public static String decrypt(String data, String key) {
        try {
            Cipher cipher = initAESCipher(key, Cipher.DECRYPT_MODE);
            byte[] originBytes = Base64.decodeBase64(data);
            byte[] result = cipher.doFinal(originBytes);
            return new String(result, CHARSET_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>
     * 文件加密
     * </p>
     *
     * @param key 数据
     * @param sourceFilePath 源文件 路径
     * @param destFilePath 解密后文件路径
     * @throws Exception
     */
    public static void encryptFile(String key, String sourceFilePath, String destFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        encryptFile(key, sourceFile, destFile);
    }


    /**
     * <p>
     * 文件加密
     * </p>
     *
     * @param key  key
     * @param sourceFile 源文件
     * @param destFile 解密后文件
     * @return 解密后文件
     */
    public static File encryptFile(String key, File sourceFile, File destFile) throws IOException {
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            if (destFile.exists()) {
                destFile.delete();
            }
            destFile.createNewFile();
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destFile);
            Cipher cipher = initAESCipher(key, Cipher.ENCRYPT_MODE);
            // 以加密流写入文件
            CipherInputStream cipherInputStream = new CipherInputStream(
                    in, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = cipherInputStream.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
        }
        return destFile;
    }

    /**
     * <p>
     * 文件解密
     * </p>
     *
     * @param key key
     * @param sourceFile 源文件
     * @param destFile 解密后文件
     * @return 解密后文件
     */
    public static File decryptFile(String key, File sourceFile, File destFile) throws IOException {
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            if (destFile.exists()) {
                destFile.delete();
            }
            destFile.createNewFile();
            FileInputStream in = null;
            try {
                in = new FileInputStream(sourceFile);
                FileOutputStream out = new FileOutputStream(destFile);
                Cipher cipher = initAESCipher(key, Cipher.DECRYPT_MODE);
                CipherOutputStream cout = new CipherOutputStream(out, cipher);
                byte[] cache = new byte[CACHE_SIZE];
                int nRead = 0;
                while ((nRead = in.read(cache)) != -1) {
                    cout.write(cache, 0, nRead);
                    cout.flush();
                }
                cout.close();
                out.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                in.close();
            }
        }
        return destFile;
    }

    /**
     * <p>
     * 文件解密
     * </p>
     *
     * @param key key
     * @param sourceFilePath 源文件路径
     * @param destFilePath 解密后文件路径
     * @throws Exception
     */
    public static void decryptFile(String key, String sourceFilePath, String destFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        decryptFile(key,sourceFile,destFile);
    }

    /**
     * 初始化 AES Cipher
     *
     * @param sKey
     * @param cipherMode
     * @return
     */
    public static Cipher initAESCipher(String sKey, int cipherMode) {
        // 创建Key gen
        KeyGenerator keyGenerator;
        Cipher cipher = null;
        try {
            keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
            keyGenerator.init(KEY_SIZE, new SecureRandom(sKey.getBytes()));
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] codeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(codeFormat, KEY_ALGORITHM);
            cipher = Cipher.getInstance(KEY_ALGORITHM);
            // 初始化
            cipher.init(cipherMode, key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return cipher;
    }


    /**
     * <p>
     * 生成随机密钥
     * </p>
     *
     * @return 获取密钥
     * @throws Exception
     */
    private static String getSecretKey() throws Exception {
        return getSecretKey(null);
    }

    /**
     * <p>
     * 生成密钥
     * </p>
     *
     * @param seed 密钥种子
     * @return
     * @throws Exception
     */
    public static String getSecretKey(String seed) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom;
        if (seed != null && !"".equals(seed)) {
            secureRandom = new SecureRandom(seed.getBytes());
        } else {
            secureRandom = new SecureRandom();
        }
        keyGenerator.init(KEY_SIZE, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.encodeBase64URLSafeString(secretKey.getEncoded());
    }

    public static void main(String[] args) throws Exception {
//        String sKey = getSecretKey();
//        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
//        keyGenerator.init(KEY_SIZE, new SecureRandom(sKey.getBytes()));
//        SecretKey secretKey = keyGenerator.generateKey();
//        byte[] codeFormat = secretKey.getEncoded();
//        SecretKeySpec key = new SecretKeySpec(codeFormat, KEY_ALGORITHM);
//        System.err.println(key.getEncoded().length);
//
//        String enr = encrypt("{'mobile':'18980840843','code':'8060','platform':'android','channelId':12454348}", sKey);
//        System.out.println(enr);
//
//        String decrypt = decrypt(enr, sKey);
//        System.out.println(sKey);
//        System.out.println(decrypt);

//        encryptFile(sKey, "C:\\Users\\yp\\Desktop\\安全防护措施.docx", "C:\\Users\\yp\\Desktop\\施.docx");
//        decryptFile(sKey,"C:\\Users\\yp\\Desktop\\施.docx","C:\\\\Users\\\\yp\\\\Desktop\\\\21.docx");
    }
}
