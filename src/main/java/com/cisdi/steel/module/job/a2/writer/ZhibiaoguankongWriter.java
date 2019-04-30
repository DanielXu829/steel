package com.cisdi.steel.module.job.a2.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 指标管控
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ZhibiaoguankongWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                DateQuery dateQuery = null;
                String shift = "";
                String shiftDate = "";
                int shiftNum = 0;
                if (DateUtil.isEffectiveDate(date.getRecordDate(), dateQueries.get(0).getStartTime(), dateQueries.get(0).getEndTime())) {
                    dateQuery = dateQueries.get(0);
                    shift = "夜班";
                    shiftDate = "00:00";
                    shiftNum = 1;
                } else if (DateUtil.isEffectiveDate(date.getRecordDate(), dateQueries.get(1).getStartTime(), dateQueries.get(1).getEndTime())) {
                    dateQuery = dateQueries.get(1);
                    shift = "白班";
                    shiftDate = "08:00";
                    shiftNum = 2;
                } else {
                    dateQuery = dateQueries.get(2);
                    shift = "中班";
                    shiftDate = "16:00";
                    shiftNum = 3;
                }
                int rowIndex = 1;
                if ("crushing".equals(sheetSplit[1])) {
                    Double cellDataList = this.valHandler(getUrl(), dateQuery);
                    setSheetValue(sheet, 1, 0, cellDataList);
                } else if ("lianjiao".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = this.mapDataHandler(getUrl6(), rowIndex,dateQuery, shiftNum+"");
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else if ("tag".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl3(), columns, dateQuery);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else {
                    Row row = sheet.createRow(1);
                    row.createCell(2).setCellValue(shift);
                    row.getCell(2).setCellType(CellType.STRING);
                    row.createCell(3).setCellValue(shiftDate);
                    row.getCell(3).setCellType(CellType.STRING);
                    List<CellData> cellDataList = mapDataHandler1(rowIndex, getUrl5(), columns, dateQuery);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam2(dateQuery);
        int size = columns.size();
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            if (StringUtils.isNotBlank(column)) {
                queryParam.put("tagNames", column);
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (Objects.nonNull(jsonObject)) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (Objects.nonNull(data)) {
                            Double val = data.getDouble(column);
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler1(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        List<CellData> cellDataList = new ArrayList<>();
        Map<String, String> queryParam = getQueryParamX(dateQuery);
        queryParam.put("tagNames", columns.get(0));
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    JSONArray arr = data.getJSONArray(columns.get(0));
                    if (Objects.nonNull(arr) && arr.size() != 0) {
                        Double val = 0.0;
                        for (int k = 0; k < arr.size(); k++) {
                            JSONObject jsonObject2 = arr.getJSONObject(k);
                            if (jsonObject2.getDouble("val") > 90) {
                                val = jsonObject2.getDouble("val");
                            }
                        }
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0, val);
                    }
                }
            }
        }
        return cellDataList;
    }

    private void setSheetValue(Sheet sheet, Integer rowNum, Integer columnNum, Object obj) {
        Row row = sheet.getRow(rowNum);
        if (Objects.isNull(row)) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.getCell(columnNum);
        if (Objects.isNull(cell)) {
            cell = row.createCell(columnNum);
        }
        PoiCustomUtil.setCellValue(cell, obj);
    }

    public Double valHandler(String url, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }
        JSONObject r = data.getJSONObject("particleDistribution");
        if (Objects.isNull(r)) {
            return null;
        }
        Double crushingFineness = r.getDouble("crushingFineness");
        return crushingFineness;
    }

    public List<CellData> mapDataHandler(String url, int rowIndex, DateQuery dateQuery, String shift) {
        String[] coke = {"CO6", "CO7"};
        List<Double> list = new ArrayList<>();
        List<CellData> cellDataList = new ArrayList<>();
        Double k2 = 0.0;
        Double km = 0.0;
        Double kAvg = 0.0;
        Double kAn = 0.0;
        for (int i = 0; i < coke.length; i++) {
            String cokeNo = coke[i];
            Map<String, String> queryParam = getQueryParam4(dateQuery, shift, cokeNo);
            String result = httpUtil.get(url, queryParam);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (Objects.nonNull(jsonObject)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONObject obj = data.getJSONObject("DayTemperatureStatistics");
                        if (Objects.nonNull(obj)) {
                            kAvg += obj.getDouble("dayKAvg");
                            kAn += obj.getDouble("dayKan");
                        }
                    }
                }
            }
        }
        Map<String, String> queryParam = getQueryParam4(dateQuery, shift, "CO6");
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    JSONObject obj = data.getJSONObject("DayTemperatureStatistics");
                    if (Objects.nonNull(obj)) {
                        k2 = obj.getDouble("k2");
                        km = obj.getDouble("kM");
                    }
                }
            }
        }
        list.add(k2);
        list.add(km);
        list.add(kAvg / 2);
        list.add(kAn / 2);
        for (int i = 0; i < list.size(); i++) {
            Double val = list.get(i);
            ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
        }
        return cellDataList;
    }


    @Override
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("start", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("end", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("type", "avg");
        return result;
    }

    protected Map<String, String> getQueryParamX(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam4(DateQuery dateQuery, String shift, String cokeNo) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd hh:mm:ss"));
        result.put("shift", shift);
        result.put("cokeNo", cokeNo);
        return result;
    }

    private String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getParticleDistributionLatest";
    }

    private String getUrl3() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getTagValueStatisticType";
    }

    private String getUrl6() {
        return httpProperties.getUrlApiJHOne() + "/dayTemperatureStatistics/selectByDateAndShift";
    }

    protected String getUrl5() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getTagValue";
    }
}
