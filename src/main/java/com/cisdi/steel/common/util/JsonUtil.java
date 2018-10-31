package com.cisdi.steel.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {
    
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 转换为json字符串
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 将json转换成对象Class
     *
     * @param src
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(String src, Class<T> clazz) {
        if (StringUtils.isEmpty(src) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) src : objectMapper.readValue(src, clazz);
        } catch (Exception e) {
            log.warn("Parse Json to Object error", e);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 将json转换成对象TypeReference
     * @param src
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T>T jsonToObject(String src, TypeReference<T> typeReference){
        if(StringUtils.isEmpty(src) || typeReference == null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class) ? src : objectMapper.readValue(src, typeReference));
        } catch (Exception e) {
            log.warn("Parse Json to Object error",e);
            e.printStackTrace();
            return null;
        }
    }
}