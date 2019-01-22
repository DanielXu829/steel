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
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
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
public class PeimeiliangWriter extends AbstractExcelReadWriter {
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
//                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                int size = dateQueries.size();
                DateQuery dateQuery = dateQueries.get(0);
                Set<String> strings = mapDataHandler(getUrl2(), dateQuery);
                List<String> columns = new ArrayList<>(strings);
                if ("name".equals(sheetSplit[1])) {
                    int rowNum = 0;
                    for (String val : strings) {
                        setSheetValue(sheet, 0, rowNum, val);
                        rowNum++;
                    }
                } else {
                    for (int j = 0; j < size; j++) {
                        int rowIndex=10+j;
                        List<CellData> cellData = mapDataHandler2(getUrl2(), columns, dateQueries.get(j), rowIndex, sheet);
                        ExcelWriterUtil.setCellValue(sheet, cellData);
                    }
                }

            }
        }
        return workbook;
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


    protected Set<String> mapDataHandler(String url, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam1(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONArray rows = jsonObject.getJSONArray("rows");
        if (Objects.isNull(rows)) {
            return null;
        }
        HashSet<String> meiName = new HashSet<>();
        for (int i = 0; i < rows.size(); i++) {
            JSONObject jsonObject1 = rows.getJSONObject(i);
            String clock = jsonObject1.getString("clock");
            String date = DateUtil.getFormatDateTime(DateUtil.strToDate(clock, DateUtil.fullFormat), "yyyy/MM/dd HH:mm:ss");
            Map<String, String> queryParam1 = getQueryParam(date);
            String result1 = httpUtil.get(getUrl(), queryParam1);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject2 = JSONObject.parseObject(result1);
                if (Objects.nonNull(jsonObject2)) {
                    JSONObject data = jsonObject2.getJSONObject("data");
                    Map<String, Object> innerMap = data.getInnerMap();
                    Set<String> strings = innerMap.keySet();
                    for (String key : strings) {
                        meiName.add(innerMap.get(key).toString());
                    }
                }
            }
        }
        return meiName;
    }

    protected List<CellData> mapDataHandler2(String url, List<String> columns, DateQuery dateQuery, int rowIndex, Sheet sheet) {
        Map<String, String> queryParam = getQueryParam2(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONArray rows = jsonObject.getJSONArray("rows");
        if (Objects.isNull(rows)) {
            return null;
        }
        Map<String, Object> rowData = new HashMap<>();
        for (int i = 0; i < rows.size(); i++) {
            JSONObject jsonObject1 = rows.getJSONObject(i);
            String clock = jsonObject1.getString("clock");
            String date = DateUtil.getFormatDateTime(DateUtil.strToDate(clock, DateUtil.fullFormat), "yyyy/MM/dd HH:mm:ss");
            Map<String, String> queryParam1 = getQueryParam(date);
            String result1 = httpUtil.get(getUrl(), queryParam1);
            List<String> nameList = new ArrayList<>();
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject2 = JSONObject.parseObject(result1);
                if (Objects.nonNull(jsonObject2)) {
                    JSONObject data = jsonObject2.getJSONObject("data");
                    Map<String, Object> innerMap = data.getInnerMap();
                    Set<String> strings = innerMap.keySet();
                    for (String key : strings) {
                        nameList.add(innerMap.get(key).toString());
                    }
                    String date1 = DateUtil.getFormatDateTime(DateUtil.strToDate(clock, DateUtil.fullFormat), "yyyy/MM/dd HH:mm:00");
                    for (int j = 0; j < nameList.size(); j++) {
                        String name = nameList.get(j);
                        List<String> rowCelVal = PoiCustomUtil.getRowCelVal(sheet, j);
                        Double val = 0.0;
                        for (int k = 0; k < rowCelVal.size(); k++) {
                            String tagName = rowCelVal.get(k);
                            Map<String, String> queryParam3 = getQueryParam3(date1, tagName);
                            String result2 = httpUtil.get(getUrl3(), queryParam3);
                            JSONObject jsonObject3 = JSONObject.parseObject(result2);
                            JSONArray rows3 = jsonObject3.getJSONArray("rows");
                            JSONObject obj = rows3.getJSONObject(0);
                            val += obj.getDouble("val");
                        }
//                        valList.add(val);
                        Object o = rowData.get(name);
                        if(Objects.isNull(o)){
                            rowData.put(name, val);
                        }else {
                            Double cc=(Double)o+ val;
                            rowData.put(name,cc);
                        }

                    }

                }
            }

        }
        int startRow = rowIndex;
        return ExcelWriterUtil.handlerRowData(columns, startRow, rowData);
    }

    protected Map<String, String> getQueryParam(String date) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", date);
        return result;
    }

    protected Map<String, String> getQueryParam1(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        String start=DateUtil.getFormatDateTime(DateQueryUtil.getMonthStartTime(dateQuery.getRecordDate()), "yyyy/MM/dd HH:mm:ss");
        String end=DateUtil.getFormatDateTime(DateQueryUtil.getMonthEndTime(dateQuery.getRecordDate()), "yyyy/MM/dd HH:mm:ss");
        result.put("startDate", start);
        result.put("endDate", end);
        result.put("tagName", "CK67_L1R_CB_CBReset_4_report");
        return result;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("tagName", "CK67_L1R_CB_CBReset_4_report");
        return result;
    }

    protected Map<String, String> getQueryParam3(String date, String tagName) {
        Map<String, String> result = new HashMap<>();
        result.put("tagName", tagName);
        result.put("time", date);
        return result;
    }

    protected String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getCoalSiloName";
    }

    protected String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/manufacturingState/getTagValue";
    }

    protected String getUrl3() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getVauleByNameAndTime";
    }
}
