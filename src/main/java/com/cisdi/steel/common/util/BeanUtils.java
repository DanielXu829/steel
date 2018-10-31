package com.cisdi.steel.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * <p>Description:  map转换工具 </p>
 * <p>email: ypasdf@163.com </p>
 * <p>Copyright: Copyright (c) 2018 </p>
 * <P>Date: 2018/3/25 </P>
 *
 * @author common
 * @version 1.0
 */
@Slf4j
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

    private static final String EXCLUDE_UID = "serialVersionUID";

    /**
     * 类转换为map
     *
     * @param bean 转换的bean
     * @param isExcludeUid         是否排除序列化
     * @param isExcludeNullOrEmpty 是否排除空或者null值
     * @param isAsc                是否ascii排序
     * @return 结果
     */
    public static Map<String, Object> beanToMap(Object bean,
                                                boolean isExcludeUid, boolean isExcludeNullOrEmpty, boolean isAsc) {
        if (null == bean) {
            return null;
        }

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                //解除访问权限
                field.setAccessible(true);
                //属性名
                String key = field.getName();

                //排除序列化产生的serialVersionUID
                if (isExcludeUid && !StringUtils.isEmpty(key)
                        && EXCLUDE_UID.equals(key)) {
                    continue;
                }

                //值
                Object value = field.get(bean);
                //排除值为空的字段
                if (isExcludeNullOrEmpty && null == value) {
                    continue;
                }
                result.put(key, value);
                if (isAsc) {
                    result = ascSort(result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("bean转换map出错");
        }
        return result;
    }

    /**
     * 对map进行ascII排序
     *
     * @param map 排序的集合
     * @return 排序后的结果
     */
    public static Map<String, Object> ascSort(Map<String, Object> map) {

        Map<String, Object> result = new LinkedHashMap<>();
        Set<String> keySet = map.keySet();
        String[] array = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
        for (String key : array) {
            result.put(key, map.get(key));
        }

        return result;
    }

    /**
     * 类转换为map
     * 默认排除UID属性
     * 默认不排除为null值
     *
     * @param bean 转换的javabean
     * @return 结果
     */
    public static Map<String, Object> beanToMap(Object bean) {
        return beanToMap(bean, true, false, false);
    }

    /**
     * map转换bean
     *
     * @param map   数据
     * @param clazz 转换的类型
     * @return 结果
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        T bean = null;
        try {
            bean = clazz.newInstance();
            //获取属性集合
            Field[] fields = clazz.getDeclaredFields();
            // 获取所有方法
            for (Field field : fields) {
                field.setAccessible(true);
                String key = field.getName();
                //排除序列化产生的serialVersionUID
                if (!StringUtils.isEmpty(key) && EXCLUDE_UID.equals(key)) {
                    continue;
                }

                Object value = map.get(key);
                field.set(bean, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("map转换bean出错");
        }
        return bean;
    }

    /**
     * 判断是否没有某个字段
     * @param bean 需要判断的bean
     * @param name 名称
     * @return true存在 false 不存在
     */
    public static boolean hasProperty(Object bean,String name){
        if (null == bean) {
            return false;
        }
        try {
            // 不抛异常 说明存在
            bean.getClass().getDeclaredField(name);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
