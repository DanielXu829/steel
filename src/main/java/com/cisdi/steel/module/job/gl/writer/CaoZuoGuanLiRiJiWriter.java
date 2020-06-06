package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.AnalysisValueDTO;
import com.cisdi.steel.dto.response.gl.TagValueMapDTO;
import com.cisdi.steel.dto.response.gl.TapTPCDTO;
import com.cisdi.steel.dto.response.gl.req.TagQueryParam;
import com.cisdi.steel.dto.response.gl.res.*;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
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

        } catch (Exception e) {
            log.error("处理模板数据失败", e);
        } finally {
            Date currentDate = new Date();
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
    protected void handleFacadeXiaoShiCanShu(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        try {
            String queryUrl = getUrlTagNamesInRange(version);

            // 动态报表生成的模板默认取第二个sheet。
            String sheetName = "_FacadeXiaoShiCanShu_day_hour";
            Sheet sheet = workbook.getSheet(sheetName);
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                DateQuery dateQuery = this.getDateQuery(excelDTO);
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, dateQuery.getRecordDate());
                // evt为后缀的值需要此逻辑，防止取到前一天累计的值。
                DateQuery firstDateQuery = dateQueries.get(0);
                firstDateQuery.setStartTime(DateUtil.getDateBeginTime(firstDateQuery.getEndTime()));
                dateQueries.set(0, firstDateQuery);

                // 直接拿到tag点名, 无需根据别名再去获取tag点名
                List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(sheet);
                for (int rowNum = 0; rowNum < dateQueries.size(); rowNum++) {
                    List<CellData> cellDataList = handleEachRowData(tagNames, queryUrl, dateQueries.get(rowNum), rowNum*3 + 1);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
            // TODO 煤枪数支
            // TODO 齿轮箱水温差℃
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
                //如果是evt结尾的, 取时间范围内最大值
                Double maxVal = 0.0d;
                for (int j = 0; j < clockList.size(); j++) {
                    Long tempTime = clockList.get(j);
                    Date date = new Date(tempTime);
                    if ((date.getTime() >= queryStartTime.getTime()) && (date.getTime() <= queryEndTime.getTime())) {
                        Double defaultVal = tagValueMap.get(tempTime);
                        if (defaultVal > maxVal) {
                            maxVal = defaultVal;
                        }
                    }
                }
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
        if (StringUtils.isNotBlank(jsonData)) {
            JSONObject object = JSON.parseObject(jsonData);
            if (Objects.nonNull(object)) {
                object = object.getJSONObject("data");
                if (Objects.nonNull(object)) {
                    Map<String, Object> innerMap = object.getInnerMap();
                    if (Objects.isNull(innerMap)) {
                        return;
                    }
                    for (int i = 0; i < 36; i++) {
                        if (Objects.nonNull(innerMap.get(i+1+"")) && (int)innerMap.get(i+1+"") != 0) {
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, i*2 + columnIndex, "×");
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex + 2, i*2 + columnIndex, "×");
                        } else {
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, i*2 + columnIndex, "√");
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex + 2, i*2 + columnIndex, "√");
                        }
                    }
                }
            }
        }
    }

    //开始--------------------正面-风口信息--------------------
    protected void handleFengKouXinXi(WriterExcelDTO excelDTO, Workbook workbook, String version) {
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
            String url = httpProperties.getGlUrlVersion(version) + "/bfBlast/queryBlastStatus";
            DateQuery date = this.getDateQuery(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
            Date dateRun = this.getDateQuery(excelDTO).getRecordDate();
            List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourEach(dateRun);
            for (int k = 0; k < dateQueries.size(); k++) {
                DateQuery dateQuery1 = dateQueries.get(k);
                if (dateQuery1.getRecordDate().compareTo(dateQuery.getStartTime()) == 0) {
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
            ExcelWriterUtil.setCellValue(sheet, cellData);
        } catch (Exception e) {
            log.error("处理正面-风口信息出错", e);
        }
    }
    //结束--------------------正面-风口信息--------------------


    //开始--------------------正面-变料信息--------------------
    protected void handleBianLiaoXinXi(WriterExcelDTO excelDTO,Workbook workbook,String version){
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
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(new Date());
            List<ChargeVarInfo> chargeVarInfos = getChargeVarInfo(version, dateQuery);
            List<CellData> cellDataList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(chargeVarInfos)) {
                handleDangWeiJiaoDu (sheet, chargeVarInfos);
                //动态类型的添加顺序
                List<String> luLiaoList = new ArrayList<String>(){{
                    add("开始批次");
                    add("矿批t");
                    add("焦批t");
                    add("焦炭负荷");
                    add("球团%");
                    add("块矿%");
                    add("批铁t");
                    add("焦比kg/t");
                }};
                // 初始化动态生成的炉料变更种类
                ChargeVarInfo info = chargeVarInfos.get(0);
                if (Objects.nonNull(info)) {
                    List<ChargeVarMaterial> chargeVarMaterials = info.getChargeVarMaterial();
                    if (Objects.nonNull(chargeVarMaterials) && CollectionUtils.isNotEmpty(chargeVarMaterials)) {
                        for (ChargeVarMaterial material:chargeVarMaterials) {
                            if (!luLiaoList.contains(material.getBrandName())) {
                                ExcelWriterUtil.addCellData(cellDataList, typeRowIndex +
                                        luLiaoList.size() - 8, typeColumnIndex, material.getBrandName());
                                luLiaoList.add(material.getBrandName());
                            }
                        }
                    }
                }
                for (int i = 0; i < luLiaoList.size(); i++) {
                    String brandName = luLiaoList.get(i);
                    for(int j = 0; j < chargeVarInfos.size() && j < 12; j++) {
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
                                // TODO 需要累加brandName相同的数据
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
            handleLiaoXianBianGeng(sheet, version);
        } catch (Exception e) {
            log.error("处理正面-变料信息出错", e);
        }
    }

    /**
     * 档位角度变更
     * @param sheet
     * @param cellDataList
     * @param chargeVarInfos
     */
    private void handleDangWeiJiaoDu (Sheet sheet, List<ChargeVarInfo> chargeVarInfos) {
        List<CellData> cellDataList = new ArrayList<>();
        chargeVarInfos = chargeVarInfos.stream().filter(item -> Objects.nonNull(item.getChargeVarDistribution()) && CollectionUtils.isNotEmpty(item.getChargeVarDistribution())).collect(Collectors.toList());
        //{挡位.角度}
        Cell cell = PoiCustomUtil.getCellByValue(sheet, "{挡位.角度}");
        if (Objects.isNull(cell)) {
            return;
        }
        int beginRowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        for (int i = 0; i < chargeVarInfos.size() && i < 4; i ++) {
            ChargeVarInfo info = chargeVarInfos.get(i);
            // 开始批数
            Integer index = info.getChargeVarIndex().getChargeNo();
            List<ChargeVarDistribution> chargeVarDistributionList = info.getChargeVarDistribution();
            boolean isFirstFiled = false;
            boolean isSecondFiled = false;
            boolean isThirdFiled = false;
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

    /**
     * 整合料线变更数据
     * @param version
     * @param key
     * @param liaoXianMap
     * @param tagValue
     * @param tag
     */
    private void handleStartIndex(String version, String key, Map<Integer, Map<String, BigDecimal>> liaoXianMap, BigDecimal tagValue, String tag) {
        String url = httpProperties.getGlUrlVersion(version) + "/tagValue/latest";
        Map<String, String> param = new HashMap<>();
        param.put("time", String.valueOf(Long.valueOf(key)));
        param.put("tagname", "BF8_L2C_SH_CurrentBatch_evt");
        String result = httpUtil.get(url, param);
        if (StringUtils.isNotBlank(result)) {
            JSONObject startIndex = JSON.parseObject(result);
            if (Objects.nonNull(startIndex)) {
                startIndex = startIndex.getJSONObject("data");
                if (Objects.nonNull(startIndex)) {
                    Integer val = startIndex.getInteger("val");
                    if (val != null) {
                        if (liaoXianMap.containsKey(val)) {
                            liaoXianMap.get(val).put(tag, tagValue);
                        } else {
                            liaoXianMap.put(val, new HashMap<String, BigDecimal>(){{ put("C", tagValue); }});
                        }
                    }
                }
            }
        }
    }

    /**
     * 处理料线变更数据
     * @param version
     */
    private void handleLiaoXianBianGeng (Sheet sheet, String version) {
        //{挡位.角度}
        Cell cell = PoiCustomUtil.getCellByValue(sheet, "{开始批次}");
        if (Objects.isNull(cell)) {
            return;
        }
        int beginRowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();

        List<CellData> cellDataList = new ArrayList<>();
        String queryUrl = getUrlTagNamesInRange(version);
        DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(new Date());
        JSONObject query = new JSONObject();
        query.put("starttime", String.valueOf(dateQuery.getQueryStartTime()));
        query.put("endtime", String.valueOf(dateQuery.getQueryEndTime()));
        String[] tagNames = new String[]{"BF8_L2C_TP_CokeSetLine_evt", "BF8_L2C_TP_SinterSetLine_evt", "BF8_L2C_TP_LiSinterSetLine_evt"};
        query.put("tagnames", tagNames);
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(queryUrl, jsonString);
        Map<Integer, Map<String, BigDecimal>> liaoXianMap = new HashMap<Integer, Map<String, BigDecimal>>();
        if (StringUtils.isNotBlank(results)) {
            JSONObject jsonObject = JSON.parseObject(results);
            if (Objects.nonNull(jsonObject)) {
                jsonObject = jsonObject.getJSONObject("data");
                if (Objects.nonNull(jsonObject)) {
                    int index = 0;
                    for (String tagName:tagNames) {
                        JSONObject jsonDatas = jsonObject.getJSONObject(tagName);
                        if (Objects.nonNull(jsonDatas) && jsonDatas.size() > 0) {
                            Set<String> keySet = jsonDatas.keySet();
                            for (String key:keySet) {
                                BigDecimal tagValue = jsonDatas.getBigDecimal(key);
                                switch (tagName) {
                                    case "BF8_L2C_TP_CokeSetLine_evt":
                                        handleStartIndex(version, key, liaoXianMap, tagValue, "C");
                                        break;
                                    case "BF8_L2C_TP_SinterSetLine_evt":
                                        handleStartIndex(version, key, liaoXianMap, tagValue, "OI");
                                        break;
                                    case "BF8_L2C_TP_LiSinterSetLine_evt":
                                        handleStartIndex(version, key, liaoXianMap, tagValue, "Os");
                                        break;
                                }
                                index ++;
                            }
                        }
                    }
                }

            }
        }
        int index = 0;
        for(Map.Entry<Integer, Map<String, BigDecimal>> entry : liaoXianMap.entrySet()) {
            Integer mapKey = entry.getKey();
            Map<String, BigDecimal> mapValue = entry.getValue();
            ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex, mapKey);
            for(String key : mapValue.keySet()){
                BigDecimal val = mapValue.get(key);
                switch (key) {
                    case "C":
                        ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 3, val);
                        break;
                    case "OI":
                        ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 5, val);
                        break;
                    case "Os":
                        ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 7, val);
                        break;
                }
            }
            index++;
        }
        ExcelWriterUtil.setCellValue(sheet, cellDataList);
    }

    //结束--------------------正面-变料信息--------------------

    /**
     * 通过tag点拿数据的API，根据sequence和version返回不同工序的api地址
     *
     * @param sequence
     * @param version
     * @return
     */
    protected String getUrlTagNamesInRange(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    protected void handleFacadeXiaoShiCanShu(Workbook workbook, String version) {
        try {
            log.debug("处理 正面 - 小时参数 部分");
        } catch (Exception e) {
            log.error("处理 正面 - 小时参数 部分产生错误", e);
        }
    }

    /**
     * 获取数据
     * @param sheet
     * @param version
     * @param queryParam
     * @return
     */
    private BigDecimal getLatestByCategoryAndItem (Sheet sheet, String version, Map<String, String> queryParam) {
        String url = httpProperties.getGlUrlVersion(version) + "/getLatestByCategoryAndItem";
        String data = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSONObject.parseObject(data);
        BigDecimal result = jsonObject.getBigDecimal("data");
        return result;
    }

    /**
     * 反面-第一行数据
     * @param excelDTO
     * @param workbook
     * @param version
     */
    protected void handleFirstRowData(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        // 需要写入的单元格数据对象
        List<CellData> resultList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            //入炉铁分
            BigDecimal ruLuTieFen = getLatestByCategoryAndItem(sheet, version, new HashMap(){{
                put("category", "IRON");
                put("anaitemname", "TFe");
                put("granularity", "day");
            }});
            // 批铁量
            BigDecimal piTieLiang = getLatestByCategoryAndItem(sheet, version, new HashMap(){{
                put("category", "IRON");
                put("anaitemname", "weightset");
                put("granularity", "day");
            }});
            // 焦比
            BigDecimal jiaoBi = getLatestByCategoryAndItem(sheet, version, new HashMap(){{
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
                    ExcelWriterUtil.addCellData(resultList, beginRow, beginColumn, ruLuTieFen);
                }
            }

            //找到待填充的坐标
            Cell piTieLiangCell = PoiCustomUtil.getCellByValue(sheet, "{批铁量}");
            if (Objects.nonNull(piTieLiangCell)) {
                // 待填充的行,列
                int beginRow = piTieLiangCell.getRowIndex();
                int beginColumn = piTieLiangCell.getColumnIndex();
                if (Objects.nonNull(piTieLiang) && Objects.nonNull(ruLuTieFen)) {
//            ExcelWriterUtil.addCellData(resultList, tapBeginRow, tapBeginColumn, result.setScale(3,
//                    BigDecimal.ROUND_HALF_UP).multiply(ruLuTieFen.setScale(3, BigDecimal.ROUND_HALF_UP)));
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
                    ExcelWriterUtil.addCellData(resultList, beginRow, beginColumn, jiaoBi.divide(piTieLiang.multiply(ruLuTieFen), 4, BigDecimal.ROUND_HALF_UP));
                }
            }

            ExcelWriterUtil.setCellValue(sheet, resultList);
        } catch (Exception e) {
            log.error("处理反面-第一行数据出错", e);
        }
    }

    // 反面-出铁
    protected void handleTapData(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        // 需要写入的单元格数据对象
        List<CellData> resultList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQuery(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
            // 处理出铁数据
            List<String> tapNoList = handleTapData(sheet, 6, dateQuery, resultList, version, null);
            tapNoList = handleTapData(sheet, 26, dateQuery, resultList, version, tapNoList);
            // 处理罐号重量数据
            if (CollectionUtils.isNotEmpty(tapNoList)) {
                handleTpcInfoData(sheet, resultList, tapNoList, version);
            }

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
    private List<String> handleTapData(Sheet sheet, int itemRowNum, DateQuery dateQuery, List<CellData> resultList, String version, List<String> tapNoList) {
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
                String url = httpProperties.getGlUrlVersion(version) + "/tagValue/latest";
                Map<String, String> param = new HashMap<>();
                param.put("time", String.valueOf(stringObjectMap.get("endTime")));
                param.put("tagname", "BF8_L2M_SH_ChargeCount_evt");
                String result = httpUtil.get(url, param);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject object = JSON.parseObject(result);
                    if (Objects.nonNull(object)) {
                        object = object.getJSONObject("data");
                        if (Objects.nonNull(object)) {
                            int val = object.getIntValue("val");
                            ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, 1, val);
                        }
                    }
                }
                //批料数
                String queryUrl = getUrlTagNamesInRange(version);
                JSONObject query = new JSONObject();
                query.put("starttime", String.valueOf(stringObjectMap.get("startTime")));
                query.put("endtime", String.valueOf(stringObjectMap.get("endTime")));
                query.put("tagnames", new String[]{"BF8_L2M_SH_ChargeCount_evt"});
                SerializeConfig serializeConfig = new SerializeConfig();
                String jsonString = JSONObject.toJSONString(query, serializeConfig);
                String results = httpUtil.postJsonParams(queryUrl, jsonString);
                if (StringUtils.isNotBlank(results)) {
                    JSONObject tagObject = JSONObject.parseObject(results);
                    if (Objects.nonNull(tagObject)) {
                        tagObject = tagObject.getJSONObject("data");
                        if (Objects.nonNull(tagObject)) {
                            tagObject = tagObject.getJSONObject("BF8_L2M_SH_ChargeCount_evt");
                            if (Objects.nonNull(tagObject)) {
                                Map<String, Object> innerMap = tagObject.getInnerMap();
                                if (Objects.nonNull(innerMap)) {
                                    ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, 9, innerMap.size());
                                }
                            }
                        }
                    }

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
                        ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, j, value.toString());
                    } else if (itemArray.length == 3 && "item".equals(itemArray[0])) {
                        switch (itemArray[1]) {
                            case "tapValues":
                                if (Objects.isNull(tapValues) || tapValues.size() == 0) {
                                    continue;
                                }
                                ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, j, tapValues.get(itemArray[2]));
                                break;
                            case "slagAnalysis":
                                if (Objects.isNull(slagAnalysis) || slagAnalysis.size() == 0) {
                                    continue;
                                }
                                Double val = slagAnalysis.get(itemArray[2]);
                                //R2不乘100
                                if (!itemArray[2].equals("B2")) {
                                    val = slagAnalysis.get(itemArray[2]) * 100;
                                }
                                ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, j, val);
                                break;
                            case "hmAnalysis":
                                if (Objects.isNull(hmAnalysis) || hmAnalysis.size() == 0) {
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
    protected void handOreBlockData(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        String brandCodeType = "LUMPORE";
        String oreBlockType = "LC";
        // 待填充的数据
        String[] arr = {"TFe", "Al2O3", "CaO", "MgO", "SiO2", "S" ,"P"};
        List<CellData> cellDataList = new ArrayList<>();
        // 反面sheet
        Sheet sheet = null;
        try {
            sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQuery(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());

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
            // 2.遍历brandCodeList取出所有数据
            for (int i = 0; i < brandCodeList.size(); i++) {
                // 3. 根据brandCode和 type调接口获取数据
                String url = getAnalysisValueUrl(version) + "/" + endTime + "?type=" + oreBlockType + "&brandcode=" + brandCodeList.get(i);;

                setAnalysisValue2Cell(sheet, cellDataList, arr, url,
                        tpcNoBeginRow + i, tpcNoBeginColumn);
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
                                             int tpcNoBeginRow, int tpcNoBeginColumn) {
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
                // 填充具体数据
                ExcelWriterUtil.addCellData(cellDataList, writeRow, writeColumn, value);
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
                                                  String placeHolder, Map<String,String> map) {
        // 找到待填充的坐标
        Cell tpcNoCell = PoiCustomUtil.getCellByValue(sheet, placeHolder);
        if (Objects.isNull(tpcNoCell)) {
            return;
        }
        // 待填充的行,列
        int tpcNoBeginRow = tpcNoCell.getRowIndex();
        int tpcNoBeginColumn = tpcNoCell.getColumnIndex();
        // 检查表头列是否存在，如果不存在则不填充数据
        //checkHeaders(sheet, arr, map, tpcNoBeginRow-1);

        setAnalysisValue2Cell(sheet, cellDataList, arr, url, tpcNoBeginRow, tpcNoBeginColumn);
    }

    /**
     * 处理AnalysisValue相关的数据，包含成分分析(焦炭和煤粉)和热力强度
     * @param excelDTO
     * @param workbook
     * @param version
     */
    protected void handAnalysisValue(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        String heatType = "LG";
        String cokeType = "LC";
        String cokeBrandCode = "COKE";
        String coalBrandCode = "COAL";
        String heatPlaceHolder = "{热强度}";
        String cokePlaceHolderYe = "{焦炭.夜}";
        String cokePlaceHolderBai = "{焦炭.白}";
        String coalPlaceHolder = "{煤粉}";
        String[] heatArr = {"M40", "M10", "CSR", "CRI"};
        String[] cokeArr = {"H2O", "Ad", "Vdaf", "S"};
        // 根据需求，取回的煤粉数据中C取Fcad，S取Std
        String[] coalArr = {"Ad", "Vdaf", "Std", "Fcad", "S"};
        Map<String,String> map = new HashMap<String, String>(){{
            put("Fcad","C");
            put("Std","S");
        }};
        List<CellData> cellDataList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQuery(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
            //analysisValues/rangeByType?from=1577931200000&to=1578017600000&materialType=COAL
            String from = Objects.requireNonNull(dateQuery.getStartTime().getTime()).toString();
            String to = Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString();

            // 1. 获取AnalysisValue Url前缀
            String url = httpProperties.getGlUrlVersion(version) + "/analysisValues/rangeByType?from=" + from + "&to=" + to + "&materialType=";
            String heatUrl = getAnalysisValueUrl(version) + "/" + to + "?type=LG&brandcode=KM-L_COKE";
            //先处理热力强度，因为只有一行数据
            //再处理成分分析，分为煤粉和焦炭
            // 2. 获取热强度数据，填充数据
            handAnalysisValue2Cell(sheet, cellDataList, heatUrl, heatArr, heatPlaceHolder, new HashMap<>());
            // 3. 获取焦炭数据，填充数据
            // 获取两班倒查询策略
            Date dateRun = this.getDateQuery(excelDTO).getRecordDate();
            List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourEach(dateRun);
            for (int k = 0; k < dateQueries.size(); k++) {
                DateQuery dateQuery1 = dateQueries.get(k);
                //夜班
                if (dateQuery1.getRecordDate().compareTo(dateQuery.getStartTime()) == 0) {
                    handJiaoTan(sheet, httpProperties.getGlUrlVersion(version) + "/analysisValues/rangeByType", cellDataList, dateQuery1, cokeArr, cokePlaceHolderYe);
                } else {
                    handJiaoTan(sheet, httpProperties.getGlUrlVersion(version) + "/analysisValues/rangeByType", cellDataList, dateQuery1, cokeArr, cokePlaceHolderBai);
                }
            }
            // 4. 获取煤粉数据，填充数据

            handAnalysisValue2Cell(sheet, cellDataList, url + coalBrandCode, coalArr, coalPlaceHolder, map);

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
    private void handJiaoTan(Sheet sheet, String url, List<CellData> cellDataList, DateQuery dateQuery, String[] arr, String placeHolder) {
        Map<String, String> queryParam = new HashMap();
        Date date = new Date();
        queryParam.put("to", Objects.requireNonNull(dateQuery.getQueryEndTime()).toString());
        queryParam.put("from", Objects.requireNonNull(dateQuery.getQueryStartTime()).toString());

        queryParam.put("materialType", "COKE");
        String jsonData = httpUtil.get(url, queryParam);
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
        int dataSize = arr.length;
        // 找到待填充的坐标
        Cell tpcNoCell = PoiCustomUtil.getCellByValue(sheet, placeHolder);
        if (Objects.isNull(tpcNoCell)) {
            return;
        }
        // 待填充的行,列
        int tpcNoBeginRow = tpcNoCell.getRowIndex();
        int tpcNoBeginColumn = tpcNoCell.getColumnIndex();
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
                // 填充具体数据
                ExcelWriterUtil.addCellData(cellDataList, writeRow, writeColumn, value);
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
     * 处理烧结矿理化数据，时间和编号以LC为主显示，LP没有数据则空着
     * @param sheet
     * @param brandCode
     * @param pageNum
     * @param result
     * @param cellDataList
     */
    private void handAnalysisValuesData(Sheet sheet, String result, List<CellData> cellDataList) {
        String placeHolder = "{矿种}";
        String[] arr = {"TFe", "FeO", "CaO", "MgO", "SiO2", "S", "B2", "Drum", "S+40",
                "S25-40", "S16-25", "S10-16", "S5-10", "S-5", "S-10", "Abrasion"};
        String[] lcArr = {"TFe", "FeO", "CaO", "MgO", "SiO2", "S"};
        List< String> lcList = new ArrayList<String>(lcArr.length);
        Collections.addAll(lcList, lcArr);

        // TODO 矿种 槽号 DI 抗磨
        // 矿种坐标
        Cell cell = PoiCustomUtil.getCellByValue(sheet, placeHolder);
        if (Objects.isNull(cell)) {
            return;
        }
        // 待填充的行,列
        int beginRow = cell.getRowIndex();
        int coalBeginColumn = 1;

        // 根据json映射对象DTO
        AnalysisValueDTO analysisValueDTO = null;
        if (StringUtils.isNotBlank(result)) {
            analysisValueDTO = JSON.parseObject(result, AnalysisValueDTO.class);
        }
        if (Objects.isNull(analysisValueDTO)) {
            return;
        }
        List<AnalysisValue> list = analysisValueDTO.getData();
        if (Objects.isNull(list) || CollectionUtils.isEmpty(list)) {
            return;
        }

        //目前只能显示12行数据
        for (int i = 0; i< list.size() && i < 12; i++) {
            AnalysisValue analysisValue = list.get(i);
            if (Objects.isNull(analysisValue)) continue;
            Analysis analysis = analysisValue.getAnalysis();
            final String sampleid = analysis != null ? analysis.getSampleid() : null;
            String type = analysis != null ? analysis.getType() : null;
            // 处理矿种、记录时间、编号、槽号
            if (!Objects.isNull(analysis)) {
                //TODO 填充矿种数据
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
                // TODO 槽号
            }
            Map<String, BigDecimal> values = analysisValue.getValues();
            if (Objects.isNull(values)) continue;
            for (int j = 0; j < arr.length; j++){
                //16-25 = 100 -其他
                // 获取具体AnalysisValue数据
                //<10  是 <5 + 5-10
                int writeColumn = coalBeginColumn + 4 + j;
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

    /**
     * 处理烧结矿理化分析数据
     * @param excelDTO
     * @param workbook
     * @param version
     */
    protected void handAnalysisValues(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        List<CellData> cellDataList = new ArrayList<>();
        try {
            // 反面sheet
            ///analysisValues/rangeByCode?from=1589904000000&to=1590163200000&brandCode=S4_SINTER
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQuery(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
            // 1. 获取AnalysisValues Url前缀
            String url = getAnalysisValuesUrl(version);
            Map<String, String> queryParam = new HashMap();
            queryParam.put("from", Objects.requireNonNull(dateQuery.getQueryStartTime()).toString());
            queryParam.put("to", Objects.requireNonNull(dateQuery.getQueryEndTime()).toString());
            queryParam.put("brandCode", "S4_SINTER");
            //queryParam.put("from", "1591200000000");
            //queryParam.put("to", "1591286400000");
            String result = httpUtil.get(url, queryParam);
            if (StringUtils.isBlank(result)) {
                queryParam.put("brandCode", "S1_SINTER");
                result = httpUtil.get(url, queryParam);
            } else {
                JSONObject jsonObject = JSON.parseObject(result);
                if (Objects.nonNull(jsonObject)) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.size() == 0) {
                        queryParam.put("brandCode", "S1_SINTER");
                        result = httpUtil.get(url, queryParam);
                    }
                } else {
                    queryParam.put("brandCode", "S1_SINTER");
                    result = httpUtil.get(url, queryParam);
                }
            }
            handAnalysisValuesData(sheet, result, cellDataList);
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理反面-烧结矿理化分析出错", e);
        }
    }

    /**
     * 查询BrandCode,可能有多个，最多三个
     * @param version
     * @param startTime
     * @param endTime
     * @param type
     * @return 包含BrandCode的JSONArray
     */
    private List<String> getBrandCodeData(String version, DateQuery dateQuery, String type) {
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
     * 获取高炉的种类Url
     * @param version
     * @return Url的string
     */
    private String getBrandCodes(String version){
        return httpProperties.getGlUrlVersion(version) + "/brandCodes/getBrandCodes";
    }

    /**
     * 高炉API
     * @param version
     * @return
     */
    private String getAnalysisValueUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/analysisValue/clock";
    }

    /**
     * 烧结矿理化分析API
     * @param version
     * @return
     */
    private String getAnalysisValuesUrl(String version) {
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
}
