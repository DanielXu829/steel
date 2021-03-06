package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.SuccessEntity;
import com.cisdi.steel.dto.response.gl.*;
import com.cisdi.steel.dto.response.gl.res.*;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.gl.GLDataUtil;
import com.cisdi.steel.module.job.util.FastJSONUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.google.common.collect.Lists;
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
        String chargeNoData = httpUtil.get(getTagValueUrl(version), queryParam);

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
     * 8高炉月报汇总的原燃料消耗接口
     * @param version
     * @param date
     * @return
     */
    protected MaterialExpendStcDTO getMaterialExpandStcDTO(String version, Date date) {
        String materialExpendStcUrl = String.format(httpProperties.getGlUrlVersion(version) + "/report/material/materialExpend/stc?dateTime=%s", date.getTime());
        String materialExpendDTOStr = httpUtil.get(materialExpendStcUrl);

        return Optional.ofNullable(materialExpendDTOStr).map(e -> JSON.parseObject(e, MaterialExpendStcDTO.class)).orElse(null);
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
     * 获取每天的风口完整信息，可用于月报展示
     * @param version
     * @return api数据
     */
    protected BfBlastResult getBfBlastResult(String version, Date date) {
        BfBlastResult bfBlastResultDTO = null;
        Map<String, String> queryParam = new HashMap();
        Date dateBeginTime = DateUtil.getDateEndTime22(date);
        queryParam.put("time",  String.valueOf(dateBeginTime.getTime()));

        String bfBlastResultUrl = this.getBfBlastResultUrl(version);
        String bfBlastResultDTOStr = httpUtil.get(bfBlastResultUrl, queryParam);
        if (StringUtils.isNotBlank(bfBlastResultDTOStr)) {
            SuccessEntity<BfBlastResult> successEntity = JSON.parseObject(bfBlastResultDTOStr, new TypeReference<SuccessEntity<BfBlastResult>>() {});
            if (Objects.isNull(successEntity) || Objects.isNull(successEntity.getData())) {
                log.warn("根据时间[{}]获取的bfBlastResultDTO数据为空", dateBeginTime);
            } else {
                bfBlastResultDTO = successEntity.getData();
            }
        }
        return bfBlastResultDTO;
    }

    /**
     * 按天或者按月获取精益信息
     * @param version
     * @return api数据
     */
    protected TapJyDTO getTapJyDTO(String version, long startTime, long endTimed, String dataType, String workShift) {
        TapJyDTO tapJyDTO = null;
        Map<String, String> queryParam = new HashMap();
        queryParam.put("startTime",  String.valueOf(startTime));
        queryParam.put("endTime",  String.valueOf(endTimed));
        queryParam.put("dataType",  dataType);
        queryParam.put("workShift",  workShift);

        String tapJyDTOUrl = this.getTapJyDTOUrl(version);
        String tapJyDTOStr = httpUtil.get(tapJyDTOUrl, queryParam);
        if (StringUtils.isNotBlank(tapJyDTOStr)) {
            SuccessEntity<TapJyDTO> successEntity = JSON.parseObject(tapJyDTOStr, new TypeReference<SuccessEntity<TapJyDTO>>() {});
            if (Objects.isNull(successEntity) || Objects.isNull(successEntity.getData())) {
                log.warn("根据时间[{}]获取的tapJyDTO数据为空", startTime);
            } else {
                tapJyDTO = successEntity.getData();
            }
        }
        return tapJyDTO;
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
                //tagValueListDTO.getData().sort(Comparator.comparing(TagValue::getVal));
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

    protected TapSummaryListDTO getTapSummaryListDTO(String version, Date date) {
        return getTapSummaryListDTO(version, date, "day");
    }

    protected TapSummaryListDTO getTapSummaryListDTO(String version, Date date, String workShift) {
        TapSummaryListDTO tapSummaryListDTO = null;
        Map<String, String> queryParam = new HashMap();
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        queryParam.put("dateTime",  String.valueOf(dateBeginTime.getTime()));
        queryParam.put("workShift",  workShift);

        String tapSummaryListUrl = httpProperties.getGlUrlVersion(version) + "/report/tap/getTapSummary";
        String tapSummaryListStr = httpUtil.get(tapSummaryListUrl, queryParam);
        if (StringUtils.isNotBlank(tapSummaryListStr)) {
            tapSummaryListDTO = JSON.parseObject(tapSummaryListStr, TapSummaryListDTO.class);
            if (Objects.isNull(tapSummaryListDTO) || CollectionUtils.isEmpty(tapSummaryListDTO.getData())) {
                log.warn("[{}] 的TapSummaryListDTO数据为空", dateBeginTime);
            }
        }
        return tapSummaryListDTO;
    }

    protected TapSummary getTapSummary(String version, DateQuery dateQuery) {
        TapSummary summary = null;
        Map<String, String> queryParam = new HashMap();
        queryParam.put("startTime",  Objects.requireNonNull(dateQuery.getStartTime().getTime()).toString());
        queryParam.put("endTime",  Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString());
        String result = httpUtil.get(getTapSummaryByRangeUrl(version), queryParam);
        if(StringUtils.isNotBlank(result)) {
            TapSummaryListDTO summaryDTO = JSON.parseObject(result, TapSummaryListDTO.class);
            if(Objects.nonNull(summaryDTO) && Objects.nonNull(summaryDTO.getData()) && CollectionUtils.isNotEmpty(summaryDTO.getData())) {
                summary = summaryDTO.getData().get(0);
            }
        }
        return summary;
    }

    protected AnalysisValueDTO getAnalysisValueDTO(String version, DateQuery dateQuery, String brandCode) {
        String url = getAnalysisValuesUrl(version);
        Map<String, String> queryParam = new HashMap();
        queryParam.put("from", Objects.requireNonNull(dateQuery.getQueryStartTime()).toString());
        queryParam.put("to", Objects.requireNonNull(dateQuery.getQueryEndTime()).toString());
        queryParam.put("brandCode", brandCode);
        String result = httpUtil.get(url, queryParam);
        // 根据json映射对象DTO
        AnalysisValueDTO analysisValueDTO = null;
        if (StringUtils.isNotBlank(result)) {
            analysisValueDTO = JSON.parseObject(result, AnalysisValueDTO.class);
        }
        return analysisValueDTO;
    }

    /**
     * 获取无聊brandCode和描述map
     * @param version
     * @return
     */
    protected Map<String, String> getBrandCodeToDescrMap(String version) {
        String materialMapJsonData = httpUtil.get(getMaterialMapUrl(version));
        MaterialMapDTO materialMapDTO = JSON.parseObject(materialMapJsonData, MaterialMapDTO.class);
        Map<String, Material> stringMaterialMap = Optional.ofNullable(materialMapDTO).map(MaterialMapDTO::getData).orElse(new HashMap<>());
        Map<String, String> brandCodeToDescrMap = stringMaterialMap.values().stream()
                .collect(Collectors.toMap(Material::getBrandcode, Material::getDescr));
        return brandCodeToDescrMap;
    }

    //获取Commit url
    protected String getCommitInfoUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/comment/info";
    }

    /**
     *获取commit info中的remark
     * @param version
     * @param date
     * @param id
     * @return
     */
    protected String getReportCommitInfoById(String version, long date, int id) {
        String commit = "";
        String url = getCommitInfoUrl(version);
        Map<String, String> queryParam = new HashMap();
        queryParam.put("date", Objects.requireNonNull(date).toString());
        queryParam.put("model", "REPORT");
        queryParam.put("id", String.valueOf(id));
        String result = httpUtil.get(url, queryParam);
        if(StringUtils.isNotBlank(result)) {
            CommentDataDTO commentDataDTO = JSON.parseObject(result, CommentDataDTO.class);
            if(Objects.nonNull(commentDataDTO)) {
                CommentData data = commentDataDTO.getData();
                if(Objects.nonNull(data)) {
                    commit = data.getRemark();
                }
            }
        }
        return commit;
    }

    /**
     *获取commit info中的remark
     * @param version
     * @param date
     * @return
     */
    protected List<CommentData> getReportCommitInfo(String version, long date) {
        String url = getCommitInfoUrl(version);
        Map<String, String> queryParam = new HashMap();
        queryParam.put("date", Objects.requireNonNull(date).toString());
        queryParam.put("model", "REPORT");
        String result = httpUtil.get(url, queryParam);
        List<CommentData> commentDataList = new ArrayList<>();
        if (StringUtils.isNotBlank(result)) {
            CommentDataListDTO commentDataListDTO = JSON.parseObject(result, CommentDataListDTO.class);
            if (Objects.nonNull(commentDataListDTO)) {
                commentDataList = commentDataListDTO.getData();
            }
        }
        return commentDataList;
    }

    /**
     *获取commit info中的remark
     * @param version
     * @param date
     * @param shift
     * @param id
     * @return
     */
    protected String getShiftLogCommitInfo(String version, long date, int shift, int id) {
        String commit = "";
        String url = getCommitInfoUrl(version);
        Map<String, String> queryParam = new HashMap();
        queryParam.put("date", Objects.requireNonNull(date).toString());
        queryParam.put("model", "SHIFT_LOG");
        queryParam.put("shift", String.valueOf(shift));
        queryParam.put("id", String.valueOf(id));
        String result = httpUtil.get(url, queryParam);
        if(StringUtils.isNotBlank(result)) {
            CommentDataDTO commentDataDTO = JSON.parseObject(result, CommentDataDTO.class);
            if(Objects.nonNull(commentDataDTO)) {
                CommentData data = commentDataDTO.getData();
                if(Objects.nonNull(data)) {
                    commit = data.getRemark();
                }
            }
        }
        return commit;
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
    protected String getTagValueUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValues";
    }

    /**
     * 获取/charge/chargeNo/range的url
     * @param version
     * @return
     */
    protected String getChargeNoUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/charge/chargeNo/range";
    }

    /**
     * 获取chargeNo
     * @param query
     * @param version
     * @return
     */
    protected List<Integer> getChargeNo(DateQuery query, String version) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("startTime",  Objects.requireNonNull(query.getStartTime().getTime()).toString());
        queryParam.put("endTime",  Objects.requireNonNull(query.getEndTime().getTime()).toString());
        String chargeNoData = httpUtil.get(getChargeNoUrl(version), queryParam);

        return Optional.ofNullable(chargeNoData).map(JSONObject::parseObject).map(e -> e.getJSONArray("data"))
                .map(e -> JSONObject.parseArray(e.toJSONString(), Integer.class)).orElse(new ArrayList<Integer>());
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
     * 获取单个tag value
     * @param version
     * @param tagName
     * @param date
     * @return
     */
    protected BigDecimal getLatestTagValue(String version, String tagName, Date date) {
        long time = date.getTime();
        Map<String, String> param = new HashMap<>();
        param.put("time", String.valueOf(time));
        param.put("tagname", tagName);
        String url = getLatestTagValueUrl(version);
        String result = httpUtil.get(url, param);
        BigDecimal tagValue = FastJSONUtil.getJsonValueByKey(result, Lists.newArrayList("data"), "val", BigDecimal.class);
        return tagValue;
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

    protected String getBrandCodeData(String version, String startTime, String endTime, String type) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("startTime", startTime);
        queryMap.put("endTime", endTime);
        queryMap.put("type", type);
        return httpUtil.get(getBrandCodes(version), queryMap);
    }

    /**
     * 通过tag点拿数据的API，根据sequence和version返回不同工序的api地址
     * @param version
     * @return
     */
    protected String getUrlTagNamesInRange(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    protected Map<String, LinkedHashMap<Long, Double>> getTagNamesInRangeTagValueMapDTO(String version, DateQuery dateQuery, List<String> tagNames) {
        Map<String, String> queryParam = new HashMap();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tagnames", tagNames);
        jsonObject.put("starttime", dateQuery.getQueryStartTime());
        jsonObject.put("endtime", dateQuery.getQueryEndTime());
        String jsonData = httpUtil.postJsonParams(getUrlTagNamesInRange(version), jsonObject.toJSONString());

        Map<String, LinkedHashMap<Long, Double>> tagFormulaToValueMap = null;
        if (StringUtils.isNotBlank(jsonData)) {
            TagValueMapDTO tagValueMapDTO = JSON.parseObject(jsonData, TagValueMapDTO.class);
            tagFormulaToValueMap = Optional.ofNullable
                    (JSON.parseObject(jsonData, TagValueMapDTO.class)).map(TagValueMapDTO::getData).orElse(null);
        }
        return tagFormulaToValueMap;
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

    /**
     * 查询BrandCode,可能有多个，最多三个
     * @param version
     * @param type
     * @return 包含BrandCode的JSONArray
     */
    protected List<String> getBrandCodeData(String version, DateQuery dateQuery, String type) {
        List<String> brandCodeData = new ArrayList<>();
        Map<String, String> queryParam = new HashMap();
        queryParam.put("startTime", Objects.requireNonNull(dateQuery.getStartTime().getTime()).toString());
        queryParam.put("endTime", Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString());
        queryParam.put("type", type);
        String url = getBrandCodes(version);
        String result = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if(null != jsonObject){
            JSONArray data = jsonObject.getJSONArray("data");
            if(null!= data){
                for (Object o : data) {
                    brandCodeData.add(String.valueOf(o));
                }
            }
        }
        return brandCodeData;
    }

    /**
     * 烧结矿理化分析API
     * @param version
     * @return
     */
    protected String getAnalysisValuesUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/analysisValues/rangeByCode";
    }

    protected List<AnalysisValue> getAnalysisValuesByBrandCode(String version, String from, String to, String brandCode) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("from", from);
        queryParam.put("to", to);
        queryParam.put("brandCode", brandCode);
        String result = httpUtil.get(getAnalysisValuesUrl(version), queryParam);
        List<AnalysisValue> analysisValueList = Optional.ofNullable(result)
                .map(e -> JSON.parseObject(e, AnalysisValueDTO.class))
                .map(AnalysisValueDTO::getData).orElse(null);

        return analysisValueList;
    }

    protected String getMaterialMapUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/material/map";
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
     * 风口完整信息
     * @param version
     * @return
     */
    protected String getBfBlastResultUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/bfBlastResult/latest";
    }

    /**
     * 精益接口信息url
     * @param version
     * @return
     */
    protected String getTapJyDTOUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/report/query/jygl";
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

    /**
     * 出铁信息
     * @param version
     * @return
     */
    protected String getTapSummaryByRangeUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/report/tap/getTapSummaryByRange";
    }

    protected String getBXMaterialUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/report/getBXMaterial/mt";
    }

}
