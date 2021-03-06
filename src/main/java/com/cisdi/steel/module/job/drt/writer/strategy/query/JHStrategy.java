package com.cisdi.steel.module.job.drt.writer.strategy.query;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.jh.res.JHTagValueListDTO;
import com.cisdi.steel.dto.response.jh.res.TagValue;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.enums.SequenceEnum;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JHStrategy extends BaseStrategy {
    @Override
    public String getQueryUrl(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getNewTagValue";
    }

    @Override
    public String getKey() {
        return SequenceEnum.JH910.getSequenceCode();
    }

    @Override
    public String getJsonResult(DateQuery dateQuery, String version, List<String> tagFormulas) {
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("startDate", String.valueOf(dateQuery.getQueryStartTime()));
        queryParam.put("endDate", String.valueOf(dateQuery.getQueryEndTime()));
        String searchParam = StringUtils.join(tagFormulas, ",");
        queryParam.put("tagNames", searchParam);
        return httpUtil.get(getQueryUrl(version), queryParam);
    }

    @Override
    public Map<String, LinkedHashMap<Long, Double>> getTagValueMaps(DateQuery dateQuery, String version, List<String> tagFormulas) {
        String result = getJsonResult(dateQuery, version, tagFormulas);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        // 去除返回json字符串中的total : xxx, 防止解析JSON失败
        String totalRegex = "\"total\":.*?,";
        result = result.replaceFirst(totalRegex, StringUtils.EMPTY);
        JHTagValueListDTO jhTagValueListDTO = JSON.parseObject(result, JHTagValueListDTO.class);
        LinkedHashMap<String, List<TagValue>> jhTagValueMap =
                Optional.ofNullable(jhTagValueListDTO).map(JHTagValueListDTO::getData).orElse(null);
        if (MapUtils.isEmpty(jhTagValueMap)) {
            return null;
        }
        Map<String, LinkedHashMap<Long, Double>> tagValueMaps = new HashMap<>();
        jhTagValueMap.forEach((key, value) -> {
            LinkedHashMap<Long, Double> timeToValueMap = new LinkedHashMap<>();
            value.forEach(tagValue -> {
                if (Objects.nonNull(tagValue)) {
                    timeToValueMap.put(tagValue.getClock().getTime(), tagValue.getVal().doubleValue());
                }
            });
            tagValueMaps.put(key, timeToValueMap);
        });
        return tagValueMaps;
    }
}
