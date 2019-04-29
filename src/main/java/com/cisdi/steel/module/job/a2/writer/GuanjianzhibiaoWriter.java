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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 关键指标
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GuanjianzhibiaoWriter extends AbstractExcelReadWriter {
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
                int size = dateQueries.size();
                if ("crushing".equals(sheetSplit[1])) {
                    for (int j = 0; j < size; j++) {
                        int rowIndex = 1 + j;
                        Double cellDataList = this.valHandler(getUrl(), dateQueries.get(j));
                        setSheetValue(sheet, rowIndex, 0, cellDataList);
                    }
                } else if ("lianjiao".equals(sheetSplit[1])) {
                    for (int j = 0; j < size; j++) {
                        int rowIndex = 1 + j;
                        List<CellData> cellData = this.valHandler1(getUrl6(), rowIndex, dateQueries.get(j));
                        ExcelWriterUtil.setCellValue(sheet, cellData);
                    }
                } else if ("tag".equals(sheetSplit[1])) {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl3(), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = mapDataHandler1(rowIndex, getUrl4(), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
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
        Map<String, String> queryParam = getQueryParamx(dateQuery);
        int size = columns.size();
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            if (StringUtils.isNotBlank(column)) {
                queryParam.put("tagName", column);
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (Objects.nonNull(jsonObject)) {
                        JSONArray rows = jsonObject.getJSONArray("rows");
                        if (Objects.nonNull(rows)) {
                            for (int j = 0; j < rows.size(); j++) {
                                JSONObject obj = rows.getJSONObject(j);
                                if (Objects.nonNull(obj)) {
                                    String clock = obj.getString("clock");
                                    Date date = DateUtil.strToDate(clock, DateUtil.fullFormat);
                                    Date endTime = dateQuery.getStartTime();
                                    Date startTime = DateUtil.addHours(endTime, -8);
                                    if (DateUtil.isEffectiveDate(date, startTime, endTime)) {
                                        Map<String, String> queryParam1 = getQueryParamX(date);
                                        queryParam1.put("tagNames", "CK67_L1R_CB_CBAcTol_1m_evt");
                                        String result1 = httpUtil.get(getUrl7(), queryParam1);
                                        if (StringUtils.isNotBlank(result1)) {
                                            JSONObject jsonObject1 = JSONObject.parseObject(result1);
                                            JSONObject data = jsonObject1.getJSONObject("data");
                                            JSONArray arr = data.getJSONArray("CK67_L1R_CB_CBAcTol_1m_evt");
                                            if (Objects.nonNull(arr) && arr.size() > 0) {
                                                Double val = 0.0;
                                                for (int k = 0; k < arr.size(); k++) {
                                                    JSONObject jsonObject2 = arr.getJSONObject(k);
                                                    if (jsonObject2.getDouble("val") > 80) {
                                                        val = jsonObject2.getDouble("val");
                                                    }
                                                }
                                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                                            }
                                        }
                                    }

                                }
                            }
                        }
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
        if (dateQuery.getRecordDate().compareTo(new Date()) == 1) {
            return null;
        }
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

    public List<CellData> valHandler1(String url, int rowIndex, DateQuery dateQuery) {
        String[] shifts = {1 + "", 2 + "", 3 + ""};
        String[] coke = {"CO6", "CO7"};
        List<Double> list = new ArrayList<>();
        List<CellData> cellDataList = new ArrayList<>();
        Double k2 = 0.0;
        Double km = 0.0;
        Double kAvg = 0.0;
        Double kAn = 0.0;
        for (int i = 0; i < coke.length; i++) {
            String cokeNo = coke[i];
            Map<String, String> queryParam = getQueryParam4(dateQuery, 1+"", cokeNo);
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
        for (int i = 0; i <shifts.length ; i++) {
            String shift = shifts[i];
            Map<String, String> queryParam = getQueryParam4(dateQuery, shift, "CO6");
            String result = httpUtil.get(url, queryParam);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (Objects.nonNull(jsonObject)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONObject obj = data.getJSONObject("DayTemperatureStatistics");
                        if (Objects.nonNull(obj)) {
                            k2 += obj.getDouble("k2");
                            km += obj.getDouble("kM");
                        }
                    }
                }
            }
        }
        list.add(k2/3);
        list.add(km/3);
        list.add(kAvg/2);
        list.add(kAn/2);
        for (int i = 0; i <list.size() ; i++) {
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

    protected Map<String, String> getQueryParamx(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addDays(dateQuery.getStartTime(), -1), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(DateUtil.addDays(dateQuery.getEndTime(), -1), "yyyy/MM/dd HH:mm:ss"));

        return result;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("start", DateUtil.getFormatDateTime(DateUtil.addHours(dateQuery.getStartTime(), -8), "yyyy/MM/dd HH:mm:ss"));
        result.put("end", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("type", "avg");
        return result;
    }

    protected Map<String, String> getQueryParam3(String date) {
        Map<String, String> result = new HashMap<>();
        result.put("time", date);
        return result;
    }

    protected Map<String, String> getQueryParam4(DateQuery dateQuery, String shift, String cokeNo) {
        Map<String, String> result = new HashMap<>();
        Date date = DateUtil.addDays(dateQuery.getRecordDate(), -1);
        result.put("date", DateUtil.getFormatDateTime(date, "yyyy/MM/dd hh:mm:ss"));
        result.put("shift", shift);
        result.put("cokeNo", cokeNo);
        return result;
    }

    protected Map<String, String> getQueryParamX(Date date) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(date, "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(DateUtil.addMinute(date, 1), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    private String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getParticleDistributionLatest";
    }

    private String getUrl3() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getTagValueStatisticType";
    }

    private String getUrl4() {
        return httpProperties.getUrlApiJHOne() + "/manufacturingState/getTagValue";
    }

    private String getUrl5() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getVauleByNameAndTime";
    }

    private String getUrl6() {
        return httpProperties.getUrlApiJHOne() + "/dayTemperatureStatistics/selectByDateAndShift";
    }

    protected String getUrl7() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getTagValue";
    }

}
