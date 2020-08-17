package com.cisdi.steel.module.job.drt.writer.strategy.query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.dto.response.gl.TagValueMapDTO;
import com.cisdi.steel.dto.response.sj.req.SjTagQueryParam;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.enums.SequenceEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class SJStrategy implements HandleQueryDataStrategy {
    @Autowired
    protected HttpProperties httpProperties;

    @Autowired
    protected HttpUtil httpUtil;

    @Override
    public String getQueryUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/tagValues/tagNames";
    }

    @Override
    public String getKey() {
        return SequenceEnum.SJ4.getSequenceCode();
    }

    @Override
    public String getJsonResult(DateQuery dateQuery, String queryUrl, List<String> tagFormulas) {
        SjTagQueryParam sjTagQueryParam = new SjTagQueryParam(dateQuery.getQueryStartTime(), dateQuery.getQueryEndTime(), tagFormulas);
        SerializeConfig serializeConfig = new SerializeConfig();
        String queryJsonString = JSONObject.toJSONString(sjTagQueryParam, serializeConfig);
        return httpUtil.postJsonParams(queryUrl, queryJsonString);
    }

    @Override
    public Map<String, LinkedHashMap<Long, Double>> getTagValueMaps(DateQuery dateQuery, String version, List<String> tagFormulas) {
        String result = getJsonResult(dateQuery, version, tagFormulas);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        TagValueMapDTO tagValueMapDTO = JSON.parseObject(result, TagValueMapDTO.class);
        return Optional.ofNullable(tagValueMapDTO).map(TagValueMapDTO::getData).orElse(null);
    }
}
