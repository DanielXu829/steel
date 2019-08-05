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
 * 炼焦月报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
public class LianjiaoyuebaoWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        String version ="67.0";
        try{
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        }catch(Exception e){
        }
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
                    int rowIndex = 1;
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        List<DateQuery> dateQueries1 = DateQueryUtil.buildDay8HourEach(item.getRecordDate());
                        for (int k = 0; k < dateQueries1.size(); k++) {
                            if(dateQueries1.get(k).getRecordDate().after(new Date())){
                                break;
                            }
                            List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl1(version), columns, dateQueries1.get(k),version);
                            ExcelWriterUtil.setCellValue(sheet, cellDataList);
                            rowIndex++;
                        }
                    }
                } else if ("kjjunzhi".equals(name)) {
                    int startRow=1;
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        List<DateQuery> dateQueries1 = DateQueryUtil.buildDay8HourEach(item.getRecordDate());
                        for (int k = 0; k < dateQueries1.size(); k++) {
                            if(dateQueries1.get(k).getRecordDate().after(new Date())){
                                break;
                            }
                            List<CellData> cellDataList = this.mapDataHandler2(getUrl2(version), columns, 1, dateQueries1.get(k), startRow);
                            ExcelWriterUtil.setCellValue(sheet, cellDataList);
                            startRow++;
                        }

                    }
                } else if ("causek".equals(name)) {
                    int startRow=1;
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        List<DateQuery> dateQueries1 = DateQueryUtil.buildDay8HourEach(item.getRecordDate());
                        for (int k = 0; k < dateQueries1.size(); k++) {
                            if(dateQueries1.get(k).getRecordDate().after(new Date())){
                                break;
                            }
                            List<CellData> cellDataList = this.mapDataHandler3(getUrl3(version), columns, 1, dateQueries1.get(k), startRow);
                            ExcelWriterUtil.setCellValue(sheet, cellDataList);
                            startRow++;
                        }
                    }
                }else if ("actual".equals(name)) {
                    int startRow=1;
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        List<DateQuery> dateQueries1 = DateQueryUtil.buildDay8HourEach(item.getRecordDate());
                        for (int k = 0; k < dateQueries1.size(); k++) {
                            if(dateQueries1.get(k).getRecordDate().after(new Date())){
                                break;
                            }
                            List<CellData> cellDataList = this.mapDataHandler4(getUrl4(version), columns, 1,dateQueries1.get(k),startRow);
                            ExcelWriterUtil.setCellValue(sheet, cellDataList);
                            startRow++;
                        }
                    }
                } else if ("kanavg6".equals(name)) {
                    int startRow = 1;
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        List<DateQuery> dateQueries1 = DateQueryUtil.buildDay8HourEach(item.getRecordDate());
                        for (int k = 0; k < dateQueries1.size(); k++) {
                            if(dateQueries1.get(k).getRecordDate().after(new Date())){
                                break;
                            }
                            String jhno = "CO6";
                            if("12.0".equals(version)){
                                jhno = "CO1";
                            }else if("45.0".equals(version)){
                                jhno = "CO4";
                            }
                            List<CellData> cellDataList = this.mapDataHandler4x(getUrl6(version), startRow, dateQueries1.get(k), jhno, version);
                            ExcelWriterUtil.setCellValue(sheet, cellDataList);
                            startRow++;
                        }
                    }
                } else if ("kanavg7".equals(name)) {
                    int startRow = 1;
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        List<DateQuery> dateQueries1 = DateQueryUtil.buildDay8HourEach(item.getRecordDate());
                        for (int k = 0; k < dateQueries1.size(); k++) {
                            if(dateQueries1.get(k).getRecordDate().after(new Date())){
                                break;
                            }
                            String jhno = "CO7";
                            if("12.0".equals(version)){
                                jhno = "CO2";
                            }else if("45.0".equals(version)){
                                jhno = "CO5";
                            }
                            List<CellData> cellDataList = this.mapDataHandler4x(getUrl6(version), startRow, dateQueries1.get(k), jhno, version);
                            ExcelWriterUtil.setCellValue(sheet, cellDataList);
                            startRow++;
                        }
                    }
                }else {
                    int rowIndex=1;
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        List<DateQuery> dateQueries1 = DateQueryUtil.buildDay8HourEach(item.getRecordDate());
                        for (int m = 0; m < dateQueries1.size(); m++) {
                            if(dateQueries1.get(m).getRecordDate().after(new Date())){
                                break;
                            }
                            for (int k = 0; k < columns.size(); k++) {
                                String[] split = columns.get(k).split("/");
                                Double cellDataList = mapDataHandler5(getUrl5(version), dateQueries1.get(m), split[0], split[1], version);
                                setSheetValue(sheet, rowIndex, k, cellDataList);
                            }
                            rowIndex++;
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

    protected Double mapDataHandler5(String url, DateQuery dateQuery, String brandcode, String anaitemname, String version) {
        Map<String, String> queryParam = getQueryParam5(dateQuery, brandcode, anaitemname, version);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        Double data = jsonObject.getDouble("data");
        return data;
    }

    protected Map<String, String> getQueryParam5(DateQuery dateQuery, String brandcode, String anaitemname, String version) {
        Map<String, String> result = new HashMap<>();
        result.put("brandcode", brandcode);
        result.put("starttime", DateUtil.getFormatDateTime(dateQuery.getStartTime(),"yyyy/MM/dd HH:mm:ss"));
        result.put("endtime", DateUtil.getFormatDateTime(dateQuery.getEndTime(),"yyyy/MM/dd HH:mm:ss"));
        result.put("anaitemname", anaitemname);
        if ("12.0".equals(version)) {
            result.put("source", "1#-2#焦炉");
            result.put("unitno", "JH12");
        } else if ("67.0".equals(version)) {
            result.put("source", "6#-7#焦炉");
            result.put("unitno", "JH67");
        } else {
            result.put("source", "4#-5#焦炉");
            result.put("unitno", "JH45");
        }
        return result;
    }

    /**
     * 查询最大值
     * @param dateQuery
     * @param version
     * @param column
     * @return
     */
    public Double getMaxValue(DateQuery dateQuery,String version,String column){
        double val = 0;
        String url = getUrl7(version);
        Map<String, String> queryParam = getQueryParamMaxValue(dateQuery);
        queryParam.put("tagNames", column);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    JSONArray arr = data.getJSONArray(column);
                    if (Objects.nonNull(arr) && arr.size() != 0) {
                        for (int m = 0; m< arr.size(); m++) {
                            JSONObject o = arr.getJSONObject(m);
                            if(o.getDouble("val")>val){
                                val = o.getDouble("val");
                            }
                        }
                    }
                }
            }
        }
        return val;
    }

    /**
     * 查询指定时间的值
     * @param dateQuery
     * @param version
     * @param column
     * @return
     */
    public Double getTheTimeValue(DateQuery dateQuery,String version,String column){
        double val = 0;
        String url = getUrl1(version);
        Map<String, String> queryParam = getQueryParam(dateQuery);
        queryParam.put("tagName", column);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (Objects.nonNull(rows)) {
                    JSONObject obj = rows.getJSONObject(0);
                    if (Objects.nonNull(obj)) {
                        val = obj.getDouble("val");
                    }
                }
            }
        }
        return val;
    }

    protected List<CellData> mapDataHandler(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery,String version) {
        DateQuery tmp = new DateQuery(null,null,null);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateQuery.getStartTime());
        cal.add(Calendar.MINUTE,-30);
        tmp.setStartTime(cal.getTime());
        tmp.setRecordDate(cal.getTime());

        cal.setTime(dateQuery.getEndTime());
        cal.add(Calendar.MINUTE,-30);
        tmp.setEndTime(cal.getTime());

        int size = columns.size();
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            if (StringUtils.isNotBlank(column)) {
                String[] split = column.split(",");
                if (split.length > 1) {
                    double val2 = getMaxValue(tmp,version,split[0]);
                    double val = getTheTimeValue(tmp,version,split[0]);
                    Double total = val2>val ? val2-val : val2;
                    double val3 = getMaxValue(tmp,version,split[1]);
                    double val1 = getTheTimeValue(tmp,version,split[1]);
                    total += val3>val1 ? val3-val1 : val3;
                    ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, total);
                } else {
                    double val5 = getMaxValue(tmp,version,column);
                    double val4 = getTheTimeValue(tmp,version,column);
                    Double val = val5>val4 ? val5-val4 : val5;
                    ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
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
        Map<String, String> queryParam = getQueryParam2x(dateQuery);
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

    public List<CellData> mapDataHandler4x(String url, int startRow, DateQuery dateQuery, String jhno, String version) {
        Map<String, String> queryParam = getQueryParam6(dateQuery, jhno);
        String result = httpUtil.get(url, queryParam);
        List<Double> list = new ArrayList<>();
        List<CellData> cellDataList = new ArrayList<>();
        Double k2 = 0.0;
        Double k1 = 0.0;
        Double k3 = 0.0;
        Double shiftkAvg = 0.0;
        Double shiftkAn = 0.0;
        Double daykAn = 0.0;
        Double daykAvg = 0.0;
        Double km = 0.0;
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    JSONObject obj = data.getJSONObject("DayTemperatureStatistics");
                    if (Objects.nonNull(obj)) {
                        daykAvg = obj.getDouble("dayKAvg");
                        k1 = obj.getDouble("k1");
                        k2 = obj.getDouble("k2");
                        k3 = obj.getDouble("k3");
                        daykAn = obj.getDouble("dayKan");
                        shiftkAvg = obj.getDouble("shiftKAvg");
                        shiftkAn = obj.getDouble("shiftKan");
                        km = obj.getDouble("kM");
                    }
                }
            }
        }

//        // k1、k2、k3从宝信的表中采集
//        String tmpUrl = getUrl4(version);
//        queryParam.remove("cokeNo");
//        String tmpResult = httpUtil.get(tmpUrl, queryParam);
//        if (StringUtils.isNotBlank(tmpResult)) {
//            JSONArray arr = JSONObject.parseArray(tmpResult);
//            if (Objects.nonNull(arr) && arr.size() > 0) {
//                JSONObject obj = arr.getJSONObject(0);
//                if (Objects.nonNull(obj)) {
//                    k1 = obj.getDouble("k1");
//                    k2 = obj.getDouble("k2");
//                    k3 = obj.getDouble("k3");
//                }
//            }
//        }

        list.add(shiftkAn);
        list.add(daykAn);
        list.add(shiftkAvg);
        list.add(daykAvg);
        list.add(k1);
        list.add(k2);
        list.add(k3);
        list.add(km);
        for (int i = 0; i < list.size(); i++) {
            Double val = list.get(i);
            ExcelWriterUtil.addCellData(cellDataList, startRow, i, val);
        }
        return cellDataList;
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
        String dateTime = DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss");
        result.put("time", dateTime);
        return result;
    }

    protected Map<String, String> getQueryParam4(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        String dateTime = DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss");
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

    protected Map<String, String> getQueryParam2x(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate",DateUtil.getFormatDateTime(dateQuery.getStartTime(),"yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getEndTime(),"yyyy/MM/dd HH:mm:ss"));
        result.put("currentPage", "1");
        result.put("pageSize", "1");
        return result;
    }

    protected Map<String, String> getQueryParame3(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("date",DateUtil.getFormatDateTime(DateUtil.getDateBeginTime(dateQuery.getRecordDate()),"yyyy/MM/dd HH:mm:ss"));
        if(DateUtil.getFormatDateTime(dateQuery.getEndTime(),"HH").equals("08")){
            result.put("shift","1");
        }else if(DateUtil.getFormatDateTime(dateQuery.getEndTime(),"HH").equals("16")){
            result.put("shift","2");
        }else{
            result.put("shift","3");
        }
        return result;
    }

    protected Map<String, String> getQueryParam6(DateQuery dateQuery, String cokeNo) {
        Map<String, String> result = new HashMap<>();
        if (DateUtil.getFormatDateTime(dateQuery.getEndTime(), "HH").equals("08")) {
            result.put("shift", "1");
        } else if (DateUtil.getFormatDateTime(dateQuery.getEndTime(), "HH").equals("16")) {
            result.put("shift", "2");
        } else {
            result.put("shift", "3");
        }
        result.put("date", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd 00:00:00"));
        result.put("cokeNo", cokeNo);
        return result;
    }

    protected Map<String, String> getQueryParamMaxValue(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getEndTime(), -30), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam7(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(), -10), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected String getUrl1(String version) {
        return httpProperties.getJHUrlVersion(version) + "/coalBlendingStatus/getVauleByNameAndTime";
    }

    protected String getUrl2(String version) {
        return httpProperties.getJHUrlVersion(version)  + "/cokingStatementParameter/getByDateTime";
    }

    protected String getUrl3(String version) {
        return httpProperties.getJHUrlVersion(version)  + "/productionExecution/getCauseOfKCoefficientByDateTime";
    }

    protected String getUrl4(String version) {
        return httpProperties.getJHUrlVersion(version) + "/cokeActualPerformance/getCokeActuPerfByDateAndShift";
    }

    protected String getUrl5(String version) {
        return httpProperties.getJHUrlVersion(version) + "/analyses/getIfAnaitemValByCodeOrSource";
    }

    private String getUrl6(String version) {
        return httpProperties.getJHUrlVersion(version) + "/dayTemperatureStatistics/selectByDateAndShift";
    }

    protected String getUrl7(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getTagValue";
    }
}
