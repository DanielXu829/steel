package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.res.PageData;
import com.cisdi.steel.dto.response.gl.res.TapSgRow;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.FastJSONUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

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
            int beginRow = 3;
            Sheet sheet = workbook.getSheetAt(0);
            List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(dateQuery.getRecordDate(), 1);
            int fixLineCount = 0;
            for (int i = 0; i < allDayBeginTimeInCurrentMonth.size(); i++) {
                DateQuery eachDateQuery = DateQueryUtil.buildDayAheadTwoHour(allDayBeginTimeInCurrentMonth.get(i));
//                Date day = eachDateQuery.getRecordDate();
                Date day = allDayBeginTimeInCurrentMonth.get(i);
                // 计算行
                if (i > 0 && i % 10 == 0) {
                    fixLineCount++;
                }
                int rowIndex = beginRow + fixLineCount + i;
                //原燃料质量
                handleYuanRanLiaoZhiLiang(workbook, version, rowIndex, eachDateQuery);
                //原燃料消耗
                handleYuanRanLiaoXiaoHao(workbook, version, rowIndex, eachDateQuery);
                //布料、风口及炉况情况
                handleBuLiaoFengKouLuKuang(workbook, version, rowIndex, eachDateQuery);
                //出渣铁及煤气成分
                handleChuZhaTieMeiQiChenFen(workbook, version, rowIndex, eachDateQuery);
            }
            //技术经济指标及操作参数
            handleJiShuJingJiZhiBiao(workbook, version, allDayBeginTimeInCurrentMonth);
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
    private void handleYuanRanLiaoZhiLiang(Workbook workbook, String version, int rowIndex, DateQuery eachDateQuery) {
        List<CellData> resultList = new ArrayList<>();
        Sheet sheet = workbook.getSheet("原燃料质量");
        ExcelWriterUtil.setCellValue(sheet, resultList);
    }

    //原燃料消耗
    private void handleYuanRanLiaoXiaoHao(Workbook workbook, String version, int rowIndex, DateQuery eachDateQuery) {
        List<CellData> resultList = new ArrayList<>();
        Sheet sheet = workbook.getSheet("原燃料消耗");
        ExcelWriterUtil.setCellValue(sheet, resultList);
    }

    //布料、风口及炉况情况
    private void handleBuLiaoFengKouLuKuang(Workbook workbook, String version, int rowIndex, DateQuery eachDateQuery) {
        List<CellData> resultList = new ArrayList<>();
        Sheet sheet = workbook.getSheet("布料、风口及炉况情况");
        ExcelWriterUtil.setCellValue(sheet, resultList);
    }

    //出渣铁及煤气成分
    private void handleChuZhaTieMeiQiChenFen(Workbook workbook, String version, int rowIndex, DateQuery eachDateQuery) {
        List<CellData> resultList = new ArrayList<>();
        Sheet sheet = workbook.getSheet("出渣铁及煤气成分");
        handleTapData(sheet, 5, eachDateQuery, resultList, version);
        ExcelWriterUtil.setCellValue(sheet, resultList);
    }

    //技术经济指标及操作参数
    private void handleJiShuJingJiZhiBiao(Workbook workbook, String version, List<Date> allDayBeginTimeInCurrentMonth) {
        int beginRow = 8;
        int fixLineCount = 0;
        Sheet sheet = workbook.getSheet("技术经济指标及操作参数");
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
    }

    /**
     * 处理出铁数据
     * @param sheet
     * @param dateQuery
     * @param resultList
     * @param version
     * @return tapNoList
     */
    private void handleTapData(Sheet sheet, int itemRowNum, DateQuery dateQuery, List<CellData> resultList, String version) {
        // 铁次(tapNo)list，用于调接口罐号重量接口
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
                return;
            }
            PageData<TapSgRow> pageData = JSON.parseObject(tapData, new TypeReference<PageData<TapSgRow>>(){});
            if (Objects.isNull(pageData)) {
                return;
            }
            List<TapSgRow> tapSgRowData = pageData.getData();
            if (CollectionUtils.isEmpty(tapSgRowData)) {
                return;
            }
            tapSgRowData.sort(comparing(TapSgRow::getStartTime)); // 按时间先后进行排序
            int dataSize = tapSgRowData.size();
            List<String> itemRow = PoiCustomUtil.getRowCelVal(sheet, itemRowNum);
            if (CollectionUtils.isEmpty(itemRow)) {
                return;
            }
            int itemDataSize = itemRow.size();
            TapSgRow tapSgRow = null;
            // 需要对时间数据进行格式化处理
            String[] timeItemArray = {"startTime", "slagTime", "endTime"};
            List<String> timeItemList = Arrays.asList(timeItemArray);

            // 遍历标记行
            for (int i = 0; i < dataSize; i++) {
                tapSgRow = tapSgRowData.get(i);
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
                Integer tagValue = FastJSONUtil.getJsonValueByKey(result, Lists.newArrayList("data"), "val", Integer.class);
                ExcelWriterUtil.addCellData(resultList, itemRowNum + 1 + i, 1, tagValue);
                //批料数
                String queryUrl = getUrlTagNamesInRange(version);
                JSONObject query = new JSONObject();
                query.put("starttime", String.valueOf(stringObjectMap.get("startTime")));
                query.put("endtime", String.valueOf(stringObjectMap.get("endTime")));
                query.put("tagnames", new String[]{"BF8_L2M_SH_ChargeCount_evt"});
                SerializeConfig serializeConfig = new SerializeConfig();
                String jsonString = JSONObject.toJSONString(query, serializeConfig);
                String results = httpUtil.postJsonParams(queryUrl, jsonString);
                JSONObject tagData = FastJSONUtil.getJsonObjectByKey(results, Lists.newArrayList("data", "BF8_L2M_SH_ChargeCount_evt"));
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
        } catch (Exception e) {
            log.error("处理反面-出铁出错", e);
        }
    }
}
