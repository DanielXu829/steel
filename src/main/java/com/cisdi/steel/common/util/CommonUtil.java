package com.cisdi.steel.common.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Description:  通用的工具类  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng  2018/2/12
 * @version 1.0
 * @since 1.8
 */
public class CommonUtil {

    /**
     * 特别提醒：不要将返回的结果集返回给页面
     * 原因： 集合中long类型 返回为json结果 精读丢失 。。。。。。。。。。。
     * 操蛋 。。。。
     * 将集合Object转换成Long的类型
     * 主要是将本身Object是Long类型的值，转换成Long的集合
     *
     * @param objects 需要转换的集合Object本身为Long类型
     * @return Long的集合 hashset
     */
    public static Set<Long> convertLong(List<Object> objects) {
        if (Objects.isNull(objects) || objects.isEmpty()) {
            return null;
        }
        Set<Long> result = new HashSet<>(objects.size());
        objects.forEach(item -> {
            // 添加进集合
            Optional<Object> item1 = Optional.ofNullable(item);
            item1.ifPresent(o -> result.add(Long.parseLong(o.toString())));
        });
        return result;
    }

    /**
     * 将集合Object转换成String的类型
     * 主要是将本身Object是String类型的值，转换成String的集合
     *
     * @param objects 需要转换的集合Object本身为Long类型
     * @return Long的集合 hashset
     */
    public static Set<String> convertString(List<Object> objects) {
        if (Objects.isNull(objects) || objects.isEmpty()) {
            return null;
        }
        Set<String> result = new HashSet<>(objects.size());
        objects.forEach(item -> {
            // 添加进集合
            Optional<Object> item1 = Optional.ofNullable(item);
            item1.ifPresent(o -> result.add(o.toString()));
        });
        return result;
    }
}
