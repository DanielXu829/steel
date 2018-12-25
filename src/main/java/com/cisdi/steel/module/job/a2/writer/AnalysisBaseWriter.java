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
public class AnalysisBaseWriter extends AbstractExcelReadWriter {
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
                if ("analysis".equals(sheetSplit[1])) {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        for (int k = 0; k < columns.size(); k++) {
                            if (StringUtils.isNotBlank(columns.get(k))) {
                                String[] split = columns.get(k).split("/");
                                int rowIndex = 1 + j;
                                if(split.length==2){
                                    Double cellDataList = mapDataHandler2(getUrl2(), item, split[0], split[1]);
                                    setSheetValue(sheet, rowIndex, k, cellDataList);
                                } else if (split.length == 3) {
                                    Double cellDataList = mapDataHandler3(getUrl3(), item, split[0], split[1], split[2]);
                                    setSheetValue(sheet, rowIndex, k, cellDataList);
                                }else {
                                    Double cellDataList = mapDataHandler4(getUrl4(), item, split[0], split[1]);
                                    setSheetValue(sheet, rowIndex, k, cellDataList);
                                }
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl(), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
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

    protected Double mapDataHandler2(String url, DateQuery dateQuery, String brandcode, String anaitemname) {
        Map<String, String> queryParam = getQueryParam2(dateQuery, brandcode, anaitemname);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        Double data = jsonObject.getDouble("data");
        return data;
    }

    protected Double mapDataHandler3(String url, DateQuery dateQuery, String brandcode, String anaitemname, String source) {
        Map<String, String> queryParam = getQueryParam3(dateQuery, brandcode, anaitemname,source);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        Double data = jsonObject.getDouble("data");
        return data;
    }


    protected Double mapDataHandler4(String url, DateQuery dateQuery, String code, String flag) {
        Map<String, String> queryParam = getQueryParam4(dateQuery, code, flag);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONArray row = JSONArray.parseArray(result);
        if (row.isEmpty()) {
            return null;
        }
        JSONObject data = (JSONObject) row.get(0);
        if (Objects.isNull(data)) {
            return null;
        }
        Double confirmWgt = data.getDouble("confirmWgt");
        return confirmWgt;
    }

    protected List<CellData> mapDataHandler(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam(dateQuery);
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
                            JSONObject obj = rows.getJSONObject(0);
                            if (Objects.nonNull(obj)) {
                                Double val = obj.getDouble("val");
                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                            }
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    @Override
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getRecordDate());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String dateTime = DateUtil.getFormatDateTime(calendar.getTime(), DateUtil.fullFormat);
        result.put("time", dateTime);
        return result;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery, String brandcode, String anaitemname) {
        Map<String, String> result = new HashMap<>();
        result.put("brandcode", brandcode);
        result.put("starttime", DateUtil.getFormatDateTime(dateQuery.getStartTime(),"yyyy/MM/dd HH:mm:ss"));
        result.put("endtime", DateUtil.getFormatDateTime(dateQuery.getEndTime(),"yyyy/MM/dd HH:mm:ss"));
        result.put("anaitemname", anaitemname);
        return result;
    }

    protected Map<String, String> getQueryParam3(DateQuery dateQuery, String brandcode, String anaitemname, String source) {
        Map<String, String> result = new HashMap<>();
        result.put("brandcode", brandcode);
        result.put("starttime", DateUtil.getFormatDateTime(dateQuery.getStartTime(),"yyyy/MM/dd HH:mm:ss"));
        result.put("endtime", DateUtil.getFormatDateTime(dateQuery.getEndTime(),"yyyy/MM/dd HH:mm:ss"));
        result.put("anaitemname", anaitemname);
        result.put("source", source);
        return result;
    }


    protected Map<String, String> getQueryParam4(DateQuery dateQuery, String code, String flag) {
        Map<String, String> result = new HashMap<>();
        result.put("code", code);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getRecordDate());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        result.put("dateTime", DateUtil.getFormatDateTime(calendar.getTime(), "yyyy/MM/dd HH:mm:ss"));
        if (DateUtil.getFormatDateTime(dateQuery.getEndTime(), "HH").equals("08")) {
            result.put("shift", "1");
        } else if (DateUtil.getFormatDateTime(dateQuery.getEndTime(), "HH").equals("16")) {
            result.put("shift", "2");
        } else {
            result.put("shift", "3");
        }
        result.put("flag", flag);
        return result;
    }

    protected String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getVauleByNameAndTime";
    }

    protected String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/analyses/getAnaitemValByCode";
    }

    protected String getUrl3() {
        return httpProperties.getUrlApiJHOne() + "/analyses/getIfAnaitemValByCode";
    }

    protected String getUrl4() {
        return httpProperties.getUrlApiJHOne() + "/cokingYieldAndNumberHoles/getCokeYielAndHolesByDateAndShiftAndCode";
    }
}
