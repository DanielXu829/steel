package com.cisdi.steel.module.job.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.util.StringUtils;

import java.util.Objects;

public class FastJSONUtil {

    /**
     * 将JSON串转换为JSON数组
     * @param data
     * @return
     */
    public static JSONArray convertJsonStringToJsonArray(String data) {
        JSONArray dataArray = null;
        if (StringUtils.isNotBlank(data)) {
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (Objects.nonNull(jsonObject)) {
                dataArray = jsonObject.getJSONArray("data");
            }
        }

        return dataArray;
    }
}
