package com.cisdi.steel.module.job.sj.writer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.dto.response.sj.req.SjTagQueryParam;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;

public abstract class BaseShaoJieWriter extends AbstractExcelReadWriter {

    /**
     * 通过tag点拿数据的API
     * @param version
     * @return
     */
    protected String getTagUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/tagValues/tagNames";
    }

    /**
     * 通过tag点拿数据
     * @param version
     * @param sjTagQueryParam
     * @return
     */
    protected String getTagValue(String version, SjTagQueryParam sjTagQueryParam) {
        SerializeConfig serializeConfig = new SerializeConfig();
        String queryJsonString = JSONObject.toJSONString(sjTagQueryParam, serializeConfig);
        return httpUtil.postJsonParams(this.getTagUrl(version), queryJsonString);
    }

    /**
     * 运转率接口url
     * @param version
     * @return
     */
    protected String getYarnRunStatisticsForFullDayUrl(String version) {
        return httpProperties.getSJUrlVersion(version) + "/report/yarnRunStatisticsForFullDay";
    }

    /**
     * 运转率接口返回数据
     * @param version
     * @param timestamp
     * @return
     */
    protected String getYarnRunStatisticsForFullDayData(String version, Long timestamp) {
        String yarnRunStatisticsForFullDayUrl = getYarnRunStatisticsForFullDayUrl(version);
        return httpUtil.get(yarnRunStatisticsForFullDayUrl + "?clock=" + timestamp);
    }

    /**
     * 每周产量分析，上周均值接口
     * @param version
     * @return
     */
    protected String getAnalysisOfOutPut(String version) {
        return httpProperties.getSJUrlVersion(version) + "/report/analysisOfOutPut";
    }

    protected String getAnalysisOfOutPutData(String version, Long timestamp) {
        String analysisOfOutPutUrl = getAnalysisOfOutPut(version);
        return httpUtil.get(analysisOfOutPutUrl + "?clock=" + timestamp);
    }
}
