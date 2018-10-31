package com.cisdi.steel.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 对象操作工具类, 继承org.apache.commons.lang3.ObjectUtils类
 *
 * @author Administrator
 * @version 2014-6-29
 */
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {

    /**
     * 序列化对象
     *
     * @param object 需要序列化的对象
     * @return 结果
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos;
        ByteArrayOutputStream baos;
        try {
            if (object != null) {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反序列化对象
     *
     * @param bytes 字节
     * @return 结果
     */
    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais;
        try {
            if (bytes != null && bytes.length > 0) {
                bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                return ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
