package com.cisdi.steel.module.job.drt.writer.strategy.query;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.TagValueMapDTO;
import com.cisdi.steel.dto.response.gl.req.TagQueryParam;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.enums.SequenceEnum;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class GLStrategy extends BaseStrategy {
    @Override
    public String getQueryUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    @Override
    public String getKey() {
        return SequenceEnum.GL8.getSequenceCode();
    }

    @Override
    public String getJsonResult(DateQuery dateQuery, String version, List<String> tagFormulas) {
        TagQueryParam tagQueryParam = new TagQueryParam(dateQuery.getQueryStartTime(), dateQuery.getQueryEndTime(), tagFormulas);
        return httpUtil.postJsonParams(getQueryUrl(version), JSON.toJSONString(tagQueryParam));
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
