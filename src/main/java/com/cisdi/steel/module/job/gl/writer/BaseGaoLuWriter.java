package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.ChargeDTO;
import com.cisdi.steel.dto.response.gl.MaterialExpendDTO;
import com.cisdi.steel.dto.response.gl.TagValueListDTO;
import com.cisdi.steel.dto.response.gl.res.BatchData;
import com.cisdi.steel.dto.response.gl.res.BatchDistribution;
import com.cisdi.steel.dto.response.gl.res.MaterialExpend;
import com.cisdi.steel.dto.response.gl.res.TagValue;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseGaoLuWriter extends AbstractExcelReadWriter {

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
            if (Objects.isNull(chargeDTO) || CollectionUtils.isEmpty(chargeDTO.getData())) {
                log.warn("根据chargeNo [{}] 获取chargeDTO数据为空", chargeNo);
            }
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
            if (Objects.isNull(tagValueListDTO) || CollectionUtils.isEmpty(tagValueListDTO.getData())) {
                log.warn("根据tagName[{}]获取[{}]的TagValueListDTO数据为空", tagName, query.getStartTime());
            } else {
                // 排序，默认按chargeNo从小到大排序，即时间从老到新
                tagValueListDTO.getData().sort(Comparator.comparing(TagValue::getVal));
            }
        }
        return tagValueListDTO;
    }

    /**
     * 每日0点0分0秒获取每日炉料消耗数据，可用于月报展示
     * @param version
     * @return api数据
     */

    protected MaterialExpendDTO getMaterialExpendDTO(String version, Date date) {
        return getMaterialExpendDTO(version, date, null);
    }

    protected MaterialExpendDTO getMaterialExpendDTO(String version, Date date, String granularity) {
        MaterialExpendDTO materialExpendDTO = null;
        Map<String, String> queryParam = new HashMap();
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        queryParam.put("dateTime",  String.valueOf( dateBeginTime.getTime()));
        if (StringUtils.isNotBlank(granularity)) {
            queryParam.put("granularity", granularity);
        }

        String materialExpendUrl = httpProperties.getGlUrlVersion(version) + "/report/material/materialExpend";
        String materialExpendDTOStr = httpUtil.get(materialExpendUrl, queryParam);
        if (StringUtils.isNotBlank(materialExpendDTOStr)) {
            materialExpendDTO = JSON.parseObject(materialExpendDTOStr, MaterialExpendDTO.class);
            if (Objects.isNull(materialExpendDTO) || CollectionUtils.isEmpty(materialExpendDTO.getData())) {
                log.warn("根据granularity[{}]获取[{}]的MaterialExpendDTO数据为空", granularity, dateBeginTime);
            }
        }
        return materialExpendDTO;
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
     * 获取回用焦丁  元数据
     * @param materialExpendDTO
     * @return
     */
    protected BigDecimal getHuiYongJiaoDing(MaterialExpendDTO materialExpendDTO) {
        return getHuiYongJiaoDing(materialExpendDTO, null);
    }
    protected BigDecimal getHuiYongJiaoDing(MaterialExpendDTO materialExpendDTO, String shift) {
        BigDecimal oCount = materialExpendDTO.getData().stream()
                .filter(p -> StringUtils.endsWith(p.getMatCname(), "回用焦丁") && (StringUtils.isBlank(shift) || shift.equals(p.getWorkShift())))
                .map(MaterialExpend::getWetWgt)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return oCount;
    }

    /**
     * 获取焦炭平均批重 元数据
     * @param materialExpendDTO
     * @return
     */
    protected BigDecimal getJiaoTanPingJunPiZhong(MaterialExpendDTO materialExpendDTO) {
        return getJiaoTanPingJunPiZhong(materialExpendDTO, null);
    }
    protected BigDecimal getJiaoTanPingJunPiZhong(MaterialExpendDTO materialExpendDTO, String shift) {
        BigDecimal oCount = materialExpendDTO.getData().stream()
                .filter(p -> ("大块焦".equals(p.getMatCname()) || "小块焦".equals(p.getMatCname())) && (StringUtils.isBlank(shift) || shift.equals(p.getWorkShift())))
                .map(MaterialExpend::getWetWgt)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return oCount;
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
