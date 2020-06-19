package com.cisdi.steel.module.job.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class FastJSONUtil {

    /**
     * 将JSON串转换为JSON数组
     * @param data
     * @return
     */
    public static JSONArray convertJsonStringToJsonArray(String data) {
        return convertJsonStringToJsonArray(data, "data");
    }

    /**
     * 将JSON串转换为JSON数组
     * @param data
     * @param key
     * @return
     */
    public static JSONArray convertJsonStringToJsonArray(String data, String key) {
        JSONArray dataArray = null;
        if (StringUtils.isNotBlank(data)) {
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (Objects.nonNull(jsonObject)) {
                dataArray = jsonObject.getJSONArray(key);
            }
        }
        return dataArray;
    }

    /**
     * 根据keys获取所需的JSONObject
     * @param data
     * @param keys
     * @return
     */
    public static JSONObject getJsonObjectByKey(String data, List<String> keys) {
        JSONObject jsonObject = null;
        try {
            if (StringUtils.isNotBlank(data)) {
                jsonObject = JSONObject.parseObject(data);
                for (String key:keys) {
                    if (Objects.nonNull(jsonObject)) {
                        jsonObject = jsonObject.getJSONObject(key);
                    }
                }
            }
        } catch (Exception e) {
            log.error("字符串转化为json object时产生错误，data:" + data, e);
        }
        return jsonObject;
    }

    public static <T> T getJsonValueByKey(String data, List<String> keys, String key, Class<T> clazz) {
        T t = null;
        try {
            JSONObject jsonObject = getJsonObjectByKey(data, keys);
            if (Objects.nonNull(jsonObject)) {
                t = jsonObject.getObject(key, clazz);
            }
        } catch (Exception e) {
            log.error("json object取值时产生错误，key:" + key, e);
        }
        return t;
    }

    public static <T> T getJsonValueByKey(JSONObject jsonObject, String key, Class<T> clazz) {
        T t = null;
        try {
            if (Objects.nonNull(jsonObject)) {
                t = jsonObject.getObject(key, clazz);
            }
        } catch (Exception e) {
            log.error("json object取值时产生错误，key:" + key, e);
        }
        return t;
    }

}
