package com.cisdi.steel.module.job.drt.writer.strategy.query;

import com.cisdi.steel.module.job.strategy.Key;
import com.cisdi.steel.module.job.util.date.DateQuery;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface HandleQueryDataStrategy extends Key {
    public String getQueryUrl(String version);

    public String getJsonResult(DateQuery dateQuery, String version, List<String> tagFormulas);

    public Map<String, LinkedHashMap<Long, Double>> getTagValueMaps(
            DateQuery dateQuery, String version, List<String> tagFormulas);
}
