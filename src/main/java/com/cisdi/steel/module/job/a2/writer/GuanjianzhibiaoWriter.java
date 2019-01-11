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
                        List<CellData> cellData = this.valHandler1(getUrl2(), columns, dateQueries.get(j), rowIndex);
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
        Map<String, String> queryParam = getQueryParam1(dateQuery);
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
                                        Map<String, String> queryParam1 = getQueryParam3(DateUtil.getFormatDateTime(date, "yyyy/MM/dd HH:mm:00"));
                                        queryParam1.put("tagName", "CK67_L1R_CB_CBAcTol_1m_avg");
                                        String result1 = httpUtil.get(getUrl5(), queryParam1);
                                        if (StringUtils.isNotBlank(result1)) {
                                            JSONObject jsonObject1 = JSONObject.parseObject(result1);
                                            JSONArray rows1 = jsonObject1.getJSONArray("rows");
                                            JSONObject obj1 = rows1.getJSONObject(0);
                                            if (Objects.nonNull(obj1)) {
                                                Double val1 = obj1.getDouble("val");
                                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val1);
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

    public List<CellData> valHandler1(String url, List<String> columns, DateQuery dateQuery, int rowBatch) {
        if (dateQuery.getRecordDate().compareTo(new Date()) == 1) {
            return null;
        }
        Map<String, String> queryParam = getQueryParam1(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }
        JSONArray r = data.getJSONArray("kAnkAvgs");
        if (Objects.isNull(r) || r.size() == 0) {
            return null;
        }
        Double k2 = 0.0;
        Double coalLoadingCoefficient = 0.0;
        Double no1FurnaceKAn = 0.0;
        Double no1FurnaceKAvg = 0.0;
        Double no2FurnaceKAn = 0.0;
        Double no2FurnaceKAvg = 0.0;
        for (int i = 0; i < r.size(); i++) {
            JSONObject jsonObject1 = r.getJSONObject(i);
            k2 += jsonObject1.getDouble("k2");
            coalLoadingCoefficient += jsonObject1.getDouble("coalLoadingCoefficient");
            no1FurnaceKAn += jsonObject1.getDouble("no1FurnaceKAn");
            no1FurnaceKAvg += jsonObject1.getDouble("no1FurnaceKAvg");
            no2FurnaceKAn += jsonObject1.getDouble("no2FurnaceKAn");
            no2FurnaceKAvg += jsonObject1.getDouble("no2FurnaceKAvg");
        }
        HashMap<String, Object> Map = new HashMap<>();
        Map.put("k2", k2 / r.size());
        Map.put("coalLoadingCoefficient", coalLoadingCoefficient / r.size());
        Map.put("kAn", (no1FurnaceKAn + no2FurnaceKAn) / (r.size() * 2));
        Map.put("kAvg", (no1FurnaceKAvg + no2FurnaceKAvg) / (r.size() * 2));

        List<CellData> cellData = ExcelWriterUtil.handlerRowData(columns, rowBatch, Map);
        return cellData;
    }

    @Override
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam1(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addDays(dateQuery.getStartTime(), -1), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(DateUtil.addDays(dateQuery.getEndTime(), -1), "yyyy/MM/dd HH:mm:ss"));

        return result;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("start", DateUtil.getFormatDateTime(DateUtil.getDateBeginTime(dateQuery.getStartTime()), "yyyy/MM/dd HH:mm:ss"));
        result.put("end", DateUtil.getFormatDateTime(DateUtil.getDateEndTime(dateQuery.getStartTime()), "yyyy/MM/dd HH:mm:ss"));
        result.put("type", "avg");
        return result;
    }

    protected Map<String, String> getQueryParam3(String date) {
        Map<String, String> result = new HashMap<>();
        result.put("time", date);
        return result;
    }

    private String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getParticleDistributionLatest";
    }

    private String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/cokeActualPerformance/getCokeActuPerfByTimeEnd";
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

}