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
public class LianjiaoribaoWriter extends AbstractExcelReadWriter {
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
                String name = sheetSplit[1];
                int size = dateQueries.size();
                if ("lianjaorb".equals(name)) {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl1(), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("kjjunzhi".equals(name)) {
                    int startRow=1;
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        List<CellData> cellDataList = this.mapDataHandler2(getUrl2(), columns, 1,item,startRow);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        startRow++;
                    }
                } else if ("causek".equals(name)) {
                    int startRow=1;
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        List<CellData> cellDataList = this.mapDataHandler3(getUrl3(), columns, 1,item,startRow);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        startRow++;
                    }
                }else if ("actual".equals(name)) {
                    int startRow=1;
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        List<CellData> cellDataList = this.mapDataHandler4(getUrl4(), columns, 1,item,startRow);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        startRow++;
                    }
                }else {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        for (int k = 0; k < columns.size(); k++) {
                                String[] split = columns.get(k).split("/");
                                int rowIndex = 1 + j;
                                Double cellDataList = mapDataHandler5(getUrl5(), item, split[0], split[1]);
                                setSheetValue(sheet, rowIndex, k, cellDataList);
                        }
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

    protected Double mapDataHandler5(String url, DateQuery dateQuery, String brandcode, String anaitemname) {
        Map<String, String> queryParam = getQueryParam5(dateQuery, brandcode, anaitemname);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        Double data = jsonObject.getDouble("data");
        return data;
    }

    protected Map<String, String> getQueryParam5(DateQuery dateQuery, String brandcode, String anaitemname) {
        Map<String, String> result = new HashMap<>();
        result.put("brandcode", brandcode);
        result.put("starttime", dateQuery.getStartTime().toString());
        result.put("endtime", dateQuery.getEndTime().toString());
        result.put("anaitemname", anaitemname);
        result.put("source", "三回收");
        return result;
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

    public List<CellData> mapDataHandler2(String url, List<String> columns, int rowBatch,DateQuery item,int startRow) {
        Map<String, String> queryParam = getQueryParam2(item);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONArray rows = jsonObject.getJSONArray("rows");
        if (Objects.isNull(rows)) {
            return null;
        }
        JSONArray r = (JSONArray)rows.get(0);
        if (Objects.isNull(r) || r.size() == 0) {
            return null;
        }
        return handlerJsonArray(columns, rowBatch, r, startRow);
    }

    public List<CellData> mapDataHandler3(String url, List<String> columns,int rowBatch,DateQuery dateQuery,int startRow) {
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
//        JSONArray r = (JSONArray)rows.get(0);
//        if (Objects.isNull(r) || r.size() == 0) {
//            return null;
//        }
        return handlerJsonArray(columns, rowBatch, rows, startRow);
    }

    public List<CellData> mapDataHandler4(String url, List<String> columns,int rowBatch,DateQuery dateQuery,int startRow) {
        Map<String, String> queryParam = getQueryParame3(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONArray objects = JSONObject.parseArray(result);
        if (Objects.isNull(objects)) {
            return null;
        }
        return handlerJsonArray(columns, rowBatch, objects, startRow);
    }


    /**
     * 处理返回的 json格式
     *
     * @param columns  列名
     * @param rowBatch 占用多少行
     * @param data     数据
     * @param startRow 开始行
     * @return 结果
     */
    protected List<CellData> handlerJsonArray(List<String> columns, int rowBatch, JSONArray data, int startRow) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = data.size();
        for (int i = 0; i < size; i++) {
            JSONObject map = data.getJSONObject(i);
            if (Objects.nonNull(map)) {
                List<CellData> cellDataList1 = ExcelWriterUtil.handlerRowData(columns, startRow, map);
                cellDataList.addAll(cellDataList1);
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


    protected Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
       // result.put("startDate",DateUtil.getFormatDateTime(dateQuery.getStartTime(),"yyyy/MM/dd HH:mm:ss"));
        result.put("datetime", DateUtil.getFormatDateTime(dateQuery.getEndTime(),"yyyy/MM/dd HH:mm:ss"));
        result.put("currentPage", "1");
        result.put("pageSize", "1");
        return result;
    }

    protected Map<String, String> getQueryParame3(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("date",DateUtil.getFormatDateTime(DateUtil.getTodayBeginTime(),"yyyy/MM/dd HH:mm:ss"));
        if(DateUtil.getFormatDateTime(dateQuery.getEndTime(),"HH").equals("08")){
            result.put("shift","1");
        }else if(DateUtil.getFormatDateTime(dateQuery.getEndTime(),"HH").equals("16")){
            result.put("shift","2");
        }else{
            result.put("shift","3");
        }
        return result;
    }



    protected String getUrl1() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getVauleByNameAndTime";
    }

    protected String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/cokingStatementParameter/getByDateTime";
    }

    protected String getUrl3() {
        return httpProperties.getUrlApiJHOne() + "/productionExecution/getCauseOfKCoefficientByDateTime";
    }

    protected String getUrl4() {
        return httpProperties.getUrlApiJHOne() + "/cokeActualPerformance/getCokeActuPerfByDateAndShift";
    }

    protected String getUrl5() {
        return httpProperties.getUrlApiJHOne() + "/analyses/getIfAnaitemValByCodeOrSource";
    }
}
