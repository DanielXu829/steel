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
    protected void handleFacadeXiaoShiCanShu(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        try {
            String queryUrl = getUrlTagNamesInRange(version);

            // 动态报表生成的模板默认取第二个sheet。
            String sheetName = "_FacadeXiaoShiCanShu_day_hour";
            Sheet sheet = workbook.getSheet(sheetName);
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
//                DateQuery dateQuery = this.build24HoursFromTwentyTwo(excelDTO);
//                List<DateQuery> dateQueries = DateQueryUtil.buildDayHourOneEach(dateQuery.getStartTime(), dateQuery.getEndTime());
                DateQuery dateQuery = this.getDateQueryBeforeOneDay(excelDTO);
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, dateQuery.getRecordDate());
                // evt为后缀的值需要此逻辑，防止取到前一天累计的值。
//                DateQuery firstDateQuery = dateQueries.get(0);
//                firstDateQuery.setStartTime(DateUtil.getDateBeginTime(firstDateQuery.getEndTime()));
//                dateQueries.set(0, firstDateQuery);

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
            String url = getQueryBlastStatus(version);
            DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
            Date dateRun = date.getRecordDate();
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
            BigDecimal value = this.getBlastIntakeArea(version, String.valueOf(dateQuery.getQueryEndTime()));
            ExcelWriterUtil.addCellData(cellData, 1, 0, value);
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
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(DateUtil.addDays(new Date(), -1));
            List<ChargeVarInfo> chargeVarInfos = getChargeVarInfo(version, dateQuery, "D");
            handleDangWeiJiaoDu (sheet, getChargeVarInfo(version, dateQuery, "M"));
            List<CellData> cellDataList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(chargeVarInfos)) {
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
                                        luLiaoList.size() - 8, typeColumnIndex, material.getBrandName()+"t");
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
            handleLiaoXianBianGeng(sheet, version);
            handJingJiaoJiLu(sheet, version, dateQuery);
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
    private void handJingJiaoJiLu (Sheet sheet, String version, DateQuery dateQuery) {
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
        query.put("tagnames", new String[]{"BF8_L2M_NetCokeTime_evt"});
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String results = httpUtil.postJsonParams(queryUrl, jsonString);
        if (StringUtils.isNotBlank(results)) {
            JSONObject object = JSONObject.parseObject(results);
            if (Objects.nonNull(object)) {
                JSONObject data = object.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    JSONObject tag = data.getJSONObject("BF8_L2M_NetCokeTime_evt");
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
                            param.put("tagname", "BF8_L2M_CokeBatchWeight_evt");
                            String result = httpUtil.get(url, param);
                            if (StringUtils.isNotBlank(result)) {
                                JSONObject weight = JSON.parseObject(result);
                                if (Objects.nonNull(weight)) {
                                    weight = weight.getJSONObject("data");
                                    if (Objects.nonNull(weight)) {
                                        BigDecimal val = weight.getBigDecimal("val");
                                        if (val != null) {
                                            //重量
                                            ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + k, columnIndex + 2, val);
                                        }
                                    }
                                }
                            }
                            k++;
                        }
                    }
                }
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

    /**
     * 整合料线变更数据
     * @param version
     * @param key
     * @param liaoXianMap
     * @param tagValue
     * @param tag
     */
    private void handleStartIndex(String version, String key, Map<Integer, Map<String, BigDecimal>> liaoXianMap, BigDecimal tagValue, String tag) {
        String url = getLatestTagValueUrl(version);
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
        DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(DateUtil.addDays(new Date(), -1));
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
                if (key.equals("C")) {
                    ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 3, val);
                } else if (key.equals("基准")) {
                    ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 7, val);
                } else {
                    ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 5,
                            mapValue.get("OI")+"/"+mapValue.get("Os"));
                }
//                switch (key) {
//                    case "C":
//                        ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 3, val);
//                        break;
//                    case "OI":
//                    case "Os":
//                        ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 5,
//                                mapValue.get("OI")+"/"+mapValue.get("Os"));
//                        break;
//                    case "基准":
//                        ExcelWriterUtil.addCellData(cellDataList, beginRowIndex + index, columnIndex + 7, val);
//                        break;
//                }
            }
            index++;
        }
        ExcelWriterUtil.setCellValue(sheet, cellDataList);
//        if (liaoXianMap.size() > 23) {
//            //设置动态边框样式
//            int beginRowNum = beginRowIndex;
//            int lastRowNum = beginRowIndex + (liaoXianMap.size()-23);
//            int beginColumnNum = columnIndex;
//            int endColumnNum = columnIndex + 8;
//            ExcelWriterUtil.setBorderStyle(sheet.getWorkbook(), sheet,  beginRowNum, lastRowNum, beginColumnNum, endColumnNum);
//        }
    }

    //结束--------------------正面-变料信息--------------------

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
    protected void handleTapData(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        // 需要写入的单元格数据对象
        List<CellData> resultList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
            // 处理出铁数据
            List<String> tapNoList = handleTapData(sheet, 6, dateQuery, resultList, version, null);
            // 处理罐号重量数据
            if (CollectionUtils.isNotEmpty(tapNoList)) {
                handleTpcInfoData(sheet, resultList, tapNoList, version);
            }
            tapNoList = handleTapData(sheet, 26, dateQuery, resultList, version, tapNoList);

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
                String url = getLatestTagValueUrl(version);
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
                                if (!itemArray[2].equals("B2")) {
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
            DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
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
                String url = getAnalysisValueUrl(version, endTime, oreBlockType, brandCodeList.get(i));
                setAnalysisValue2Cell(sheet, cellDataList, arr, url,
                        tpcNoBeginRow + i, tpcNoBeginColumn, true);
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
        List<CellData> cellDataList = new ArrayList<>();
        try {
            // 反面sheet
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
            String from = Objects.requireNonNull(dateQuery.getStartTime().getTime()).toString();
            String to = Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString();

            // 1. 获取AnalysisValue Url前缀
            String url = getRangeByTypeUrl(version, from, to, coalBrandCode);
            String heatUrl = getAnalysisValueUrl(version, to, "LG", "KM-L_COKE");
            //先处理热力强度，因为只有一行数据
            //再处理成分分析，分为煤粉和焦炭
            // 2. 获取热强度数据，填充数据
            handAnalysisValue2Cell(sheet, cellDataList, heatUrl, heatArr, heatPlaceHolder, false);
            // 3. 获取焦炭数据，填充数据
            // 获取两班倒查询策略
            Date dateRun = this.getDateQueryBeforeOneDay(excelDTO).getRecordDate();
            List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourEach(dateRun);
            for (int k = 0; k < dateQueries.size(); k++) {
                DateQuery dateQuery1 = dateQueries.get(k);
                String queryUrl = getRangeByTypeUrl(version, Objects.requireNonNull(dateQuery1.getQueryStartTime()).toString(),
                        Objects.requireNonNull(dateQuery1.getQueryEndTime()).toString(), "COKE");
                //夜班
                if (dateQuery1.getRecordDate().compareTo(dateQuery.getStartTime()) == 0) {
                    handJiaoTan(sheet, queryUrl, cellDataList, cokeArr, cokePlaceHolderYe);
                } else {
                    handJiaoTan(sheet, queryUrl, cellDataList, cokeArr, cokePlaceHolderBai);
                }
            }
            // 4. 获取煤粉数据，填充数据

            handAnalysisValue2Cell(sheet, cellDataList, url + coalBrandCode, coalArr, coalPlaceHolder, true);

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
    private void handJiaoTan(Sheet sheet, String url, List<CellData> cellDataList, String[] arr, String placeHolder) {
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
                if (value != null) {
                    ExcelWriterUtil.addCellData(cellDataList, writeRow, writeColumn, value.multiply(new BigDecimal(100)));
                } else {
                    // 填充具体数据
                    ExcelWriterUtil.addCellData(cellDataList, writeRow, writeColumn, value);
                }

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
     * 处理烧结矿理化数据
     * @param sheet
     * @param brandCode
     * @param pageNum
     * @param result
     * @param cellDataList
     */
    private void handAnalysisValuesData(Sheet sheet, String result, List<CellData> cellDataList, String oreType,
                                        String placeHolder, String[] arr, String[] handleArr) {

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

        for (int i = 0; i< list.size() && i < 12; i++) {
            AnalysisValue analysisValue = list.get(i);
            if (Objects.isNull(analysisValue)) continue;
            Analysis analysis = analysisValue.getAnalysis();
            final String sampleid = analysis != null ? analysis.getSampleid() : null;
            String type = analysis != null ? analysis.getType() : null;
            // 处理矿种、记录时间、编号、槽号
            if (!Objects.isNull(analysis)) {
                //矿种
                ExcelWriterUtil.addCellData(cellDataList, beginRow + i, coalBeginColumn, oreType);
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
            Sheet sheet = workbook.getSheetAt(1);
            DateQuery date = this.getDateQueryBeforeOneDay(excelDTO);
            DateQuery dateQuery = DateQueryUtil.buildTodayNoDelay(date.getRecordDate());
            // 1. 获取AnalysisValues Url前缀
            String url = getAnalysisValuesUrl(version);
            Map<String, String> queryParam = new HashMap();
            queryParam.put("from", Objects.requireNonNull(dateQuery.getQueryStartTime()).toString());
            queryParam.put("to", Objects.requireNonNull(dateQuery.getQueryEndTime()).toString());
            queryParam.put("brandCode", "S4_SINTER");
            String result = httpUtil.get(url, queryParam);
            String kuangZhong = "S4";
            if (StringUtils.isBlank(result)) {
                queryParam.put("brandCode", "S1_SINTER");
                kuangZhong = "S1";
                result = httpUtil.get(url, queryParam);
            } else {
                JSONObject jsonObject = JSON.parseObject(result);
                if (Objects.nonNull(jsonObject)) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.size() == 0) {
                        queryParam.put("brandCode", "S1_SINTER");
                        kuangZhong = "S1";
                        result = httpUtil.get(url, queryParam);
                    }
                } else {
                    queryParam.put("brandCode", "S1_SINTER");
                    kuangZhong = "S1";
                    result = httpUtil.get(url, queryParam);
                }
            }
            String placeHolder = "{矿种}";
            String[] arr = {"TFe", "FeO", "CaO", "MgO", "SiO2", "S", "B2", "Zn","Drum", "S+40",
                    "S25-40", "S16-25", "S10-16", "S5-10", "S-5", "S-10", "SF"};
            String[] handleArr = {"TFe", "FeO", "CaO", "MgO", "SiO2", "S", "Zn"};
            handAnalysisValuesData(sheet, result, cellDataList, kuangZhong, placeHolder, arr, handleArr);

            String placeHolder2 = "{球团}";
            String[] arr2 = {"TFe", "FeO", "CaO", "MgO", "SiO2", "S", "B2", "Zn"};
            String[] handleArr2 = {"TFe", "FeO", "CaO", "MgO", "SiO2", "S", "Zn"};
            String queryUrl = getRangeByTypeUrl(version, Objects.requireNonNull(dateQuery.getQueryStartTime()).toString(),
                    Objects.requireNonNull(dateQuery.getQueryEndTime()).toString(), "PELLETS");
            String qiuTuanData = httpUtil.get(queryUrl);
            //球团名称
            handAnalysisValuesData(sheet, qiuTuanData, cellDataList, "", placeHolder2, arr2, handleArr2);
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



}
