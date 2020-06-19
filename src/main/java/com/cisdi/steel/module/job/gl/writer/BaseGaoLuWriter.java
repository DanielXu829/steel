package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.SuccessEntity;
import com.cisdi.steel.dto.response.gl.ChargeDTO;
import com.cisdi.steel.dto.response.gl.MaterialExpendDTO;
import com.cisdi.steel.dto.response.gl.TagValueListDTO;
import com.cisdi.steel.dto.response.gl.TapTPCDTO;
import com.cisdi.steel.dto.response.gl.res.*;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.gl.GLDataUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseGaoLuWriter extends AbstractExcelReadWriter {

    // 获取批次总数 tagName
    protected static String batchCountTagName = "BF8_L2C_SH_CurrentBatch_1d_max";

    @Autowired
    protected GLDataUtil glDataUtil;

    /**
     * 获取变料信息数据
     * @param version
     * @return api数据
     */
    protected List<ChargeVarInfo> getChargeVarInfo(String version, DateQuery dateQuery, String type) {
        List<ChargeVarInfo> chargeVarInfos = null;
        Map<String, String> queryParam = new HashMap();
        queryParam.put("time",  Objects.requireNonNull(dateQuery.getRecordDate().getTime()).toString());
        queryParam.put("type",  type);
        String chargeVarInfoStr = httpUtil.get(getChargeVarInfoUrl(version), queryParam);

        if (StringUtils.isNotBlank(chargeVarInfoStr)) {
            SuccessEntity<List<ChargeVarInfo>> successEntity = JSON.parseObject(chargeVarInfoStr, new TypeReference<SuccessEntity<List<ChargeVarInfo>>>() {});
            chargeVarInfos = successEntity.getData();
            if (CollectionUtils.isEmpty(chargeVarInfos)) {
                log.warn("获取ChargeVarInfo数据为空");
            }
        }
        return chargeVarInfos;
    }

    /**
     * 获取风口信息数据
     * @param version
     * @return api数据
     */
    protected BfBlastMainInfo getBfBlastMainInfo(String version) {
        BfBlastMainInfo bfBlastMainInfo = null;
        String bfBlastMainInfoStr = httpUtil.get(getBfBlastMainInfoUrl(version));
        if (StringUtils.isNotBlank(bfBlastMainInfoStr)) {
            SuccessEntity<BfBlastMainInfo> successEntity = JSON.parseObject(bfBlastMainInfoStr, new TypeReference<SuccessEntity<BfBlastMainInfo>>() {});
            bfBlastMainInfo = successEntity.getData();
            if (Objects.isNull(bfBlastMainInfo) || CollectionUtils.isEmpty(bfBlastMainInfo.getBfBlastMains())) {
                log.warn("获取BfBlastMainInfo数据为空");
            }
        }
        return bfBlastMainInfo;
    }

    /**
     * 获取进风面积
     * @param version
     * @param time
     * @return
     */
    protected BigDecimal getBlastIntakeArea(String version, String time) {
        BigDecimal value = null;
        Map<String, String> queryParam = new HashMap();
        queryParam.put("time",  time);
        String blastIntakeArea = httpUtil.get(getBlastIntakeAreaUrl(version), queryParam);
        if (StringUtils.isNotBlank(blastIntakeArea)) {
            JSONObject data = JSON.parseObject(blastIntakeArea);
            if (Objects.nonNull(data)) {
                value = data.getBigDecimal("data");
            }
        }
        return value;
    }

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

    /**
     * 每日0点0分0秒获取每日炉料消耗数据，分班次返回数据，可用于月报展示
     * @param version
     * @param date
     * @param granularity 默认“shift”
     * @return
     */
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

    /**
     * 获取/report/tagValue/getTagValueListByRange  元数据, 返回第一个TagValue的值, 默认值为0
     * @param version
     * @param query
     * @param tagName
     * @param granularity
     * @return
     */
    protected BigDecimal getFirstTagValueByRange(String version, DateQuery query, String tagName, String granularity) {
        BigDecimal value = new BigDecimal(0);
        TagValue firstTagValueObjByRange = this.getFirstTagValueObjByRange(version, query, tagName, granularity);
        if (Objects.nonNull(firstTagValueObjByRange)) {
            value = firstTagValueObjByRange.getVal();
        }
        return value;
    }

    /**
     * 获取/report/tagValue/getTagValueListByRange  元数据, 返回第一个TagValue
     * @param version
     * @param query
     * @param tagName
     * @param granularity
     * @return
     */
    protected TagValue getFirstTagValueObjByRange(String version, DateQuery query, String tagName, String granularity) {
        List<TagValue> tagValueListByRange = this.getTagValueListByRange(version, query, tagName, granularity);
        if (CollectionUtils.isNotEmpty(tagValueListByRange)) {
            return tagValueListByRange.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取/report/tagValue/getTagValueListByRange  元数据, 返回数据
     * @param version
     * @param query
     * @param tagName
     * @param granularity
     * @return
     */
    protected List<TagValue> getTagValueListByRange(String version, DateQuery query, String tagName, String granularity) {
        List<TagValue> tagValueList = new ArrayList<>();
        Map<String, String> queryParam = new HashMap();
        queryParam.put("startTime",  Objects.requireNonNull(query.getStartTime().getTime()).toString());
        queryParam.put("endTime",  Objects.requireNonNull(query.getStartTime().getTime()).toString());
        if (StringUtils.isNotBlank(tagName)) {
            queryParam.put("tagName", tagName);
        }
        if (StringUtils.isNotBlank(granularity)) {
            queryParam.put("granularity", granularity);
        }

        String tagValueListUrl = httpProperties.getGlUrlVersion(version) + "/report/tagValue/getTagValueListByRange";
        String tagValueListDTOStr = httpUtil.get(tagValueListUrl, queryParam);
        if (StringUtils.isNotBlank(tagValueListDTOStr)) {
            TagValueListDTO tagValueListDTO= JSON.parseObject(tagValueListDTOStr, TagValueListDTO.class);
            if (CollectionUtils.isNotEmpty(tagValueListDTO.getData())) {
                tagValueList = tagValueListDTO.getData();
            } else {
                log.warn("[{}]的tagValueList为空, tagName[{}], granularity[{}]", DateFormatUtils.format(query.getRecordDate(), DateUtil.yyyyMMddChineseFormat), tagName, granularity);
            }
        }
        return tagValueList;
    }

    /**
     * 每日0点0分0秒获取每日炉料消耗数据，可用于月报展示
     * @param version
     * @return api数据
     */
    protected TapTPCDTO getTapTPCDTO(String version, Date date) {
        TapTPCDTO tapTPCDTO = null;
        Map<String, String> queryParam = new HashMap();
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        queryParam.put("dateTime",  String.valueOf(dateBeginTime.getTime()));

        String tapTPCUrl = httpProperties.getGlUrlVersion(version) + "/report/tap/getTapTPCByRange";
        String tapTPCDTOStr = httpUtil.get(tapTPCUrl, queryParam);
        if (StringUtils.isNotBlank(tapTPCDTOStr)) {
            tapTPCDTO = JSON.parseObject(tapTPCDTOStr, TapTPCDTO.class);
            if (Objects.isNull(tapTPCDTO) || CollectionUtils.isEmpty(tapTPCDTO.getData())) {
                log.warn("[{}] 的TapTPCDTO数据为空", dateBeginTime);
            }
        }
        return tapTPCDTO;
    }

    /**
     * 计算出铁量 - 净重
     * @param tapTPCDTO
     * @return
     */
    protected BigDecimal getSumNetWgt(TapTPCDTO tapTPCDTO) {
        return getSumNetWgt(tapTPCDTO, null);
    }

    /**
     * 计算出铁量 - 净重
     * @param tapTPCDTO
     * @param shift     shift (1：夜班，2：白班)
     * @return
     */
    protected BigDecimal getSumNetWgt(TapTPCDTO tapTPCDTO, String shift) {
        BigDecimal sum = new BigDecimal(0);
        sum = tapTPCDTO.getData().stream()
                .filter(p -> (StringUtils.isBlank(shift) || shift.equals(p.getWorkShift())))
                .map(TapTPC::getNetWt)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum;
    }

    /**
     * 计算出铁量 - 毛重
     * @param tapTPCDTO
     * @return
     */
    protected BigDecimal getSumGrossWgt(TapTPCDTO tapTPCDTO) {
        return getSumGrossWgt(tapTPCDTO, null);
    }

    /**
     * 计算出铁量 - 毛重
     * @param tapTPCDTO
     * @param shift shift (1：夜班，2：白班)
     * @return
     */
    protected BigDecimal getSumGrossWgt(TapTPCDTO tapTPCDTO, String shift) {
        BigDecimal sum = new BigDecimal(0);
        sum = tapTPCDTO.getData().stream()
                .filter(p -> (StringUtils.isBlank(shift) || shift.equals(p.getWorkShift())))
                .map(TapTPC::getGrossWt)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum;
    }

    protected String getRoundact(List<BatchDistribution> distributions) {
        //排序, 按position倒序
        distributions.sort(Comparator.comparing(BatchDistribution::getPosition).reversed());
        //过滤weightact不为0的值
        List<BatchDistribution> collect = distributions.stream()
                .filter(p -> (p.getWeightset() == null || p.getWeightset().doubleValue() > 0))
                .collect(Collectors.toList());
        // 拼接roundset
        return collect.stream()
                .map(p -> String.valueOf(p.getRoundact()))
                .collect(Collectors.joining(""));
    }

    protected String getRoundset(List<BatchDistribution> distributions) {
        //排序, 按position倒序
        distributions.sort(Comparator.comparing(BatchDistribution::getPosition).reversed());
        //过滤weightact不为0的值
        List<BatchDistribution> collect = distributions.stream()
                .filter(p -> (p.getWeightset() == null || p.getWeightset().doubleValue() > 0))
                .collect(Collectors.toList());
        // 拼接roundset
        return collect.stream()
                .map(p -> String.valueOf(p.getRoundset()))
                .collect(Collectors.joining(""));
    }

    protected String getPosition(List<BatchDistribution> distributions) {
        //排序, 按position倒序
        distributions.sort(Comparator.comparing(BatchDistribution::getPosition).reversed());
        //过滤weightact不为0的值
        List<BatchDistribution> collect = distributions.stream()
                .filter(p -> (p.getWeightset() == null || p.getWeightset().doubleValue() > 0))
                .collect(Collectors.toList());
        // 拼接position
        return collect.stream()
                .map(p -> String.valueOf(p.getPosition()))
                .collect(Collectors.joining(""));
    }

    /**
     * 获取material wetwgt 元数据
     * @param materialExpendDTO material数据
     * @return
     */
    protected BigDecimal getMaterialExpendWetWgt(MaterialExpendDTO materialExpendDTO) {
        return getMaterialExpendWetWgt(materialExpendDTO, null, null);
    }

    /**
     * 获取material wetwgt 元数据
     * @param materialExpendDTO material数据
     * @param matCnames matCname列表，多个的值相加
     * @return
     */
    protected BigDecimal getMaterialExpendWetWgt(MaterialExpendDTO materialExpendDTO, List<String> matCnames) {
        return getMaterialExpendWetWgt(materialExpendDTO, matCnames, null);
    }

    /**
     * 获取material wetwgt 元数据
     * @param materialExpendDTO material数据
     * @param matCnames matCname列表，多个的值相加
     * @param shift shift shift (1：夜班，2：白班)
     * @return
     */
    protected BigDecimal getMaterialExpendWetWgt(MaterialExpendDTO materialExpendDTO, List<String> matCnames, String shift) {
        if (Objects.isNull(materialExpendDTO) || CollectionUtils.isEmpty(materialExpendDTO.getData())) {
            return new BigDecimal(0.0);
        }
        BigDecimal sum = materialExpendDTO.getData().stream()
                .filter(p -> (Objects.isNull(matCnames) || matCnames.contains(p.getMatCname())) && (StringUtils.isBlank(shift) || shift.equals(p.getWorkShift())))
                .map(MaterialExpend::getWetWgt)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum;
    }

    /**
     * 获取latest tagValues
     * @param date
     * @param version
     * @param tagNames
     * @return
     */
    protected TagValueListDTO getLatestTagValueListDTO(Date date, String version, List<String> tagNames) {
        Map<String, String> queryParam = new HashMap();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", tagNames);
        String chargeNoData = httpUtil.postJsonParams(getLatestTagValuesUrl(version) + date.getTime(), jsonObject.toJSONString());

        TagValueListDTO tagValueListDTO = null;
        if (StringUtils.isNotBlank(chargeNoData)) {
            tagValueListDTO = JSON.parseObject(chargeNoData, TagValueListDTO.class);
            if (Objects.isNull(tagValueListDTO) || CollectionUtils.isEmpty(tagValueListDTO.getData())) {
                log.warn("根据tagName[{}]获取[{}]的latest TagValueListDTO数据为空", tagNames, date);
            } else {
                // 排序，默认按chargeNo从小到大排序，即时间从老到新
                tagValueListDTO.getData().sort(Comparator.comparing(TagValue::getVal));
            }
        }
        return tagValueListDTO;
    }

    /**
     * 获取布料矩阵数据
     * @param query
     * @param version
     * @return
     */
    protected Map<String, List<BatchDistribution>> getMatrixDistrAvgInRangeMap(DateQuery query, String version) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("startTime",  Objects.requireNonNull(query.getStartTime().getTime()).toString());
        queryParam.put("endTime",  Objects.requireNonNull(query.getEndTime().getTime()).toString());
        String chargeNoData = httpUtil.get(getMatrixDistrAvgInRangeUrl(version), queryParam);

        Map<String, List<BatchDistribution>> matrixDistrAvgInRangeMap = null;
        if (StringUtils.isNotBlank(chargeNoData)) {
            SuccessEntity<Map<String, List<BatchDistribution>>> successEntity = JSON.parseObject(chargeNoData, new TypeReference<SuccessEntity<Map<String, List<BatchDistribution>>>>() {});
            if (Objects.isNull(successEntity) || MapUtils.isEmpty(successEntity.getData())) {
                log.warn("根据时间[{}]获取的matrixDistrAvgInRangeMap数据为空", query.getStartTime());
            } else {
                matrixDistrAvgInRangeMap = successEntity.getData();
            }
        }
        return matrixDistrAvgInRangeMap;
    }

    /**
     * 获取materials数据组指定的值
     * @param materialsArray
     * @param compareKey
     * @param compareValue
     * @param actualKey
     * @return
     */
    protected String getMaterialValue(JSONArray materialsArray, String compareKey, String compareValue, String actualKey) {
        if (CollectionUtils.isNotEmpty(materialsArray)) {
            for (int k = 0; k < materialsArray.size(); k++) {
                JSONObject material = materialsArray.getJSONObject(k);
                if (StringUtils.endsWith(material.getString(compareKey), compareValue)){
                    return material.getString(actualKey);
                }
            }
        }
        return null;
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

    /**
     * 获取/bfBlast/main/info的url
     * @param version
     * @return
     */
    protected String getBlastIntakeAreaUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/bfBlast/blastIntakeArea";
    }

    /**
     * 获取/bfBlast/main/info的url
     * @param version
     * @return
     */
    protected String getBfBlastMainInfoUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/bfBlast/main/info";
    }

    /**
     * 获取/charge/variation/range的url
     * @param version
     * @return
     */
    protected String getChargeVarInfoUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/charge/variation/day";
    }

    /**
     * 获取/tagValues/latest的url
     * @param version
     * @return
     */
    protected String getLatestTagValuesUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValues/latest/";
    }

    /**
     * 获取/batch/distribution/getMatrixDistrAvgInRange的url
     * @param version
     * @return
     */
    protected String getMatrixDistrAvgInRangeUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/batch/distribution/getMatrixDistrAvgInRange";
    }

    /**
     * 获取/tagValue/latest的url
     * @param version
     * @return
     */
    protected String getLatestTagValueUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValue/latest";
    }
    /**
     * 获取高炉的种类Url
     * @param version
     * @return Url的string
     */
    protected String getBrandCodes(String version){
        return httpProperties.getGlUrlVersion(version) + "/brandCodes/getBrandCodes";
    }

    /**
     * 通过tag点拿数据的API，根据sequence和version返回不同工序的api地址
     * @param version
     * @return
     */
    protected String getUrlTagNamesInRange(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    /**
     * 热强度和矿块信息
     * @param version
     * @return
     */
    protected String getAnalysisValueUrl(String version, String time, String type, String brandCode) {
        String url = String.format(httpProperties.getGlUrlVersion(version) + "/analysisValue/clock/%s?type=%s&brandcode=%s", time, type, brandCode);
        return url;
    }

    protected String getRangeByTypeUrl (String version, String from, String to, String type) {
        String url = String.format(httpProperties.getGlUrlVersion(version) + "/analysisValues/rangeByType?from=%s&to=%s&materialType=%s", from, to, type);
        return url;
    }

    /**
     * 烧结矿理化分析API
     * @param version
     * @return
     */
    protected String getAnalysisValuesUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/analysisValues/rangeByCode";
    }

    /**
     * 出铁数据接口
     * @param version
     * @return
     */
    protected String getTapsInRange(String version) {
        return httpProperties.getGlUrlVersion(version) + "/taps/sg/period";
    }

    /**
     * 罐号重量数据接口
     * @param version
     * @return
     */
    protected String getTpcInfoByTapNo(String version) {
        return httpProperties.getGlUrlVersion(version) + "/report/tap/queryTpcInfoByTapNo";
    }

    /**
     * 风口坏否信息
     * @param version
     * @return
     */
    protected String getQueryBlastStatus(String version) {
        return httpProperties.getGlUrlVersion(version) + "/bfBlast/queryBlastStatus";
    }

    /**
     * 获取数据：入炉铁份、批铁量、焦比
     * @param version
     * @param queryParam
     * @return
     */
    protected BigDecimal getLatestByCategoryAndItem (String version, Map<String, String> queryParam) {
        String url = httpProperties.getGlUrlVersion(version) + "/getLatestByCategoryAndItem";
        String data = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSONObject.parseObject(data);
        BigDecimal result = null;
        if (Objects.nonNull(jsonObject)) {
            result = jsonObject.getBigDecimal("data");
        }
        return result;
    }
}
