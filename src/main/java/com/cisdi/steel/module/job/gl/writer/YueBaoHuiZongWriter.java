package com.cisdi.steel.module.job.gl.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.AnalysisValueDTO;
import com.cisdi.steel.dto.response.gl.MaterialExpendStcDTO;
import com.cisdi.steel.dto.response.gl.res.AnalysisValue;
import com.cisdi.steel.dto.response.gl.TagValueListDTO;
import com.cisdi.steel.dto.response.gl.res.*;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.FastJSONUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 8高炉月报汇总 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/06/24 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class YueBaoHuiZongWriter extends BaseGaoLuWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = "8.0";
        Date date = new Date();
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }

        try {
            DateQuery dateQuery = getDateQuery(excelDTO);
            List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(dateQuery.getRecordDate(), 1);
            //原燃料质量
            handleYuanRanLiaoZhiLiang(workbook, version, allDayBeginTimeInCurrentMonth);
            //技术经济指标及操作参数
            handleJiShuJingJiZhiBiao(workbook, version, allDayBeginTimeInCurrentMonth);
            //布料、风口及炉况情况
            handleBuLiaoFengKouLuKuang(workbook, version, allDayBeginTimeInCurrentMonth);
            //出渣铁及煤气成分
            handleChuZhaTieMeiQiChenFen(workbook, version, allDayBeginTimeInCurrentMonth);
            // 原燃料消耗
            handleYuanRanLiaoXiaoHao(workbook, version, allDayBeginTimeInCurrentMonth);
        } catch (Exception e) {
            log.error("处理 月报汇总 时产生错误", e);
            throw e;
        } finally {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (!Objects.isNull(sheet) && !workbook.isSheetHidden(i)) {
                    // 全局替换 当前日期
                    ExcelWriterUtil.replaceCurrentMonthInTitleWithSpace(sheet, 0, 0, date);
                    PoiCustomUtil.clearPlaceHolder(sheet);
                }
            }
        }
        return workbook;
    }

    //原燃料质量
    private void handleYuanRanLiaoZhiLiang(Workbook workbook, String version, List<Date> allDayBeginTimeInCurrentMonth) {
        try {
            List<CellData> cellDataList = new ArrayList<>();
            Sheet sheet = workbook.getSheet("原燃料质量");
            String url = getAnalysisValuesUrl(version);
            Map<String, String> queryParamSINTER = new HashMap();
            int itemRowNum = 5;
            int fixLineCount = 0;
            List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
            sheet.getRow(itemRowNum).setZeroHeight(true);
            for (int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i++) {
                if (i > 0 && i % 10 == 0) {
                    fixLineCount++;
                }
                int row = itemRowNum + 1 + fixLineCount + i;
                DateQuery eachDateQuery = DateQueryUtil.buildDayAheadTwoHour(allDayBeginTimeInCurrentMonth.get(i));
                String from = Objects.requireNonNull(eachDateQuery.getStartTime().getTime()).toString();
                String to = Objects.requireNonNull(eachDateQuery.getEndTime().getTime()).toString();
                queryParamSINTER.put("from", from);
                queryParamSINTER.put("to", to);
                queryParamSINTER.put("brandCode", "S4_SINTER");
                String sinterResult = httpUtil.get(url, queryParamSINTER);
                String pelletsResult = httpUtil.get(getRangeByTypeUrl(version, from, to, "PELLETS"));
                String lumporeResult = httpUtil.get(getRangeByTypeUrl(version, from, to, "LUMPORE"));
                String cokeResult = httpUtil.get(getRangeByTypeUrl(version, from, to, "COKE"));
                String pciResult = httpUtil.get(getRangeByTypeUrl(version, from, to, "PCI"));
                List<AnalysisValue> sinterValues = Optional.ofNullable(sinterResult)
                        .map(e -> JSON.parseObject(e, AnalysisValueDTO.class))
                        .map(AnalysisValueDTO::getData).orElse(null);
                List<AnalysisValue> pelletsValues = Optional.ofNullable(pelletsResult)
                        .map(e -> JSON.parseObject(e, AnalysisValueDTO.class))
                        .map(AnalysisValueDTO::getData).orElse(null);
                List<AnalysisValue> lumporeValues = Optional.ofNullable(lumporeResult)
                        .map(e -> JSON.parseObject(e, AnalysisValueDTO.class))
                        .map(AnalysisValueDTO::getData).orElse(null);
                List<AnalysisValue> cokeValues = Optional.ofNullable(cokeResult)
                        .map(e -> JSON.parseObject(e, AnalysisValueDTO.class))
                        .map(AnalysisValueDTO::getData).orElse(null);
                List<AnalysisValue> pciValues = Optional.ofNullable(pciResult)
                        .map(e -> JSON.parseObject(e, AnalysisValueDTO.class))
                        .map(AnalysisValueDTO::getData).orElse(null);
                Map<String, List<AnalysisValue>> itemPrefixToValues = new HashMap<String, List<AnalysisValue>>() {{
                    put("SJK", sinterValues);
                    put("QT", pelletsValues);
                    put("KK", lumporeValues);
                    put("JT", cokeValues);
                    put("PCM", pciValues);
                }};
                String [] itemsNoNeedToHandlePercent = {"S-5", "S5-10", "S25-40", "B2", "Drum", "SF"};
                for (int itemIndex = 2; itemIndex < itemNameList.size(); itemIndex++) {
                    String itemName = itemNameList.get(itemIndex);
                    if (StringUtils.isBlank(itemName)) {
                        continue;
                    }
                    String[] itemSplitArray = itemName.split("_");
                    if (itemSplitArray.length > 0) {
                        List<AnalysisValue> analysisValues = itemPrefixToValues.get(itemSplitArray[0]);
                        if (CollectionUtils.isEmpty(analysisValues)) {
                            continue;
                        }
                        String item = itemSplitArray[1];
                        BigDecimal sinterAverageValue = analysisValues.stream().map(AnalysisValue::getValues)
                                .map(e -> e.get(item)).filter(e -> e != null)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .divide(BigDecimal.valueOf(analysisValues.size()), 4, BigDecimal.ROUND_HALF_UP);
                        String unit = PoiCellUtil.getCellValue(sheet, itemRowNum - 1, itemIndex);
                        if ("%".equals(unit) && !Arrays.asList(itemsNoNeedToHandlePercent).contains(item)) {
                            sinterAverageValue = sinterAverageValue.multiply(new BigDecimal("100"));
                        }
                        ExcelWriterUtil.addCellData(cellDataList, row, itemIndex, sinterAverageValue);
                    }
                }
            }
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理 原燃料质量 出错", e);
        }
    }

    //原燃料消耗
    private void handleYuanRanLiaoXiaoHao(Workbook workbook, String version, List<Date> allDayBeginTimeInCurrentMonth) {
        try {
            List<CellData> cellDataList = new ArrayList<>();
            Sheet sheet = workbook.getSheet("原燃料消耗");
            int itemRowNum = 4;
            int fixLineCount = 0;
            List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
            sheet.getRow(itemRowNum).setZeroHeight(true);
            for (int i = 0, daySize = allDayBeginTimeInCurrentMonth.size(); i < daySize; i++) {
                if (i > 0 && i % 10 == 0) {
                    fixLineCount++;
                }
                int row = itemRowNum + 1 + fixLineCount + i;
                DateQuery eachDateQuery = DateQueryUtil.buildDayAheadTwoHour(allDayBeginTimeInCurrentMonth.get(i));
                MaterialExpendStcDTO materialExpandStcDTO = getMaterialExpandStcDTO(version, eachDateQuery.getEndTime());
                Map<String, List<MaterialExpend>> typeToMaterialExpendListMap =
                        Optional.ofNullable(materialExpandStcDTO).map(MaterialExpendStcDTO::getData).orElse(null);
                if (Objects.isNull(typeToMaterialExpendListMap) || typeToMaterialExpendListMap.isEmpty()) {
                    continue;
                }
                // 转换为key为matCode，value为dryWgt的map
                Map<String, BigDecimal> matCodeToDryWgtMap = typeToMaterialExpendListMap.entrySet().stream()
                        .map(Map.Entry::getValue).flatMap(Collection::stream)
                        .collect(Collectors.toMap(MaterialExpend::getMatCode, MaterialExpend::getDryWgt));
                for (int itemIndex = 0, itemSize = itemNameList.size(); itemIndex < itemSize; itemIndex++) {
                    String itemName = itemNameList.get(itemIndex);
                    if (StringUtils.isBlank(itemName)) {
                        continue;
                    }
                    BigDecimal dryWgt = matCodeToDryWgtMap.get(itemName);
                    if (Objects.nonNull(dryWgt)) {
                        ExcelWriterUtil.addCellData(cellDataList, row, itemIndex, dryWgt);
                    }
                }
            }
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理 原燃料消耗 出错", e);
        }
    }

    //布料、风口及炉况情况
    private void handleBuLiaoFengKouLuKuang(Workbook workbook, String version, List<Date> allDayBeginTimeInCurrentMonth) {
        try {
            int beginRow = 6;
            int fixLineCount = 0;
            Sheet sheet = workbook.getSheet("布料、风口及炉况情况");
            // 标记行
            int itemRowNum = 5;
            sheet.getRow(itemRowNum).setZeroHeight(true);
            // 获取excel占位符列
            List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
            List<CellData> cellDataList = new ArrayList<>();
            for (int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i++) {
                DateQuery eachDateQuery = DateQueryUtil.buildDayAheadTwoHour(allDayBeginTimeInCurrentMonth.get(i));
                Date day = allDayBeginTimeInCurrentMonth.get(i);
                BigDecimal defaultCellValue = new BigDecimal(0.0);

                // 获取风口信息
                BfBlastResult bfBlastResult = this.getBfBlastResult(version, day);
                Map<String, Object> bfBlastResultMap = new HashMap<String, Object>();
                if (Objects.nonNull(bfBlastResult)) {
                    bfBlastResultMap.put("qualityCount", bfBlastResult.getTuyereQualityCount());
                    bfBlastResultMap.put("tuyereBurnoutCount", bfBlastResult.getTuyereBurnoutCount());
                    bfBlastResultMap.put("tuyereAbrasionCount", bfBlastResult.getTuyereAbrasionCount());
                    bfBlastResultMap.put("tuyereOutboardCount", bfBlastResult.getTuyereOutboardCount());
                    bfBlastResultMap.put("tuyereAdjustCount", bfBlastResult.getTuyereAdjustCount());
                    bfBlastResultMap.put("tuyereBlockCount", bfBlastResult.getTuyereBlockCount());
                    bfBlastResultMap.put("blastArea", bfBlastResult.getBlastArea());
                    bfBlastResultMap.put("blastChangeCount", bfBlastResult.getBlastChangeCount());
                }

                // 获取布料矩阵
                DateQuery dateQueryNodelay = DateQueryUtil.buildTodayNoDelay(day);
                Map<String, List<BatchDistribution>> matrixDistrAvgInRangeMap = getMatrixDistrAvgInRangeMap(dateQueryNodelay, version);

                // 获取料线数据
                Map<String, String> liaoXianMaps = new HashMap<>();
                liaoXianMaps.put("料线-烧结矿", "BF8_L2C_TP_SinterSetLine_1d_avg");
                liaoXianMaps.put("料线-焦炭", "BF8_L2C_TP_CokeSetLine_1d_avg");
                liaoXianMaps.put("料线-小烧", "BF8_L2C_TP_LiSinterSetLine_1d_avg");
                liaoXianMaps.put("料线-主尺", "BF8_L2C_MainRuler_1d_avg");
                ArrayList<String> liaoXianTagNames = Lists.newArrayList(liaoXianMaps.values());
                Date liaoXianQueryTime = DateUtil.addDays(day, 1);
                TagValueListDTO liaoXianTagValueList = getLatestTagValueListDTO(liaoXianQueryTime, version, liaoXianTagNames);

                if (i > 0 && i % 10 == 0) {
                    fixLineCount++;
                }
                int row = itemRowNum + 1 + fixLineCount + i;
                // 循环列
                for (int j = 0; j < itemNameList.size(); j++) {
                    // 获取标记项单元格中的值
                    String itemName = itemNameList.get(j);
                    int col = j;
                    if (StringUtils.isNotBlank(itemName)) {
                        ExcelWriterUtil.addCellData(cellDataList, row, col, defaultCellValue);
                        switch (itemName) {
                            case "料线-烧结矿":
                            case "料线-焦炭":
                            case "料线-小烧":
                            case "料线-主尺": {
                                if(Objects.nonNull(liaoXianTagValueList) && CollectionUtils.isNotEmpty(liaoXianTagValueList.getData())) {
                                    Map<String, Object> collect = liaoXianTagValueList.getData().stream().collect(HashMap::new, (m,v)->
                                            m.put(v.getName(), v.getVal()),HashMap::putAll);
                                    Object orignalVal = collect.get(liaoXianMaps.get(itemName));
                                    if (Objects.nonNull(orignalVal)) {
                                        BigDecimal val = ((BigDecimal) orignalVal).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                                        ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                                    }
                                }
                                break;
                            }
                            case "C":
                            case "Ol":
                            case "Os": {
                                if(MapUtils.isNotEmpty(matrixDistrAvgInRangeMap) && CollectionUtils.isNotEmpty(matrixDistrAvgInRangeMap.get(itemName))) {
                                    List<BatchDistribution> batchDistributions = matrixDistrAvgInRangeMap.get(itemName);
                                    if (CollectionUtils.isNotEmpty(batchDistributions)) {
                                        String val = StringUtils.join(Arrays.asList(itemName.substring(0,1), getPosition(batchDistributions), getRoundset(batchDistributions)), "-");
                                        ExcelWriterUtil.addCellData(cellDataList, row, col, val);
                                    }
                                }
                                break;
                            }
                            case "qualityCount":
                            case "tuyereBurnoutCount":
                            case "tuyereAbrasionCount":
                            case "tuyereOutboardCount":
                            case "tuyereAdjustCount":
                            case "tuyereBlockCount":
                            case "blastArea":
                            case "blastChangeCount": {
                                if(MapUtils.isNotEmpty(bfBlastResultMap)) {
                                    Object valObj = bfBlastResultMap.get(itemName);
                                    if (Objects.nonNull(valObj)) {
                                        ExcelWriterUtil.addCellData(cellDataList, row, col, valObj);
                                    }
                                }
                                break;
                            }
                            case "N/A": {
                                ExcelWriterUtil.addCellData(cellDataList, row, col, defaultCellValue);
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    }
                }
            }
            ExcelWriterUtil.replaceCurrentMonthInTitleWithSpace(sheet, 0, 0, allDayBeginTimeInCurrentMonth.get(0));
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (Exception e) {
            log.error("处理 布料、风口及炉况情况 出错", e);
        }
    }

    //出渣铁及煤气成分
    private void handleChuZhaTieMeiQiChenFen(Workbook workbook, String version, List<Date> allDayBeginTimeInCurrentMonth) {
        List<CellData> resultList = new ArrayList<>();
        Sheet sheet = workbook.getSheet("出渣铁及煤气成分");
        handleTapData(sheet, version, allDayBeginTimeInCurrentMonth, resultList);
        ExcelWriterUtil.setCellValue(sheet, resultList);
    }

    //技术经济指标及操作参数
    private void handleJiShuJingJiZhiBiao(Workbook workbook, String version, List<Date> allDayBeginTimeInCurrentMonth) {
        try {
            int fixLineCount = 0;
            Sheet sheet = workbook.getSheet("技术经济指标及操作参数");
            Date date = allDayBeginTimeInCurrentMonth.get(0);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int currentMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            // 累计数据显示有点问题，隐藏第31天
            if (currentMonth < 31) {
                sheet.getRow(40).setZeroHeight(true);
            }
            ExcelWriterUtil.replaceDaysOfMonthInTitle(sheet, 0, 6, date);
            // 标记行
            int tagFormulaNum = 6;
            int itemRowNum = 7;
            sheet.getRow(tagFormulaNum).setZeroHeight(true);
            sheet.getRow(itemRowNum).setZeroHeight(true);
            // 获取excel占位符列
            List<String> tagFormulaList = PoiCustomUtil.getRowCelVal(sheet, tagFormulaNum);
            List<String> itemNameList = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
            Map<String, String> itemToTagFormulaMap = itemNameList.stream()
                    .collect(Collectors.toMap(key -> key,key -> tagFormulaList.get(itemNameList.indexOf(key)), (value1, value2) -> value1));
            List<CellData> cellDataList = new ArrayList<>();
            for (int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i++) {
                DateQuery eachDateQuery = DateQueryUtil.buildDayAheadTwoHour(allDayBeginTimeInCurrentMonth.get(i));
                Date day = allDayBeginTimeInCurrentMonth.get(i);
                BigDecimal defaultCellValue = new BigDecimal(0.0);
                JSONObject queryJsonObject = new JSONObject();
                Map<String, String> queryParam = eachDateQuery.getQueryParam();
                String starttime = queryParam.get("starttime");
                queryJsonObject.put("starttime", starttime);
                String endtime = queryParam.get("endtime");
                queryJsonObject.put("endtime", endtime);
                queryJsonObject.put("tagnames", tagFormulaList);
                String jsonData = httpUtil.postJsonParams(getUrlTagNamesInRange(version), queryJsonObject.toJSONString());
                if (StringUtils.isBlank(jsonData)) {
                    continue;
                }
                JSONObject jsonObject = JSONObject.parseObject(jsonData).getJSONObject("data");
                if (Objects.isNull(jsonObject)) {
                    continue;
                }
                Map<String, Double> itemToValueMap = new HashMap<>();
                for (Map.Entry<String, String> itemToTagFormulaEntry : itemToTagFormulaMap.entrySet()) {
                    JSONObject timeValueJsonObject = jsonObject.getJSONObject(itemToTagFormulaEntry.getValue());
                    if (Objects.isNull(timeValueJsonObject)) {
                        continue;
                    }
                    List<Object> valueList = new ArrayList<>(timeValueJsonObject.values());
                    if (CollectionUtils.isNotEmpty(valueList)) {
                        itemToValueMap.put(itemToTagFormulaEntry.getKey(), new Double(valueList.get(0).toString()));
                    }
                }
                if (i > 0 && i % 10 == 0) {
                    fixLineCount++;
                }
                int row = itemRowNum + 1 + fixLineCount + i;
                // 循环列
                for (int j = 1; j < itemNameList.size(); j++) {
                    String itemName = itemNameList.get(j);
                    int col = j;
                    if (StringUtils.isNotBlank(itemName)) {
                        ExcelWriterUtil.addCellData(cellDataList, row, col, itemToValueMap.get(itemName));
                    }
                }
            }
            ExcelWriterUtil.replaceCurrentMonthInTitleWithSpace(sheet, 1, 0, allDayBeginTimeInCurrentMonth.get(0));
            ExcelWriterUtil.setCellValue(sheet, cellDataList);
        } catch (NumberFormatException e) {
            log.error("处理 技术经济指标及操作参数 出错", e);
        }
    }

    /**
     * 出渣铁及煤气成分数据
     * @param sheet
     * @param dateQuery
     * @param resultList
     * @param version
     * @return tapNoList
     */
    private void handleTapData(Sheet sheet, String version, List<Date> allDayBeginTimeInCurrentMonth, List<CellData> cellDataList) {
        try {
            int beginRow = 6;
            int fixLineCount = 0;
            // 标记行
            int tagFormulaNum = 5;
            sheet.getRow(tagFormulaNum).setZeroHeight(true);
            // 获取excel占位符列
            List<String> tagFormulaList = PoiCustomUtil.getRowCelVal(sheet, tagFormulaNum);
            if (CollectionUtils.isEmpty(tagFormulaList)) {
                return;
            }
            int itemDataSize = tagFormulaList.size();
            for (int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i++) {
                DateQuery eachDateQuery = DateQueryUtil.buildDayAheadTwoHour(allDayBeginTimeInCurrentMonth.get(i));
                if (i > 0 && i % 10 == 0) {
                    fixLineCount++;
                }
                int row = beginRow + fixLineCount + i;
                for (int j = 1; j < itemDataSize; j++) {
                    String item = tagFormulaList.get(j);
                    Double val = null;
                    if(StringUtils.isBlank(item)) {
                        continue;
                    }
                    String[] itemArray = item.split("_");
                    switch (itemArray[0]) {
                        case "CountChargeNum":
                            BigDecimal countChargeNum = glDataUtil.getCountChargeNumByTapTimeRange(version, eachDateQuery);
                            if(Objects.nonNull(countChargeNum)) val = countChargeNum.doubleValue();
                            break;
                        case "InbaRate":
                            BigDecimal inbaRateInRange = glDataUtil.getInbaRateInRange(version, eachDateQuery);
                            if(Objects.nonNull(inbaRateInRange)) val = inbaRateInRange.doubleValue();
                            break;
                        case "AnalysisValues":
                            if (itemArray.length == 4) {
                                List<Double> list = getAnalysisValuesByKey(version, eachDateQuery, itemArray[1], itemArray[2]);
                                val = ExcelWriterUtil.executeSpecialList(itemArray[3], list);
                            }
                            break;
                        case "TapSummary":
                            if (itemArray.length == 2) {
                                val = getTapSummaryByKey(version, eachDateQuery, itemArray[1]);
                            }
                            break;
                        default:
                            val = getLatestValue(version, String.valueOf(eachDateQuery.getRecordDate().getTime()), item);
                            break;
                    }
                    ExcelWriterUtil.addCellData(cellDataList, row, j, val);
                }
            }
        } catch (Exception e) {
            log.error("处理出渣铁及煤气成分出错", e);
        }
    }

    /**
     * 获取出铁信息
     * @param version
     * @param dateQuery
     * @param key
     * @return
     */
    private Double getTapSummaryByKey (String version, DateQuery dateQuery, String key) {
        Double val = null;
        Map<String, String> queryParam = new HashMap();
        queryParam.put("startTime",  Objects.requireNonNull(dateQuery.getStartTime().getTime()).toString());
        queryParam.put("endTime",  Objects.requireNonNull(dateQuery.getEndTime().getTime()).toString());
        String result = httpUtil.get(getTapSummaryByRangeUrl(version), queryParam);
        BigDecimal value = FastJSONUtil.getJsonValueByKey(result, Lists.newArrayList("data"), key, BigDecimal.class);
        if(Objects.nonNull(value)) {
            val = value.doubleValue();
        }
        return val;
    }

    /**
     * 根据key获取AnalysisValue中某个元素的list
     * @param version
     * @param dateQuery
     * @param brandCode
     * @param key
     * @return
     */
    private List<Double> getAnalysisValuesByKey(String version, DateQuery dateQuery, String brandCode, String key) {
        List<String> strList = new ArrayList<String>(){{add("B2");add("B4");}};
        List<AnalysisValue> list = null;
        List<Double> valueList = new ArrayList<>();
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
            if (Objects.nonNull(analysisValueDTO)) {
                list = analysisValueDTO.getData();
                if (Objects.nonNull(list) && CollectionUtils.isNotEmpty(list)) {
                    for (AnalysisValue analysisValue:list) {
                        Map<String, BigDecimal> values = analysisValue.getValues();
                        if (Objects.nonNull(values)) {
                            BigDecimal val = values.get(key);
                            if(Objects.nonNull(val)) {
                                if(!strList.contains(key)) {
                                    val = val.multiply(new BigDecimal(100));
                                }
                                valueList.add(val.doubleValue());
                            }
                        }
                    }
                }
            }
        }
        return valueList;
    }

    /**
     * 获取latest tagvalue
     * @param version
     * @param time
     * @param tagName
     * @return
     */
    private Double getLatestValue(String version, String time, String tagName) {
        BigDecimal tagValue = null;
        String url = getLatestTagValueUrl(version);
        Map<String, String> param = new HashMap<>();
        param.put("time", time);
        param.put("tagname", tagName);
        String result = httpUtil.get(url, param);
        tagValue = FastJSONUtil.getJsonValueByKey(result, Lists.newArrayList("data"), "val", BigDecimal.class);
        if (Objects.nonNull(tagValue)) {
            return tagValue.doubleValue();
        }
        return null;
    }
}
