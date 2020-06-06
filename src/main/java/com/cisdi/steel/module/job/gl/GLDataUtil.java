package com.cisdi.steel.module.job.gl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.dto.response.SuccessEntity;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class GLDataUtil {

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private HttpProperties httpProperties;

    /**
     * 获取出铁间批数
     * @param version
     * @param dateQuery
     * @return api数据
     */
    public BigDecimal getCountChargeNumByTapTimeRange(String version, DateQuery dateQuery) {
        BigDecimal data = null;
        try {
            Map<String, String> queryParam = new HashMap();
            queryParam.put("startTime",  Objects.requireNonNull(dateQuery.getStartTime().getTime()).toString());
            queryParam.put("endTime",  Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString());
            String chargeVarInfoStr = httpUtil.get(getCountChargeNumByTapTimeRangeUrl(version), queryParam);

            if (StringUtils.isNotBlank(chargeVarInfoStr)) {
                SuccessEntity<BigDecimal> successEntity = JSON.parseObject(chargeVarInfoStr, new TypeReference<SuccessEntity<BigDecimal>>() {});
                data = successEntity.getData();
                if (Objects.isNull(data)) {
                    log.warn("根据时间[{}],[{}]获取的出铁间批数 数据为空", dateQuery.getStartTime(), dateQuery.getEndTime());
                }
            }
        } catch (Exception e) {
            log.error(String.format("根据时间[{}],[{}]获取的出铁间批数出错", dateQuery.getStartTime(), dateQuery.getEndTime()), e);
        }
        return data;
    }

    /**
     * 获取一级品率
     * @param version
     * @param dateQuery
     * @return api数据
     */
    public BigDecimal getFirstGradeRateInRange(String version, DateQuery dateQuery) {
        BigDecimal data = null;
        try {
            Map<String, String> queryParam = new HashMap();
            queryParam.put("startTime",  Objects.requireNonNull(dateQuery.getStartTime().getTime()).toString());
            queryParam.put("endTime",  Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString());
            String chargeVarInfoStr = httpUtil.get(getFirstGradeRateInRangeUrl(version), queryParam);

            if (StringUtils.isNotBlank(chargeVarInfoStr)) {
                SuccessEntity<BigDecimal> successEntity = JSON.parseObject(chargeVarInfoStr, new TypeReference<SuccessEntity<BigDecimal>>() {});
                data = successEntity.getData();
                if (Objects.isNull(data)) {
                    log.warn("根据时间[{}],[{}]获取的一级品率 数据为空", dateQuery.getStartTime(), dateQuery.getEndTime());
                }
            }
        } catch (Exception e) {
            log.error(String.format("根据时间[{}],[{}]获取的一级品率出错", dateQuery.getStartTime(), dateQuery.getEndTime()), e);
        }
        return data;
    }


    /**
     * 获取出铁间批数 url
     * @param version
     * @return
     */
    private String getCountChargeNumByTapTimeRangeUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/report/tap/countChargeNumByTapTimeRange";
    }

    /**
     * 获取一级品率 url
     * @param version
     * @return
     */
    private String getFirstGradeRateInRangeUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/report/tap/getFirstGradeRateInRange";
    }
}
