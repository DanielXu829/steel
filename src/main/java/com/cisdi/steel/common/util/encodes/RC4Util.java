/**
 * @Project RTMS
 * @Title RC4Util.java
 * @Package com.rainbow.util.security
 * @Description RC4加密
 * @author tuxy
 * @date 2013-6-8 下午4:07:25
 * @Copyright ©2013 Chongqing Rainbow Technology Co., Ltd. All Rights Reserved.
 * @version V1.0
 */
package com.cisdi.steel.common.util.encodes;

/**
 * RC4加密
 * RC4算法是一种在电子信息领域加密的技术手段，用于无线通信网络，是一种电子密码，只有经过授权（缴纳相应费用）的用户才能享受该服务
 * @author 95765
 */
public class RC4Util {

    public static String RC4Util(String aInput, String aKey) {
        int[] iS = new int[256];
        byte[] iK = new byte[256];

        for (int i = 0; i < 256; i++) {
            iS[i] = i;
        }

        int j = 1;

        for (short i = 0; i < 256; i++) {
            iK[i] = (byte) aKey.charAt((i % aKey.length()));
        }

        j = 0;

        for (int i = 0; i < 255; i++) {
            j = (j + iS[i] + iK[i]) % 256;
            int temp = iS[i];
            iS[i] = iS[j];
            iS[j] = temp;
        }


        int i = 0;
        j = 0;
        char[] iInputChar = aInput.toCharArray();
        char[] iOutputChar = new char[iInputChar.length];
        for (short x = 0; x < iInputChar.length; x++) {
            i = (i + 1) % 256;
            j = (j + iS[i]) % 256;
            int temp = iS[i];
            iS[i] = iS[j];
            iS[j] = temp;
            int t = (iS[i] + (iS[j] % 256)) % 256;
            int iY = iS[t];
            char iCY = (char) iY;
            iOutputChar[x] = (char) (iInputChar[x] ^ iCY);
        }
        return new String(iOutputChar);

    }

    public static void main(String[] args) {
        String inputStr = "做个好男人fasdfasdf";
        String key = "abcdefgasdfasdf";

        String str = RC4Util(inputStr, key);

        //打印加密后的字符串
        System.out.println(str);

        //打印解密后的字符串
        System.out.println(RC4Util(str, "abcdefgasdfasdf"));
    }

}
