package com.cisdi.steel.common.util.encodes;

import java.math.BigInteger;

/**
 * 浪潮参数加密解密类
 *
 * @author aoyun
 */
@SuppressWarnings("ALL")
public class Snippet {
    public static void main(String[] args) {
        System.out.println(encrypt("杨家琼asdfaasdfasdfsdfasdf"));
        System.out.println(decrypt("-1962571a5149186f439e8c5e5af61e0b07a976c462"));

    }

    /**
     * 加密
     * @param s 字符日
     * @return 结果
     */
    public static  String encrypt(String s) {
        if (s == null) {
            return "";
        }
        if (s.length() == 0) {
            return "";
        } else {
            BigInteger biginteger = new BigInteger(s.getBytes());
            BigInteger biginteger1 = new BigInteger("0933910847463829232312312");
            BigInteger biginteger2 = biginteger1.xor(biginteger);
            return biginteger2.toString(16);
        }
    }

    /**
     * 解密
     * @param s
     * @return
     */
    public static String decrypt(String s) {
        if (s == null) {
            return "";
        }
        if (s.length() == 0) {
            return "";
        }
        BigInteger biginteger = new BigInteger("0933910847463829232312312");
        try {
            BigInteger biginteger1 = new BigInteger(s, 16);
            BigInteger biginteger2 = biginteger1.xor(biginteger);
            return new String(biginteger2.toByteArray());
        } catch (Exception exception) {
            return "";
        }
    }
}
