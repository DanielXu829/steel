package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.ChargeDTO;
import com.cisdi.steel.dto.response.gl.TagValueListDTO;
import com.cisdi.steel.dto.response.gl.res.BatchDistribution;
import com.cisdi.steel.dto.response.gl.res.TagValue;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.util.date.DateQuery;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseShangLiaoBuLiaoWriter extends AbstractExcelReadWriter {

    /**
     * 根据chargeNo获取charge raw data
     * @param version
     * @param chargeNo
     * @return api数据
     */
    protected ChargeDTO getChargeDTO(String version, Integer chargeNo) {
        ChargeDTO chargeDTO = null;
        String chargeDTOStr = httpUtil.get(getPrimaryUrl(version, chargeNo));
        if (StringUtils.isNotBlank(chargeDTOStr)) {
            chargeDTO = JSON.parseObject(chargeDTOStr, ChargeDTO.class);
        }
        return chargeDTO;
    }

    /**
     * 获取chargeNo 列表
     * @param query
     * @param version
     * @return
     */
    protected TagValueListDTO getTagValueListDTO(DateQuery query, String version, String tagName) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("starttime",  Objects.requireNonNull(query.getStartTime().getTime()).toString());
        queryParam.put("endtime",  Objects.requireNonNull(query.getEndTime().getTime()).toString());
        queryParam.put("tagname", tagName);
        String chargeNoData = httpUtil.get(getChargeNoUrl(version), queryParam);

        TagValueListDTO tagValueListDTO = null;
        if (StringUtils.isNotBlank(chargeNoData)) {
            tagValueListDTO = JSON.parseObject(chargeNoData, TagValueListDTO.class);
            // 排序，默认按chargeNo从小到大排序，即时间从老到新
            tagValueListDTO.getData().sort(Comparator.comparing(TagValue::getVal));
        }
        return tagValueListDTO;
    }

    protected String getRoundact(List<BatchDistribution> distributions) {
        //排序, 按position倒序
        distributions.sort(Comparator.comparing(BatchDistribution::getPosition).reversed());
        //过滤weightact不为0的值
        List<BatchDistribution> collect = distributions.stream()
                .filter(p -> p.getWeightset().doubleValue() > 0)
                .collect(Collectors.toList());
        // 拼接roundset
        return collect.stream()
                .map(p -> String.valueOf(p.getRoundact()))
                .collect(Collectors.joining(""));
    }

    protected String getPosition(List<BatchDistribution> distributions) {
        //排序, 按position倒序
        distributions.sort(Comparator.comparing(BatchDistribution::getPosition).reversed());
        //过滤weightact不为0的值
        List<BatchDistribution> collect = distributions.stream()
                .filter(p -> p.getWeightset().doubleValue() > 0)
                .collect(Collectors.toList());
        // 拼接position
        return collect.stream()
                .map(p -> String.valueOf(p.getPosition()))
                .collect(Collectors.joining(""));
    }

    /**
     * 获取charge/rawdata的url
     * @param version
     * @return url
     */
    protected String getPrimaryUrl(String version, Integer chargeNo) {
        return httpProperties.getGlUrlVersion(version) + "/charge/rawdata/" + chargeNo;
    }

    /**
     * 获取/tagValues的url
     * @param version
     * @return
     */
    protected String getChargeNoUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValues";
    }

}
