package com.cisdi.steel.module.job.drt.writer.strategy.query;

import com.cisdi.steel.module.job.strategy.Key;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface HandleQueryDataStrategy extends Key {

    /**
     * 查询接口的
     * @param version
     * @return
     */
    String getQueryUrl(String version);

    /**
     * 获取接口返回的json串
     * @param dateQuery
     * @param version
     * @param tagFormulas
     * @return
     */
    String getJsonResult(DateQuery dateQuery, String version, List<String> tagFormulas);

    /**
     * 组装map数据
     * @param dateQuery
     * @param version
     * @param tagFormulas
     * @return
     */
    Map<String, LinkedHashMap<Long, Double>> getTagValueMaps(
            DateQuery dateQuery, String version, List<String> tagFormulas);

    /**
     * 生成查询策略
     * @param recordDate
     * @param reportTemplateConfig
     * @return
     */
    List<DateQuery> getDateQueries(Date recordDate, ReportTemplateConfig reportTemplateConfig);
}
