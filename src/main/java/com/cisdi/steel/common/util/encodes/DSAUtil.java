/**
 * @Project RTMS
 * @Title DSAUtil.java
 * @Package com.rainbow.util.security
 * @author tuxy
 * @date 2013-6-8 下午4:20:28
 * @Copyright ©2013 Chongqing Rainbow Technology Co., Ltd. All Rights Reserved.
 * @version V1.0
 */
package com.cisdi.steel.common.util.encodes;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.security.*;

/**
 * DSA是基于整数有限域离散对数难题的，其安全性与RSA相比差不多。
 * DSA的一个重要特点是两个素数公开，这样，当使用别人的p和q时，
 * 即使不知道私钥，你也能确认它们是否是随机产生的，还是作了手脚。RSA算法却做不到
 *
 * @author tuxy
 * @ClassName DSAUtil
 * @Description DSA加密
 * @date 2013-6-8 下午4:20:28
 */
@Slf4j
public class DSAUtil {

    /**
     * 加密算法
     */
    private static final String KEY_ALGORITHM = "DSA";

    /**
     * key大小
     */
    private static final int KEY_SIZE = 1024;

    private static final String PUBKEY_PATH = SystemUtils.getUserDir().getAbsolutePath() + File.separator + "pubkey.dat";
    private static final String PRIKEY_PATH = SystemUtils.getUserDir().getAbsolutePath() + File.separator + "prikey.dat";
    private static final String INFO_PATH = SystemUtils.getUserDir().getAbsolutePath() + File.separator + "info.dat";

    /**
     * <p>说明： 生成一对密钥 </p>
     *
     * @return void
     */
    public static void creatPairKeys() {
        try {

            KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom sr = new SecureRandom();
            sr.setSeed("123".getBytes());// 密钥种子
            keygen.initialize(KEY_SIZE, sr);
            KeyPair keys = keygen.generateKeyPair();
            PublicKey pubkey = keys.getPublic();
            PrivateKey prikey = keys.getPrivate();
            // 将生成的密钥对序列化到文件
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(PRIKEY_PATH));
            out.writeObject(prikey);
            out.close();
            out = new ObjectOutputStream(new FileOutputStream(PUBKEY_PATH));
            out.writeObject(pubkey);
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * <p>说明： 使用私钥对数据签名 </p>
     *
     * @return void
     */
    public static void sign() {
        String s = "密文是：软博，你能看得见吗？sadfasdfasdfasdfasdfasdfasdf";
        try {
            // 导入私钥
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(PRIKEY_PATH));
            PrivateKey prikey = (PrivateKey) in.readObject();
            in.close();

            // 对数据签名
            Signature signature = Signature.getInstance(KEY_ALGORITHM);
            signature.initSign(prikey);
            signature.update(s.getBytes());
            byte[] signed = signature.sign();

            // 将签名后的数据写入文件
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(INFO_PATH));
            out.writeObject(s);
            out.writeObject(signed);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    /**
     * <p>说明： 使用公钥验证签名是否正常 </p>
     *
     * @return void
     */
    public static void check() {
        try {
            // 导入公钥
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(PUBKEY_PATH));
            PublicKey pubkey = (PublicKey) in.readObject();
            in.close();

            // 导入需要读取的文件
            in = new ObjectInputStream(new FileInputStream(INFO_PATH));
            String s = (String) in.readObject();
            byte[] signed = (byte[]) in.readObject();
            in.close();

            // 验证密钥对
            Signature signCheck = Signature.getInstance(KEY_ALGORITHM);
            signCheck.initVerify(pubkey);
            signCheck.update(s.getBytes());
            if (signCheck.verify(signed)) {
                System.out.println(s);
            } else {
                System.out.println("无阅读权限");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }


    public static void main(String[] args) throws Exception {
//		creatPairKeys();
        sign();
        check();
    }

}
