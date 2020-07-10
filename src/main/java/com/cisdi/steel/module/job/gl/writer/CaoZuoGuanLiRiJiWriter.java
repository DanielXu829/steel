package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.AnalysisValueDTO;
import com.cisdi.steel.dto.response.gl.TagValueListDTO;
import com.cisdi.steel.dto.response.gl.TagValueMapDTO;
import com.cisdi.steel.dto.response.gl.TapTPCDTO;
import com.cisdi.steel.dto.response.gl.req.TagQueryParam;
import com.cisdi.steel.dto.response.gl.res.*;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.FastJSONUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * <p>Description: 8高炉操作管理日记 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/12/28 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class CaoZuoGuanLiRiJiWriter extends BaseGaoLuWriter {

    @Autowired
    protected TargetManagementMapper targetManagementMapper;

    /**
     * @param excelDTO 数据
     * @return
     */
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = "8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }

        try {
            // 正面-小时参数
            handleFacadeXiaoShiCanShu(excelDTO, workbook, version);

            // 正面-风口信息
            handleFengKouXinXi(excelDTO, workbook, version);

            // 正面-变料信息
            handleBianLiaoXinXi(excelDTO, workbook, version);

            //反面第一行数据
            handleFirstRowData(excelDTO, workbook, version);

            // 出铁(反面)
            handleTapData(excelDTO, workbook, version);

            //反面-块矿
            handOreBlockData(excelDTO, workbook, version);

            //反面-焦炭/煤粉
            handAnalysisValue(excelDTO, workbook, version);

            // 反面-烧结矿理化分析
            handAnalysisValues(excelDTO, workbook, version);

            //反面-操作简析/大事记
            handleCaoZuoJianxi(excelDTO, workbook, version);
        } catch (Exception e) {
            log.error("处理模板数据失败", e);
        } finally {
            //Date currentDate = new Date();
            Date currentDate = DateUtil.addDays(new Date(), -1);
            for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet=workbook.getSheetAt(i);
                // 清除标记项(例如:{块矿.矿种})
                if (!Objects.isNull(sheet) && !workbook.isSheetHidden(i)) {
                    // 全局替换 当前日期
                    ExcelWriterUtil.replaceCurrentDateInTitle(sheet, "%当前日期%", currentDate);
                    PoiCustomUtil.clearPlaceHolder(sheet);
                }
            }
        }

        return workbook;
    }

    //开始--------------------正面-小时参数--------------------
    private void handleFacadeXiaoShiCanShu(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        try {
            String queryUrl = getUrlTagNamesInRange(version);

            // 动态报表生成的模板默认取第二个sheet。
            String sheetName = "_FacadeXiaoShiCanShu_day_hour";
            Sheet sheet = workbook.getSheet(sheetName);
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                DateQuery query = this.getDateQueryBeforeOneDay(excelDTO);
                DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(query.getRecordDate());
                List<DateQuery> dateQueries = DateQueryUtil.buildDayHourOneEach(dateQuery.getStartTime(), dateQuery.getEndTime());
                // 直接拿到tag点名, 无需根据别名再去获取tag点名
                List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(sheet);
                for (int rowNum = 0; rowNum < dateQueries.size(); rowNum++) {
                    List<CellData> cellDataList = handleEachRowData(tagNames, queryUrl, dateQueries.get(rowNum), rowNum*3 + 1);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        } catch (Exception e) {
            log.error("处理正面-小时参数出错", e);
        }
    }

    /**
     * 处理每行数据
     *
     * @param tagFormulas
     * @param queryUrl
     * @param queryParam
     * @param rowIndex
     * @return
     */
    private List<CellData> handleEachRowData(List<String> tagNames, String queryUrl, DateQuery dateQuery, int rowIndex) {
        List<TargetManagement> targetManagements = targetManagementMapper.selectTargetManagementsByTargetNames(tagNames);
        Map<String, TargetManagement> targetManagementMap = targetManagements.stream().collect(Collectors.toMap(TargetManagement::getTargetName, target -> target));
        List<String> tagFormulas = targetManagements.stream().map(TargetManagement::getTargetFormula).collect(Collectors.toList());
        // 批量查询tag value
        TagQueryParam tagQueryParam = new TagQueryParam(dateQuery.getQueryStartTime(), dateQuery.getQueryEndTime(), tagFormulas);
        String result = httpUtil.postJsonParams(queryUrl, JSON.toJSONString(tagQueryParam));

        List<CellData> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(result)) {
            TagValueMapDTO tagValueMapDTO = JSON.parseObject(result, TagValueMapDTO.class);
            Map<String, Map<Long, Double>> tagValueMaps = tagValueMapDTO.getData();
            // 获取targetManagement map

            for (int columnIndex = 0; columnIndex < tagNames.size(); columnIndex++) {
                String targetName = tagNames.get(columnIndex);
                if (StringUtils.isNotBlank(targetName)) {
                    TargetManagement targetManagement = targetManagementMap.get(targetName);
                    if (Objects.nonNull(targetManagement)) {
                        Map<Long, Double> tagValueMap = tagValueMaps.get(targetManagement.getTargetFormula());
                        if (Objects.nonNull(tagValueMap)) {
                            TargetManagement tag = targetManagementMap.get(targetName);
                            List<DateQuery> dayEach = DateQueryUtil.buildDayHourOneEach(new Date(dateQuery.getQueryStartTime()), new Date(dateQuery.getQueryEndTime()));
                            handleEachCellData(dayEach, tagValueMap, resultList, rowIndex, columnIndex, tag);
                        }
                    }
                }
            }
        }

        return resultList;
    }

    /**
     * 处理每个单元格数据
     *
     * @param dayEach
     * @param tagValueMap
     * @param resultList
     * @param rowIndex
     * @param columnIndex
     * @param tag
     */
    private void handleEachCellData(List<DateQuery> dayEach, Map<Long, Double> tagValueMap, List<CellData> resultList, int rowIndex, int columnIndex, TargetManagement tag) {
        // 按照时间顺序从老到新排序
        List<Long> clockList = tagValueMap.keySet().stream().sorted().collect(Collectors.toList());
        for (int i = 0; i < dayEach.size(); i++) {
            DateQuery query = dayEach.get(i);
            Date queryStartTime = query.getStartTime();
            Date queryEndTime = query.getEndTime();
            if (tag.getTargetFormula().endsWith("_evt")) {
                //如果是evt结尾的, 取时间最大的值
                Long key = Collections.max(clockList);
                Double maxVal = tagValueMap.get(key);
                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, maxVal);
            } else {
                // 其他情况，取时间范围内第一个值。
                for (int j = 0; j < clockList.size(); j++) {
                    Long tempTime = clockList.get(j);
                    Date date = new Date(tempTime);
                    if ((date.getTime() >= queryStartTime.getTime()) && (date.getTime() <= queryEndTime.getTime())) {
                        Double val = tagValueMap.get(tempTime);
                        ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, val);
                        break;
                    }
                }
            }
        }
    }
    //结束--------------------正面-小时参数--------------------

    /**
     * 风口坏否
     * @param url
     * @param dateQuery
     * @param cellDataList
     * @param dateTime
     * @param rowIndex
     * @param columnIndex
     */
    private void handleFengKouHuaiFou (String url, DateQuery dateQuery, List<CellData> cellDataList, Long dateTime, int rowIndex, int columnIndex) {
        Map<String, String> queryParam = new HashMap();
        queryParam.put("time", Objects.requireNonNull(dateTime).toString());
        String jsonData = httpUtil.get(url, queryParam);
        for (int i = 0; i < 36; i++) {
            Integer val = FastJSONUtil.getJsonValueByKey(jsonData, Lists.newArrayList("data"), String.valueOf(i+1), Integer.class);
            if (Objects.nonNull(val) && val != 0) {
                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i*2 + columnIndex, "×");
                ExcelWriterUtil.addCellData(cellDataList, rowIndex + 2, i*2 + columnIndex, "×");
            } else {
                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i*2 + columnIndex, "√");
                ExcelWriterUtil.addCellData(cellDataList, rowIndex + 2, i*2 + columnIndex, "√");
            }
        }
    }

    //开始--------------------正面-风口信息--------------------
    private void handleFengKouXinXi(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        try {
            Sheet zhengMianSheet = workbook.getSheetAt(0);
            // 获取标志位的坐标
            Cell fengKouZhiJing = PoiCustomUtil.getCellByValue(zhengMianSheet, "{风口直径}");
            int rowIndex = fengKouZhiJing.getRowIndex();
            int columnIndex = fengKouZhiJing.getColumnIndex();
            List<CellData> cellDataList = new ArrayList<CellData>();
            int fengkouNumMaxIndex = columnIndex + 36;
            // 填充风口信息
            BfBlastMainInfo bfBlastMainInfo = getBfBlastMainInfo(version);
            for (int i = 0; i < 36; i++) {
                BfBlastMain bfBlastMain = bfBlastMainInfo.getBfBlastMains().get(i);
                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i*2+columnIndex, bfBlastMain.getBlastDiameter());
            }
            String url = getQueryBlastStatus(version);
            DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(date.getRecordDate());
            Date dateRun = date.getRecordDate();
            List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourAheadTwoHour(dateRun);
            for (int k = 0; k < dateQueries.size(); k++) {
                DateQuery dateQuery1 = dateQueries.get(k);
                if (dateQuery1.getStartTime().compareTo(dateQuery.getStartTime()) == 0) {
                    //夜班/接班
                    handleFengKouHuaiFou(url, dateQuery, cellDataList, dateQuery1.getQueryEndTime(), rowIndex + 1, columnIndex);
                } else {
                    //白班/交班
                    handleFengKouHuaiFou(url, dateQuery, cellDataList, dateQuery1.getQueryEndTime(), rowIndex + 2, columnIndex);
                }
            }
            ExcelWriterUtil.setCellValue(zhengMianSheet, cellDataList);

            // 填充其他风口信息
            String queryUrl = getUrlTagNamesInRange(version);
            // 动态报表生成的模板默认取第二个sheet。
            String sheetName = "_FacadeFengKouXinXi_day_hour";
            Sheet sheet = workbook.getSheet(sheetName);
            // 直接拿到tag点名, 无需根据别名再去获取tag点名
            List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(sheet);
            int rowNum = 1;

            List<CellData> cellData = handleEachRowData(tagNames, queryUrl, dateQuery, rowNum);
            BigDecimal value = this.getBlastIntakeArea(version, String.valueOf(dateQuery.getQueryEndTime()));
            ExcelWriterUtil.addCellData(cellData, 1, 0, value);
            ExcelWriterUtil.setCellValue(sheet, cellData);
        } catch (Exception e) {
            log.error("处理正面-风口信息出错", e);
        }
    }
    //结束--------------------正面-风口信息--------------------

    //开始--------------------正面-变料信息--------------------
    private void handleBianLiaoXinXi(WriterExcelDTO excelDTO, Workbook workbook, String version){
        try {
            Sheet sheet = workbook.getSheetAt(0);
            // 获取标志位的坐标
            Cell piShuCell = PoiCustomUtil.getCellByValue(sheet, "{变料.批数}");
            int beginRowIndex = piShuCell.getRowIndex();
            int columnIndex = piShuCell.getColumnIndex();

            //动态的炉料变更种类
            Cell typeCell = PoiCustomUtil.getCellByValue(sheet, "{变料.种类}");
            int typeRowIndex = typeCell.getRowIndex();
            int typeColumnIndex = typeCell.getColumnIndex();

            // 获取数据并填充
            DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(DateUtil.addDays(new Date(), -1));
            List<ChargeVarInfo> chargeVarInfos = getChargeVarInfo(version, dateQuery, "M");
            handleDangWeiJiaoDu (sheet, getChargeVarInfo(version, dateQuery, "D"));
            List<CellData> cellDataList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(chargeVarInfos)) {
                //动态类型的添加顺序
                List<String> luLiaoList = new ArrayList<String>(){{
                    add("开始批次");
                    add("矿批t");
                    add("焦批t");
                    add("焦炭负荷");
                    add("烧结%");
                    add("球团%");
                    add("块矿%");
                    add("批铁t");
                    add("焦比kg/t");
                }};
                int initSize = luLiaoList.size();
                // 初始化动态生成的炉料变更种类
                ChargeVarInfo info = chargeVarInfos.get(0);
                for(ChargeVarInfo chargeVarInfo:chargeVarInfos) {
                    if (Objects.nonNull(info) && Objects.nonNull(chargeVarInfo) &&
                            Objects.nonNull(chargeVarInfo.getChargeVarMaterial()) &&
                            Objects.nonNull(info.getChargeVarMaterial()) && chargeVarInfo.getChargeVarMaterial().size() > info.getChargeVarMaterial().size()) {
                        info = chargeVarInfo;
                    }
                }
                if (Objects.nonNull(info)) {
                    List<ChargeVarMaterial> chargeVarMaterials = info.getChargeVarMaterial();
                    if (Objects.nonNull(chargeVarMaterials) && CollectionUtils.isNotEmpty(chargeVarMaterials)) {
                        for (ChargeVarMaterial material:chargeVarMaterials) {
                            if (!luLiaoList.contains(material.getBrandName())) {
                                ExcelWriterUtil.addCellData(cellDataList, typeRowIndex +
                                        luLiaoList.size() - initSize, typeColumnIndex, material.getBrandName()+"t");
                                luLiaoList.add(material.getBrandName());
                            }
                        }
                    }
                }
                for (int i = 0; i < luLiaoList.size(); i++) {
                    String brandName = luLiaoList.get(i);
                    for(int j = 0; j < chargeVarInfos.size(); j++) {
                        ChargeVarInfo chargeVarInfo = chargeVarInfos.get(j);
                        List<ChargeVarMaterial> chargeVarMaterials = chargeVarInfo.getChargeVarMaterial();
                        switch (brandName) {
                            case "开始批次": {
                                Integer chargeNo = chargeVarInfo.getChargeVarIndex().getChargeNo();
                                ExcelWriterUtil.addCellData(cellDataList, i + beginRowIndex, columnIndex + j*2, chargeNo);
                                break;
                            }
                            case "矿批t": {
                                ExcelWriterUtil.addCellData(cellDataList, i + beginRowIndex, columnIndex + j*2, chargeVarInfo.getOreMass());
                                break;
                            }
                            case "焦批t": {
                                ExcelWriterUtil.addCellData(cellDataList, i + beginRowIndex, columnIndex + j*2, chargeVarInfo.getCokeMass());
                                break;
                            }
                            case "焦炭负荷": {
                                ExcelWriterUtil.addCellData(cellDataList, i + beginRowIndex, columnIndex + j*2, chargeVarInfo.getCokeLoad());
                                break;
                            }
                            case "烧结%": {
                                ExcelWriterUtil.addCellData(cellDataList, i + beginRowIndex, columnIndex + j*2, chargeVarInfo.getSinterRate());
                                break;
                            }
                            case "球团%": {
                                ExcelWriterUtil.addCellData(cellDataList, i + beginRowIndex, columnIndex + j*2, chargeVarInfo.getPelletsRate());
                                break;
                            }
                            case "块矿%": {
                                ExcelWriterUtil.addCellData(cellDataList, i + beginRowIndex, columnIndex + j*2, chargeVarInfo.getLumporeRate());
                                break;
                            }
                            case "批铁t": {
                                ExcelWriterUtil.addCellData(cellDataList, i + beginRowIndex, columnIndex + j*2, chargeVarInfo.getTheroyHMMass());
                                break;
                            }
                            case "焦比kg/t": {
                                ExcelWriterUtil.addCellData(cellDataList, i + beginRowIndex, columnIndex + j*2, chargeVarInfo.getCokeRate());
                                break;
                            }
                            default: {
                                // 动态类型赋值
                                // 需要累加brandName相同的数据
                                BigDecimal weight = chargeVarMaterials.stream()
                                        .filter(p -> brandName.equals(p.getBrandName()))
                                        .map(ChargeVarMaterial::getWeight)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                ExcelWriterUtil.addCellData(cellDataList, i + beginRowIndex, columnIndex + j*2, weight);
                                break;
                            }
                        }
                    }
                }
                ExcelWriterUtil.setCellValue(sheet, cellDataList);
            }
            handleLiaoXianBianGeng(workbook, sheet, version);
            handJingJiaoJiLu(workbook, sheet, version, dateQuery);
        } catch (Exception e) {
            log.error("处理正面-变料信息出错", e);
        }
    }

    /**
     * 净焦记录
     * @param sheet
     * @param version
     * @param dateQuery
     */
    private void handJingJiaoJiLu (Workbook workbook, Sheet sheet, String version, DateQuery dateQuery) {
        //{挡位.角度}
        Cell cell = PoiCustomUtil.getCellByValue(sheet, "{净焦.批次}");
        if (Objects.isNull(cell)) {
            return;
        }
        int beginRowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();

        List<CellData> cellDataList = new ArrayList<>();
        String queryUrl = getUrlTagNamesInRange(version);
        JSONObject query = new JSONObject();
        query.put("starttime", dateQuery.getQueryStartTime());
        query.put("endtime", dateQuery.getQueryEndTime());
        //"BF8_L2M_NetCokeTime_evt"
        String cellValue = PoiCustomUtil.getSheetCell(workbook, "_tagNames", 0, 0);
        //"BF8_L2M_CokeBatchWeight_evt"
        String tagname = PoiCustomUtil.getSheetCell(workbook, "_tagNames", 1, 0);
        query.put("tagnames", new String[]{cellValue});
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(queryUrl, jsonString);
        JSONObject tag = FastJSONUtil.getJsonObjectByKey(results, Lists.newArrayList("data", cellValue));
        if (Objects.nonNull(tag)) {
            Map<String, Object> innerMap = tag.getInnerMap();
            Set<String> keys = innerMap.keySet();
            int k = 0;
            for (String key : keys) {
                //批次
                ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + k, columnIndex, tag.getBigDecimal(key));
                String url = getLatestTagValueUrl(version);
                Map<String, String> param = new HashMap<>();
                param.put("time", key);
                param.put("tagname", tagname);
                String result = httpUtil.get(url, param);
                BigDecimal tagValue = FastJSONUtil.getJsonValueByKey(result, Lists.newArrayList("data"), "val", BigDecimal.class);
                ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + k, columnIndex + 2, tagValue);
                k++;
            }
        }
        ExcelWriterUtil.setCellValue(sheet, cellDataList);
    }

    /**
     * 档位角度变更
     * @param sheet
     * @param cellDataList
     * @param chargeVarInfos
     */
    private void handleDangWeiJiaoDu (Sheet sheet, List<ChargeVarInfo> chargeVarInfos) {
        List<CellData> cellDataList = new ArrayList<>();
        if (CollectionUtils.isEmpty(chargeVarInfos)) {
            return;
        }
        chargeVarInfos = chargeVarInfos.stream().filter(item -> Objects.nonNull(item.getChargeVarDistribution()) && CollectionUtils.isNotEmpty(item.getChargeVarDistribution())).collect(Collectors.toList());
        //{挡位.角度}
        Cell cell = PoiCustomUtil.getCellByValue(sheet, "{挡位.角度}");
        if (Objects.isNull(cell)) {
            return;
        }
        int beginRowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        for (int i = 0; i < chargeVarInfos.size(); i ++) {
            ChargeVarInfo info = chargeVarInfos.get(i);
            // 开始批数
            Integer index = info.getChargeVarIndex().getChargeNo();
            List<ChargeVarDistribution> chargeVarDistributionList = info.getChargeVarDistribution();
            boolean isFirstFiled = false;
            boolean isSecondFiled = false;
            boolean isThirdFiled = false;
            chargeVarDistributionList.sort(comparing(ChargeVarDistribution::getTyp));
            for(int j = 0; j < chargeVarDistributionList.size(); j++) {
                ChargeVarDistribution distribution = chargeVarDistributionList.get(j);
                switch (distribution.getTyp()) {
                    case 1:
                        // 焦炭
                        isFirstFiled = true;
                        ExcelWriterUtil.addCellData(cellDataList, beginRowIndex, columnIndex, index);
                        ExcelWriterUtil.addCellData(cellDataList, beginRowIndex, columnIndex + 3, "焦炭");
                        ExcelWriterUtil.addCellData(cellDataList, beginRowIndex, columnIndex + 6 +
                                22 - distribution.getPosition() * 2, distribution.getAngle());
                        ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + 1, columnIndex + 6 +
                                22 - distribution.getPosition() * 2, distribution.getRound());
                        break;
                    case 2:
                        // 大烧
                        isSecondFiled = true;
                        int rowIndex = beginRowIndex;
                        if (isFirstFiled) {
                            rowIndex = beginRowIndex + 2;
                        }
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex, index);
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex + 3, "大烧");
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex + 6 +
                                22 - distribution.getPosition() * 2, distribution.getAngle());
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex + 1, columnIndex + 6 +
                                22 - distribution.getPosition() * 2, distribution.getRound());
                        break;
                    case 3:
                        // 小烧
                        isThirdFiled = true;
                        int rowIndex2 = beginRowIndex;
                        if (isFirstFiled) {
                            rowIndex2 = beginRowIndex + 2;
                        }
                        if (isSecondFiled) {
                            rowIndex2 = rowIndex2 + 2;
                        }
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex2, columnIndex, index);
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex2, columnIndex + 3, "小烧");
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex2, columnIndex + 6 +
                                22 - distribution.getPosition() * 2, distribution.getAngle());
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex2 + 1, columnIndex + 6 +
                                22 - distribution.getPosition() * 2, distribution.getRound());
                        break;
                }
            }
            if (isFirstFiled) {
                beginRowIndex = beginRowIndex + 2;
            }
            if (isSecondFiled) {
                beginRowIndex = beginRowIndex + 2;
            }
            if (isThirdFiled) {
                beginRowIndex = beginRowIndex + 2;
            }
        }
        ExcelWriterUtil.setCellValue(sheet, cellDataList);
    }

    private Integer getLatestTagValue(String version, Long time, String tagName) {
        String url = getLatestTagValueUrl(version);
        Map<String, String> param = new HashMap<>();
        param.put("time", String.valueOf(Long.valueOf(time)));
        param.put("tagname", tagName);
        String result = httpUtil.get(url, param);
        Integer val = FastJSONUtil.getJsonValueByKey(result, Lists.newArrayList("data"), "val", Integer.class);
        return val;
    }

    /**
     * 整合料线变更数据
     * @param version
     * @param key
     * @param liaoXianMap
     * @param tagValue
     * @param tag
     */
    private void handleStartIndex(Workbook workbook, String version, Long key, Map<Integer, Map<String, List<Long>>> liaoXianMap, Long tagValue, String tag) {
        //"BF8_L2C_SH_CurrentBatch_evt"
        String cellValue = PoiCustomUtil.getSheetCell(workbook, "_tagNames", 2, 0);
        Integer val = getLatestTagValue(version, key, cellValue);
        if (val != null) {
            if (liaoXianMap.containsKey(val)) {
                liaoXianMap.get(val).put(tag, Lists.newArrayList(tagValue, key));
            } else {
                liaoXianMap.put(val, new HashMap<String, List<Long>>(){{ put(tag, Lists.newArrayList(tagValue, key)); }});
            }
        }
    }

    /**
     * 处理料线变更数据
     * @param version
     */
    private void handleLiaoXianBianGeng (Workbook workbook, Sheet sheet, String version) {
        //{挡位.角度}
        Cell cell = PoiCustomUtil.getCellByValue(sheet, "{开始批次}");
        if (Objects.isNull(cell)) {
            return;
        }
        int beginRowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();

        List<CellData> cellDataList = new ArrayList<>();
        String queryUrl = getUrlTagNamesInRange(version);
        DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(DateUtil.addDays(new Date(), -1));
        JSONObject query = new JSONObject();
        query.put("starttime", String.valueOf(dateQuery.getQueryStartTime()));
        query.put("endtime", String.valueOf(dateQuery.getQueryEndTime()));
        // "BF8_L2C_TP_CokeSetLine_evt", "BF8_L2C_TP_SinterSetLine_evt", "BF8_L2C_TP_LiSinterSetLine_evt"
        String cokeSetLine = PoiCustomUtil.getSheetCell(workbook, "_tagNames", 3, 0);
        String sinterSetLine = PoiCustomUtil.getSheetCell(workbook, "_tagNames", 4, 0);
        String liSinterSetLine = PoiCustomUtil.getSheetCell(workbook, "_tagNames", 5, 0);
        String mainRuler = PoiCustomUtil.getSheetCell(workbook, "_tagNames", 6, 0);
        String[] tagNames = new String[]{cokeSetLine, sinterSetLine, liSinterSetLine};
        query.put("tagnames", tagNames);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(queryUrl, jsonString);

        Map<Integer, Map<String, List<Long>>> liaoXianMap = new HashMap<Integer, Map<String, List<Long>>>();
        if(StringUtils.isNotBlank(results)) {
            TagValueMapDTO tagValueMapDTO = JSON.parseObject(results, TagValueMapDTO.class);
            if(Objects.nonNull(tagValueMapDTO) && Objects.nonNull(tagValueMapDTO.getData())) {
                Map<String, Map<Long, Double>> data = tagValueMapDTO.getData();
                if(Objects.nonNull(data) && data.size() > 0) {
                    for (String tagName:data.keySet()) {
                        Map<Long, Double> map = data.get(tagName);
                        if(Objects.nonNull(map) && map.size() > 0) {
                            for (Long time : map.keySet()) {
                                Long tagValue = map.get(time).longValue();
                                handleStartIndex(workbook, version, time, liaoXianMap, tagValue, tagName);
                            }
                        }
                    }
                }
            }
        }
        //首先查询当天范围内的数据,如果当天没有数据，默认需要显示一条记录，这条记录就是直接查询C OL/OS 这三个点的最新值
        if(liaoXianMap.size() == 0) {
            TagValueListDTO tagValueListDTO = getLatestTagValueListDTO(dateQuery.getEndTime(), version, Arrays.asList(tagNames));
            if (Objects.nonNull(tagValueListDTO) && Objects.nonNull(tagValueListDTO.getData())) {
                List<TagValue> list = tagValueListDTO.getData();
                Map<String, List<Long>> map = new HashMap<>();
                for (TagValue tagValue:list) {
                    if (Objects.nonNull(tagValue) && StringUtils.isNotBlank(tagValue.getName()) && Objects.nonNull(tagValue.getVal())) {
                        List<Long> longList = Arrays.asList(tagValue.getVal().longValue(), tagValue.getClock().getTime());
                        map.put(tagValue.getName(), longList);
                    }
                }
                if (map.size() > 0) {
                    liaoXianMap.put(1, map);
                }
            }
        }
        int index = 0;
        for(Map.Entry<Integer, Map<String, List<Long>>> entry : liaoXianMap.entrySet()) {
            Integer mapKey = entry.getKey();
            Map<String, List<Long>> mapValue = entry.getValue();
            ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex, mapKey);
            Long time = 0L;
            for(String key : mapValue.keySet()){
                Long val = mapValue.get(key).get(0);
                Long timeVal = mapValue.get(key).get(1);
                if(time < timeVal) {
                    time = timeVal;
                }
                if (key.equals(cokeSetLine)) {
                    ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 3, val);
                }else {
                    ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 5,
                            mapValue.get(sinterSetLine).get(0)+ "/" + mapValue.get(liSinterSetLine).get(0));
                }
            }
            Integer val = getLatestTagValue(version, time, mainRuler);
            ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 7, val);
            index++;
        }
        ExcelWriterUtil.setCellValue(sheet, cellDataList);
    }

    //结束--------------------正面-变料信息--------------------

    /**
     * 反面-第一行数据
     * @param excelDTO
     * @param workbook
     * @param version
     */
    private void handleFirstRowData(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        // 需要写入的单元格数据对象
        List<CellData> resultList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            //入炉铁分
            BigDecimal ruLuTieFen = getLatestByCategoryAndItem(version, new HashMap(){{
                put("category", "IRON");
                put("anaitemname", "TFe");
                put("granularity", "day");
            }});
            // 批铁量
            BigDecimal piTieLiang = getLatestByCategoryAndItem(version, new HashMap(){{
                put("category", "IRON");
                put("anaitemname", "weightset");
                put("granularity", "day");
            }});
            // 焦比
            BigDecimal jiaoBi = getLatestByCategoryAndItem(version, new HashMap(){{
                put("category", "FUEL");
                put("anaitemname", "weightset");
                put("granularity", "day");
            }});
            //找到待填充的坐标
            Cell cell = PoiCustomUtil.getCellByValue(sheet, "{入炉铁份}");
            if (Objects.nonNull(cell)) {
                // 待填充的行,列
                int beginRow = cell.getRowIndex();
                int beginColumn = cell.getColumnIndex();
                if (Objects.nonNull(ruLuTieFen)) {
                    ExcelWriterUtil.addCellData(resultList, beginRow, beginColumn, ruLuTieFen.multiply(new BigDecimal(100)));
                }
            }

            //找到待填充的坐标
            Cell piTieLiangCell = PoiCustomUtil.getCellByValue(sheet, "{批铁量}");
            if (Objects.nonNull(piTieLiangCell)) {
                // 待填充的行,列
                int beginRow = piTieLiangCell.getRowIndex();
                int beginColumn = piTieLiangCell.getColumnIndex();
                if (Objects.nonNull(piTieLiang) && Objects.nonNull(ruLuTieFen)) {
                    ExcelWriterUtil.addCellData(resultList, beginRow, beginColumn, piTieLiang.multiply(ruLuTieFen));
                }
            }

            //找到待填充的坐标
            Cell jiaoBiCell = PoiCustomUtil.getCellByValue(sheet, "{焦比}");
            if (Objects.nonNull(jiaoBiCell)) {
                // 待填充的行,列
                int beginRow = jiaoBiCell.getRowIndex();
                int beginColumn = jiaoBiCell.getColumnIndex();
                if (Objects.nonNull(piTieLiang) && Objects.nonNull(ruLuTieFen) && Objects.nonNull(jiaoBi)) {
                    ExcelWriterUtil.addCellData(resultList, beginRow, beginColumn, jiaoBi.divide(piTieLiang.multiply(ruLuTieFen), 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(1000)));
                }
            }

            ExcelWriterUtil.setCellValue(sheet, resultList);
        } catch (Exception e) {
            log.error("处理反面-第一行数据出错", e);
        }
    }

    // 反面-出铁
    private void handleTapData(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        // 需要写入的单元格数据对象
        List<CellData> resultList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(date.getRecordDate());
            // 处理出铁数据
            List<String> tapNoList = handleTapData(workbook, sheet, 6, dateQuery, resultList, version, null);
            // 处理罐号重量数据
            if (CollectionUtils.isNotEmpty(tapNoList)) {
                handleTpcInfoData(sheet, resultList, tapNoList, version);
            }
            tapNoList = handleTapData(workbook, sheet, 26, dateQuery, resultList, version, tapNoList);

            ExcelWriterUtil.setCellValue(sheet, resultList);
        } catch (Exception e) {
            log.error("处理反面-出铁参数出错", e);
        }
    }

    /**
     * 处理出铁数据
     * @param sheet
     * @param dateQuery
     * @param resultList
     * @param version
     * @return tapNoList
     */
    private List<String> handleTapData(Workbook workbook, Sheet sheet, int itemRowNum, DateQuery dateQuery, List<CellData> resultList, String version, List<String> tapNoList) {
        // 铁次(tapNo)list，用于调接口罐号重量接口
        if (tapNoList == null) {
            tapNoList = new ArrayList<>();
        }
        try {
            // 隐藏标记行
            sheet.getRow(itemRowNum).setZeroHeight(true);
            // 1、组装出铁数据URL
            Map<String, String> queryParam = this.getQueryParam(dateQuery);
            queryParam.put("pagenum", "1");
            queryParam.put("pagesize", "10");
            // 2、获取数据并反序列化为java对象
            String tapData = httpUtil.get(getTapsInRange(version), queryParam);
            if (StringUtils.isBlank(tapData)) {
                return null;
            }
            PageData<TapSgRow> pageData = JSON.parseObject(tapData, new TypeReference<PageData<TapSgRow>>(){});
            if (Objects.isNull(pageData)) {
                return null;
            }
            List<TapSgRow> tapSgRowData = pageData.getData();
            if (CollectionUtils.isEmpty(tapSgRowData)) {
                return null;
            }
            tapSgRowData.sort(comparing(TapSgRow::getStartTime)); // 按时间先后进行排序
            int dataSize = tapSgRowData.size();
            List<String> itemRow = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
            if (CollectionUtils.isEmpty(itemRow)) {
                return null;
            }
            int itemDataSize = itemRow.size();
            TapSgRow tapSgRow = null;
            // 需要对时间数据进行格式化处理
            String[] timeItemArray = {"startTime", "slagTime", "endTime"};
            List<String> timeItemList = Arrays.asList(timeItemArray);
            //"BF8_L2M_SH_ChargeCount_evt"
            String cellValue = PoiCustomUtil.getSheetCell(workbook, "_tagNames", 7, 0);
            // 遍历标记行
            for (int i = 0; i < dataSize; i++) {
                tapSgRow = tapSgRowData.get(i);
                tapNoList.add(tapSgRow.getTapNo());
                // 将对象转为Map,key为对象引用名
                Map<String, Object> stringObjectMap = JSON.parseObject(JSON.toJSONString(tapSgRow), new TypeReference<Map<String, Object>>(){});
                Map<String, Double> tapValues = tapSgRow.getTapValues();
                Map<String, Double> slagAnalysis = tapSgRow.getSlagAnalysis();
                Map<String, Double> hmAnalysis = tapSgRow.getHmAnalysis();
                //批号
                String url = getLatestTagValueUrl(version);
                Map<String, String> param = new HashMap<>();
                param.put("time", String.valueOf(stringObjectMap.get("endTime")));
                param.put("tagname", cellValue);
                String result = httpUtil.get(url, param);
                Integer tagValue = FastJSONUtil.getJsonValueByKey(result, Lists.newArrayList("data"), "val", Integer.class);
                ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, 1, tagValue);
                //批料数
                String queryUrl = getUrlTagNamesInRange(version);
                JSONObject query = new JSONObject();
                query.put("starttime", String.valueOf(stringObjectMap.get("startTime")));
                query.put("endtime", String.valueOf(stringObjectMap.get("endTime")));
                query.put("tagnames", new String[]{cellValue});
                SerializeConfig serializeConfig = new SerializeConfig();
                String jsonString = JSONObject.toJSONString(query, serializeConfig);
                String results = httpUtil.postJsonParams(queryUrl, jsonString);
                JSONObject tagData = FastJSONUtil.getJsonObjectByKey(results, Lists.newArrayList("data", cellValue));
                if (Objects.nonNull(tagData)) {
                    ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, 9, tagData.size());
                }
                for (int j = 0; j < itemDataSize; j++) {
                    String itemData = itemRow.get(j);
                    if (StringUtils.isBlank(itemData)) {
                        continue;
                    }
                    String[] itemArray = itemData.split("_");
                    if (itemArray.length == 2 && "item".equals(itemArray[0])) {
                        Object value = stringObjectMap.get(itemArray[1]);
                        if (Objects.isNull(value)) {
                            continue;
                        }
                        // 对时间数据进行格式化处理
                        if (timeItemList.contains(itemArray[1])) {
                            LocalDateTime localDate = Instant.ofEpochMilli(Long.valueOf(String.valueOf(value))).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            value = DateTimeFormatter.ofPattern("HH:mm:ss").format(localDate);
                            // yyyy-MM-dd HH:mm:ss
                        }
                        ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, j, value);
                    } else if (itemArray.length == 3 && "item".equals(itemArray[0])) {
                        switch (itemArray[1]) {
                            case "tapValues":
                                if (Objects.isNull(tapValues) || tapValues.size() == 0) {
                                    continue;
                                }
                                ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, j, tapValues.get(itemArray[2]));
                                break;
                            case "slagAnalysis":
                                if (Objects.isNull(slagAnalysis) || slagAnalysis.size() == 0 || slagAnalysis.get(itemArray[2]) == null) {
                                    continue;
                                }
                                Double val = slagAnalysis.get(itemArray[2]);
                                //R2不乘100
                                if (!itemArray[2].equals("B2") && !itemArray[2].equals("B4")) {
                                    val = slagAnalysis.get(itemArray[2]) * 100;
                                }
                                ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, j, val);
                                break;
                            case "hmAnalysis":
                                if (Objects.isNull(hmAnalysis) || hmAnalysis.size() == 0 || hmAnalysis.get(itemArray[2]) == null) {
                                    continue;
                                }
                                ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, j, hmAnalysis.get(itemArray[2]) * 100);
                                break;
                        }
                    } else {
                        log.warn("excel标记项格式错误:" + itemData);
                    }
                }
            }

            return tapNoList;
        } catch (Exception e) {
            log.error("处理反面-出铁出错", e);
            return tapNoList;
        }
    }

    /**
     * 处理罐号重量数据
     * @param sheet
     * @param resultList
     * @param tapNoList
     * @param version
     */
    private void handleTpcInfoData(Sheet sheet, List<CellData> resultList, List<String> tapNoList, String version) {
        try {
            Cell tpcNoCell = PoiCustomUtil.getCellByValue(sheet, "{罐号}");
            if (Objects.isNull(tpcNoCell)) {
                return;
            }
            int tpcNoBeginRow = tpcNoCell.getRowIndex();
            int tpcNoBeginColumn = tpcNoCell.getColumnIndex();
            // 组装罐号重量数据URL(基于出铁返回的铁次)
            for (int i = 0; i < tapNoList.size(); i++) {
                Map<String, String> queryParam = new HashMap<>();
                queryParam.put("tapNo", tapNoList.get(i));
                String tpcInfoData = httpUtil.get(getTpcInfoByTapNo(version), queryParam);
                if (StringUtils.isBlank(tpcInfoData)) {
                    return;
                }
                TapTPCDTO tapTPCDTO = JSON.parseObject(tpcInfoData, TapTPCDTO.class);
                if (Objects.isNull(tapTPCDTO)) {
                    return;
                }
                List<TapTPC> tapTPCList = tapTPCDTO.getData();
                if (CollectionUtils.isEmpty(tapTPCList)) {
                    return;
                }
                int dataSize = tapTPCList.size();
                for (int j = 0; j < dataSize; j++) {
                    TapTPC tapTPC = tapTPCList.get(j);
                    String tpcNo = tapTPC.getTpcNo();
                    BigDecimal netWt = tapTPC.getNetWt();
                    int writeRow = tpcNoBeginRow + i;
                    int writeColumn = tpcNoBeginColumn + j * 2;
                    ExcelWriterUtil.addCellData(resultList, writeRow, writeColumn, tpcNo);
                    ExcelWriterUtil.addCellData(resultList, writeRow, writeColumn + 1, netWt);
                }
            }
        } catch (Exception e) {
            log.error("处理反面-罐号重量出错", e);
        }
    }

    /**
     * 处理反面-块矿-矿种数据
     *
     * @param sheet
     * @param dateQuery
     * @param resultList
     * @param version
     * @param type
     * @return
     */
    private void handOreBlockData(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        String brandCodeType = "LUMPORE";
        String oreBlockType = "LC";
        // 待填充的数据
        String[] arr = {"TFe", "Al2O3", "CaO", "MgO", "SiO2", "S" ,"P"};
        List<CellData> cellDataList = new ArrayList<>();
        // 反面sheet
        Sheet sheet = null;
        try {
            sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(date.getRecordDate());

            // 1. 找到待填充的坐标
            Cell tpcNoCell = PoiCustomUtil.getCellByValue(sheet, "{块矿.矿种}");
            if (Objects.isNull(tpcNoCell)) {
                return;
            }
            // 待填充的行,列
            int tpcNoBeginRow = tpcNoCell.getRowIndex();
            int tpcNoBeginColumn = tpcNoCell.getColumnIndex();
            // 检查表头列是否存在，如果不存在则不填充数据
            checkHeaders(sheet, arr, new HashMap<>(), tpcNoBeginRow-1);
            // 2.获取brandCode
            List<String> brandCodeList = getBrandCodeData(version, dateQuery, brandCodeType);
            String endTime = Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString();
            if (brandCodeList.isEmpty()) {
                return;
            }
            Map<String, String> brandCodeToDescrMap = getBrandCodeToDescrMap(version);
            // 2.遍历brandCodeList取出所有数据
            for (int i = 0; i < brandCodeList.size(); i++) {
                // 3. 根据brandCode和 type调接口获取数据
                String url = getAnalysisValueUrl(version, endTime, oreBlockType, brandCodeList.get(i));
                setAnalysisValue2Cell(sheet, cellDataList, arr, url,
                        tpcNoBeginRow + i, tpcNoBeginColumn+1, true);
                ExcelWriterUtil.addCellData(cellDataList, tpcNoBeginRow+i, tpcNoBeginColumn, brandCodeToDescrMap.get(brandCodeList.get(i)));
            }
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理反面-块矿-矿种出错", e);
        }
    }

    /**
     * 取出AnalysisValue中的data，并给相应的cell赋值
     * @param sheet
     * @param cellDataList
     * @param arr
     * @param url
     * @param queryParam
     * @param tpcNoBeginRow
     * @param tpcNoBeginColumn
     * @return
     */
    private void setAnalysisValue2Cell(Sheet sheet, List<CellData> cellDataList, String[] arr, String url,
                                       int tpcNoBeginRow, int tpcNoBeginColumn, boolean needHandle) {
        String jsonData = httpUtil.get(url);
        // 根据json映射对象DTO
        AnalysisValueDTO analysisValueDTO = null;
        if (StringUtils.isNotBlank(jsonData)) {
            analysisValueDTO = JSON.parseObject(jsonData, AnalysisValueDTO.class);
        }
        if (Objects.isNull(analysisValueDTO)) {
            return;
        }
        List<AnalysisValue> oreBlockList = analysisValueDTO.getData();
        if (oreBlockList == null || oreBlockList.isEmpty()) {
            log.warn(url + " 接口中无数据");
            return;
        }
        setAnalysisValue2Cell(sheet, cellDataList, arr, oreBlockList, tpcNoBeginRow, tpcNoBeginColumn, needHandle);
    }

    /**
     * 取出AnalysisValue中的data，并给相应的cell赋值
     * @param sheet
     * @param cellDataList
     * @param arr
     * @param url
     * @param queryParam
     * @param tpcNoBeginRow
     * @param tpcNoBeginColumn
     * @return
     */
    private void setAnalysisValue2Cell(Sheet sheet, List<CellData> cellDataList, String[] arr, List<AnalysisValue> oreBlockList,
                                       int tpcNoBeginRow, int tpcNoBeginColumn, boolean needHandle) {
        if (oreBlockList == null || oreBlockList.isEmpty()) {
            return;
        }
        int dataSize = arr.length;
        for (AnalysisValue analysisValue : oreBlockList) {
            if (oreBlockList.indexOf(analysisValue) > 2) break;
            if (Objects.isNull(analysisValue)) return;
            for (int j = 0; j < dataSize; j++){
                Map<String, BigDecimal> values = analysisValue.getValues();
                if (Objects.isNull(values)) return;
                // 获取具体AnalysisValue数据
                if (!values.containsKey(arr[j])) continue;
                BigDecimal value = values.get(arr[j]);
                int writeRow = tpcNoBeginRow;
                int writeColumn = tpcNoBeginColumn + j;
                if (value != null && needHandle) {
                    // 填充具体数据
                    ExcelWriterUtil.addCellData(cellDataList, writeRow, writeColumn, value.multiply(new BigDecimal(100)));
                } else {
                    ExcelWriterUtil.addCellData(cellDataList, writeRow, writeColumn, value);
                }
            }
        }
    }

    /**
     * 构造取回AnalysisValue所需要的参数，然后给cell赋值
     * @param sheet
     * @param cellDataList
     * @param url
     * @param arr
     * @param heatPlaceHolder
     * @param brandCode
     * @param type
     * @param isNeedMapping
     * @return
     */
    private void handAnalysisValue2Cell(Sheet sheet, List<CellData> cellDataList, String url, String[] arr,
                                        String placeHolder, boolean needHandle) {
        // 找到待填充的坐标
        Cell tpcNoCell = PoiCustomUtil.getCellByValue(sheet, placeHolder);
        if (Objects.isNull(tpcNoCell)) {
            return;
        }
        // 待填充的行,列
        int tpcNoBeginRow = tpcNoCell.getRowIndex();
        int tpcNoBeginColumn = tpcNoCell.getColumnIndex();

        setAnalysisValue2Cell(sheet, cellDataList, arr, url, tpcNoBeginRow, tpcNoBeginColumn, needHandle);
    }

    /**
     * 处理AnalysisValue相关的数据，包含成分分析(焦炭和煤粉)和热力强度
     * @param excelDTO
     * @param workbook
     * @param version
     */
    private void handAnalysisValue(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        String heatType = "LG";
        String cokeType = "LC";
        String cokeBrandCode = "COKE";
        String coalBrandCode = "FBFM-A_COAL";
        String cokePlaceHolderYe = "{焦炭.夜}";
        String cokePlaceHolderBai = "{焦炭.白}";
        String coalPlaceHolder = "{煤粉}";
        String[] cokeArr = {"H2O", "Ad", "Vdaf", "S", "M40", "M10", "CSR", "CRI"};
        // 根据需求，取回的煤粉数据中C取Fcad，S取Std
        String[] coalArr = {"Ad", "Vdaf", "Std", "Fcad", "H2O"};
        List<CellData> cellDataList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(date.getRecordDate());
            // 获取两班倒查询策略
            Date dateRun = this.getDateQueryBeforeOneDay(excelDTO).getRecordDate();
            List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourAheadTwoHour(dateRun);
            for (int k = 0; k < dateQueries.size(); k++) {
                DateQuery dateQuery1 = dateQueries.get(k);
                //夜班
                if (dateQuery1.getStartTime().compareTo(dateQuery.getStartTime()) == 0) {
                    handJiaoTan(sheet, version, dateQuery1, "COKE", cellDataList, cokeArr, cokePlaceHolderYe);
                } else {
                    handJiaoTan(sheet, version, dateQuery1, "COKE", cellDataList, cokeArr, cokePlaceHolderBai);
                }
            }
            // 4. 获取煤粉数据，填充数据
            List<AnalysisValue> oreBlockList = getAnalysisValuesByBrandCode(version, String.valueOf(dateQuery.getQueryStartTime()),
                    String.valueOf(dateQuery.getQueryEndTime()), coalBrandCode);
            // 找到待填充的坐标
            Cell tpcNoCell = PoiCustomUtil.getCellByValue(sheet, coalPlaceHolder);
            if (Objects.isNull(tpcNoCell)) {
                return;
            }
            // 待填充的行,列
            int tpcNoBeginRow = tpcNoCell.getRowIndex();
            int tpcNoBeginColumn = tpcNoCell.getColumnIndex();
            String url = getAnalysisValueUrl(version, String.valueOf(dateQuery.getQueryEndTime()), "LC", coalBrandCode);
            setAnalysisValue2Cell(sheet, cellDataList, coalArr, url,
                    tpcNoBeginRow, tpcNoBeginColumn, true);
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理反面-块矿-矿种出错", e);
        }
    }

    /**
     * 处理焦炭数据
     * @param sheet
     * @param url
     * @param cellDataList
     * @param dateQuery
     * @param arr
     * @param placeHolder
     */
    private void handJiaoTan(Sheet sheet, String version, DateQuery dateQuery, String brandCode, List<CellData> cellDataList,
                             String[] arr, String placeHolder) {
        List<String> noNeedHandleList = Arrays.asList("M40", "M10", "CSR", "CRI");
        List<AnalysisValue> list = getAnalysisValuesRequestDataByCode(version, dateQuery, brandCode);
        if (list == null || list.isEmpty()) {
            return;
        }
        int dataSize = arr.length;
        // 找到待填充的坐标
        Cell tpcNoCell = PoiCustomUtil.getCellByValue(sheet, placeHolder);
        if (Objects.isNull(tpcNoCell)) {
            return;
        }
        // 待填充的行,列
        int tpcNoBeginRow = tpcNoCell.getRowIndex();
        int tpcNoBeginColumn = tpcNoCell.getColumnIndex();
        for (int j = 0; j < dataSize; j++){
            int finalJ = j;
            BigDecimal averageValue = list.stream().map(AnalysisValue::getValues)
                    .map(e -> e.get(arr[finalJ])).filter(e -> e != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(list.size()), 4, BigDecimal.ROUND_HALF_UP);
            int writeRow = tpcNoBeginRow;
            int writeColumn = tpcNoBeginColumn + j;
            if (averageValue != null && !noNeedHandleList.contains(arr[j])) {
                ExcelWriterUtil.addCellData(cellDataList, writeRow, writeColumn, averageValue.multiply(new BigDecimal(100)));
            } else {
                // 填充具体数据
                ExcelWriterUtil.addCellData(cellDataList, writeRow, writeColumn, averageValue);
            }
        }
    }

    /**
     * 表头节点不存在或者不在指定行，若表头节点被删除则该列不填充数据
     * 某些表头节点名也许不是返回数据节点名，映射关系存放在headerMap中
     * @param sheet
     * @param headers
     * @param headerMap
     * @param rowIndex
     */
    private void checkHeaders(Sheet sheet, String[] headers, Map<String, String> headerMap, int rowIndex) {

        List<String> arrList = new ArrayList<String>(Arrays.asList(headers));
        for (String item :headers) {
            Cell cell = null;
            if (headerMap.containsKey(item)) {
                cell = PoiCustomUtil.getCellByValue(sheet, headerMap.get(item));
            } else {
                cell = PoiCustomUtil.getCellByValue(sheet, item);
            }
            List<String> list = PoiCustomUtil.getRowCelVal(sheet, rowIndex);
            if (Objects.isNull(cell)) {
                arrList.remove(item);
            } else if (list != null && !list.contains(item)) {
                arrList.remove(item);
            }
        }
        headers = arrList.toArray(new String[arrList.size()]);
    }

    /**
     * 处理烧结矿理化分析数据
     * @param excelDTO
     * @param workbook
     * @param version
     */
    private void handAnalysisValues(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        List<CellData> cellDataList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(date.getRecordDate());
            // 1. 获取AnalysisValues Url前缀
            List<AnalysisValue> sinterList = getAnalysisValuesRequestDataByCode(version, dateQuery, "SINTER");
            String placeHolder = "{矿种}";
            String[] arr = {"TFe", "FeO", "CaO", "MgO", "SiO2", "S", "B2", "Zn","Drum", "S+40",
                    "S25-40", "S16-25", "S10-16", "S5-10", "S-5", "S-10", "SF"};
            String[] handleArr = {"TFe", "FeO", "CaO", "MgO", "SiO2", "S", "Zn"};
            handAnalysisValuesData(sheet, sinterList, cellDataList, placeHolder, arr, handleArr, version, 6);

            String placeHolder2 = "{球团}";
            String[] arr2 = {"TFe", "FeO", "CaO", "MgO", "SiO2", "S", "", "Zn"};
            String[] handleArr2 = {"TFe", "FeO", "CaO", "MgO", "SiO2", "S", "Zn"};
            List<AnalysisValue> allAnalysisValues = getAnalysisValuesRequestDataByCode(version, dateQuery, "PELLETS");
            //球团名称
            handAnalysisValuesData(sheet, allAnalysisValues, cellDataList, placeHolder2, arr2, handleArr2, version, 5);
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理反面-烧结矿理化分析出错", e);
        }
    }

    /**
     * 处理烧结矿理化数据
     * @param sheet
     * @param brandCode
     * @param pageNum
     * @param result
     * @param cellDataList
     */
    private void handAnalysisValuesData(Sheet sheet, List<AnalysisValue> list, List<CellData> cellDataList,
                                        String placeHolder, String[] arr, String[] handleArr, String version, int maxRow) {

        List< String> lcList = new ArrayList<String>(handleArr.length);
        Collections.addAll(lcList, handleArr);
        // 矿种坐标
        Cell cell = PoiCustomUtil.getCellByValue(sheet, placeHolder);
        if (Objects.isNull(cell)) {
            return;
        }
        // 待填充的行,列
        int beginRow = cell.getRowIndex();
        int coalBeginColumn = 1;

        if (Objects.isNull(list) || CollectionUtils.isEmpty(list)) {
            return;
        }
        // 升序
        Collections.sort(list, new Comparator<AnalysisValue>(){
            @Override
            public int compare(AnalysisValue p1, AnalysisValue p2) {
                if (p1.getAnalysis() == null || p2.getAnalysis() == null) {
                    return 0;
                }
                if (StringUtils.isBlank(p1.getAnalysis().getSampleid()) && StringUtils.isBlank(p2.getAnalysis().getSampleid())) {
                    return 0;
                }
                if (p1.getAnalysis().getSampleid().compareTo(p2.getAnalysis().getSampleid()) > 0){
                    return 1;
                }else if (p1.getAnalysis().getSampleid().compareTo(p2.getAnalysis().getSampleid()) > 0){
                    return 0;
                }else{
                    return -1;
                }
            }
        });
        Map<String, String> brandCodeToDescrMap = getBrandCodeToDescrMap(version);
        for (int i = 0; i< list.size() && i < maxRow; i++) {
            AnalysisValue analysisValue = list.get(i);
            if (Objects.isNull(analysisValue)) continue;
            Analysis analysis = analysisValue.getAnalysis();
            final String sampleid = analysis != null ? analysis.getSampleid() : null;
            String type = analysis != null ? analysis.getType() : null;
            // 处理矿种、记录时间、编号、槽号
            if (!Objects.isNull(analysis)) {
                //矿种
                ExcelWriterUtil.addCellData(cellDataList, beginRow + i, coalBeginColumn, brandCodeToDescrMap.get(analysis.getBrandcode()));
                // 记录时间
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date clock = analysis.getClock();
                String time = sdf.format(clock);
                if (StringUtils.isNotBlank(time)) {
                    ExcelWriterUtil.addCellData(cellDataList, beginRow + i, coalBeginColumn + 1, time);
                }
                // 编号
                if (StringUtils.isNotBlank(sampleid)) {
                    ExcelWriterUtil.addCellData(cellDataList, beginRow + i, coalBeginColumn + 2, sampleid);
                }
            }
            Map<String, BigDecimal> values = analysisValue.getValues();
            if (Objects.isNull(values)) continue;
            for (int j = 0; j < arr.length; j++){
                //16-25 = 100 -其他
                // 获取具体AnalysisValue数据
                //<10  是 <5 + 5-10
                int writeColumn = coalBeginColumn + 3 + j;
                if (arr[j].equals("S-10")) {
                    if (values.containsKey("S-5") && values.containsKey("S5-10")) {
                        BigDecimal s_5 = values.get("S-5");
                        BigDecimal s5_10 = values.get("S5-10");
                        BigDecimal value = s_5.add(s5_10);
                        ExcelWriterUtil.addCellData(cellDataList, beginRow + i, writeColumn, value);
                    }
                } else if (arr[j].equals("S16-25")) {
                    if (values.containsKey("S-5") && values.containsKey("S5-10") && values.containsKey("S+40")
                            && values.containsKey("S25-40") && values.containsKey("S10-16")) {
                        BigDecimal s_5 = values.get("S-5");
                        BigDecimal s5_10 = values.get("S5-10");
                        BigDecimal s40 = values.get("S+40");
                        BigDecimal s25_40 = values.get("S25-40");
                        BigDecimal s10_16 = values.get("S10-16");
                        BigDecimal value = new BigDecimal(100).subtract(s_5).subtract(s5_10).subtract(s10_16).
                                subtract(s25_40).subtract(s40);
                        ExcelWriterUtil.addCellData(cellDataList, beginRow + i, writeColumn, value);
                    }
                } else {
                    if (!values.containsKey(arr[j])) continue;
                    BigDecimal value = values.get(arr[j]);
                    if (lcList.contains(arr[j])) {
                        value = value.multiply(new BigDecimal(100));
                    }
                    ExcelWriterUtil.addCellData(cellDataList, beginRow + i, writeColumn, value);
                }
            }
        }
    }

    private List<AnalysisValue> getAnalysisValuesRequestDataByCode(String version, DateQuery dateQuery, String brandCodeType) {
        List<AnalysisValue> allAnalysisValues = new ArrayList<>();
        List<String> list = getBrandCodeData(version, DateQueryUtil.buildDayAheadTwoHour(dateQuery.getRecordDate()), brandCodeType);
        if(Objects.nonNull(list) && CollectionUtils.isNotEmpty(list)) {
            for (String brandCode:list) {
                allAnalysisValues.addAll(getAnalysisValuesByBrandCode(version, String.valueOf(dateQuery.getQueryStartTime()), String.valueOf(dateQuery.getQueryEndTime()), brandCode));
            }
        }
        return allAnalysisValues;
    }

    /**
     * 处理反面大记事和操作简析
     * @param excelDTO
     * @param workbook
     * @param version
     */
    private void handleCaoZuoJianxi(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        try {
            List<CellData> cellDataList = new ArrayList<CellData>();
            Sheet sheet = workbook.getSheetAt(1);
            String url = getQueryBlastStatus(version);
            DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
            //id=1操作简析
            handleCommitInfo(1, 1, version, sheet, cellDataList, "{操作简析.夜班}", date);
            handleCommitInfo(2, 1, version, sheet, cellDataList, "{操作简析.白班}", date);
            //id=2大记事
            handleCommitInfo(1, 2, version, sheet, cellDataList, "{大记事.夜班}", date);
            handleCommitInfo(2, 2, version, sheet, cellDataList, "{大记事.白班}", date);
            //id=3调度主任
            handleCommitInfo(1, 3, version, sheet, cellDataList, "{调度.夜班}", date);
            handleCommitInfo(2, 3, version, sheet, cellDataList, "{调度.白班}", date);
            //id=4工长
            handleCommitInfo(1, 4, version, sheet, cellDataList, "{区工.夜班}", date);
            handleCommitInfo(2, 4, version, sheet, cellDataList, "{区工.白班}", date);
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理反面-操作简析/大事记出错", e);
        }
    }

    /**
     * 获取commit info
     * date 传当前天整点，shift  1 夜班  2 白班，model 写死
     * @param shift
     * @param id
     * @param version
     * @param workbook
     * @param cellDataList
     * @param placeHolder
     * @param dateQuery
     */
    private void handleCommitInfo(int shift, int id, String version, Sheet sheet, List<CellData> cellDataList, String placeHolder, DateQuery dateQuery) {
        // 获取标志位的坐标
        //{操作简析.夜班}
        Cell cell = PoiCustomUtil.getCellByValue(sheet, placeHolder);
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        //id=1操作简析
        String commit = getShiftLogCommitInfo(version, dateQuery.getQueryStartTime(), shift, id);
        ExcelWriterUtil.addCellData(cellDataList, rowIndex, columnIndex, commit);
    }
}
