package com.cisdi.steel.common.resp;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * <p>Description:  测试类    </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/19 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class ResponseUtil {

    /**
     * 直接返回data里面的数据
     *
     * @param str 解析的字符日
     * @return 结果
     */
    public static String getResponse(String str) {
        return JSONObject.parseObject(str, Response.class).getData();
    }

    /**
     * 集合
     *
     * @param str 解析的自身穿
     * @param <T> 自定义类型
     * @return 结果
     */
    public static <T> List<T> getResponseArray(String str, Class<T> clazz) {
        return JSONObject.parseArray(getResponse(str), clazz);
    }

    /**
     * 对象
     *
     * @param str 解析的自身穿
     * @param <T> 自定义类型
     * @return 结果
     */
    public static <T> T getResponseObject(String str, Class<T> clazz) {
        return JSONObject.parseObject(getResponse(str), clazz);
    }

}
