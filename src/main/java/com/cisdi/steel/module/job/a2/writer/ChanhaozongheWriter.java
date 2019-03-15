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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
public class ChanhaozongheWriter extends AbstractExcelReadWriter {
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
                if ("tagcha".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler(rowIndex, getUrl(), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("tagday0".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler2(rowIndex, getUrl1(), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("taghe".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler1(rowIndex, getUrl(), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("reval".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler3(rowIndex, getUrl2(), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("shizhong".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler4(rowIndex, getUrl1(), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam(dateQuery);
        Map<String, String> queryParam1 = getQueryParam1(dateQuery);
        Map<String, String> queryParam2 = getQueryParam(dateQuery);
        Map<String, String> queryParam3 = getQueryParam1(dateQuery);
        int size = columns.size();
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            if (StringUtils.isNotBlank(column)) {
                String[] split = column.split(",");
                if (split.length == 1) {
                    queryParam.put("tagName", split[0]);
                    queryParam1.put("tagName", split[0]);
                    String result = httpUtil.get(url, queryParam);
                    String result1 = httpUtil.get(url, queryParam1);
                    if (StringUtils.isNotBlank(result) && StringUtils.isNotBlank(result1)) {
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        JSONObject jsonObject1 = JSONObject.parseObject(result1);
                        if (Objects.nonNull(jsonObject) && Objects.nonNull(jsonObject1)) {
                            JSONArray arr = jsonObject.getJSONArray("rows");
                            JSONArray arr1 = jsonObject1.getJSONArray("rows");
                            if (Objects.nonNull(arr) && arr.size() != 0
                                    && Objects.nonNull(arr1) && arr1.size() != 0) {
                                JSONObject job = arr.getJSONObject(0);
                                JSONObject job1 = arr1.getJSONObject(0);
                                if (Objects.isNull(job) || Objects.isNull(job1)) {
                                    continue;
                                }
                                double val = job1.getDouble("val") - job.getDouble("val");
                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                            }
                        }
                    }
                } else {
                    queryParam.put("tagName", split[0]);
                    queryParam1.put("tagName", split[0]);
                    queryParam2.put("tagName", split[1]);
                    queryParam3.put("tagName", split[1]);
                    String result = httpUtil.get(url, queryParam);
                    String result1 = httpUtil.get(url, queryParam1);
                    String result2 = httpUtil.get(url, queryParam2);
                    String result3 = httpUtil.get(url, queryParam3);
                    if (StringUtils.isNotBlank(result) && StringUtils.isNotBlank(result1)
                            && StringUtils.isNotBlank(result2) && StringUtils.isNotBlank(result3)) {
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        JSONObject jsonObject1 = JSONObject.parseObject(result1);
                        JSONObject jsonObject2 = JSONObject.parseObject(result2);
                        JSONObject jsonObject3 = JSONObject.parseObject(result3);
                        if (Objects.nonNull(jsonObject) && Objects.nonNull(jsonObject1)
                                && Objects.nonNull(jsonObject2) && Objects.nonNull(jsonObject3)) {
                            JSONArray arr = jsonObject.getJSONArray("rows");
                            JSONArray arr1 = jsonObject1.getJSONArray("rows");
                            JSONArray arr2 = jsonObject2.getJSONArray("rows");
                            JSONArray arr3 = jsonObject3.getJSONArray("rows");
                            if (Objects.nonNull(arr) && arr.size() != 0
                                    && Objects.nonNull(arr1) && arr1.size() != 0
                                    && Objects.nonNull(arr2) && arr2.size() != 0
                                    && Objects.nonNull(arr3) && arr3.size() != 0) {
                                JSONObject job = arr.getJSONObject(0);
                                JSONObject job1 = arr1.getJSONObject(0);
                                JSONObject job2 = arr2.getJSONObject(0);
                                JSONObject job3 = arr3.getJSONObject(0);
                                if (Objects.isNull(job) || Objects.isNull(job1)
                                        || Objects.isNull(job2) || Objects.isNull(job3)) {
                                    continue;
                                }
                                double val = job1.getDouble("val") + job3.getDouble("val")
                                        - job.getDouble("val") - job2.getDouble("val");
                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                            }
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler1(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam1(dateQuery);
        Map<String, String> queryParam1 = getQueryParam1(dateQuery);
        Map<String, String> queryParam2 = getQueryParam1(dateQuery);
        Map<String, String> queryParam3 = getQueryParam1(dateQuery);
        int size = columns.size();
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            if (StringUtils.isNotBlank(column)) {
                String[] split = column.split(",");
                if (split.length == 2) {
                    queryParam.put("tagName", split[0]);
                    queryParam1.put("tagName", split[1]);
                    String result = httpUtil.get(url, queryParam);
                    String result1 = httpUtil.get(url, queryParam1);
                    if (StringUtils.isNotBlank(result) && StringUtils.isNotBlank(result1)) {
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        JSONObject jsonObject1 = JSONObject.parseObject(result1);
                        if (Objects.nonNull(jsonObject) && Objects.nonNull(jsonObject1)) {
                            JSONArray arr = jsonObject.getJSONArray("rows");
                            JSONArray arr1 = jsonObject1.getJSONArray("rows");
                            if (Objects.nonNull(arr) && arr.size() != 0
                                    && Objects.nonNull(arr1) && arr1.size() != 0) {
                                JSONObject job = arr.getJSONObject(0);
                                JSONObject job1 = arr1.getJSONObject(0);
                                if (Objects.isNull(job) || Objects.isNull(job1)) {
                                    continue;
                                }
                                double val = job1.getDouble("val") + job.getDouble("val");
                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                            }
                        }
                    }
                } else if (split.length == 4) {
                    queryParam.put("tagName", split[0]);
                    queryParam1.put("tagName", split[1]);
                    queryParam2.put("tagName", split[2]);
                    queryParam3.put("tagName", split[3]);
                    String result = httpUtil.get(url, queryParam);
                    String result1 = httpUtil.get(url, queryParam1);
                    String result2 = httpUtil.get(url, queryParam2);
                    String result3 = httpUtil.get(url, queryParam3);
                    if (StringUtils.isNotBlank(result) && StringUtils.isNotBlank(result1)
                            && StringUtils.isNotBlank(result2) && StringUtils.isNotBlank(result3)) {
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        JSONObject jsonObject1 = JSONObject.parseObject(result1);
                        JSONObject jsonObject2 = JSONObject.parseObject(result2);
                        JSONObject jsonObject3 = JSONObject.parseObject(result3);
                        if (Objects.nonNull(jsonObject) && Objects.nonNull(jsonObject1)
                                && Objects.nonNull(jsonObject2) && Objects.nonNull(jsonObject3)) {
                            JSONArray arr = jsonObject.getJSONArray("rows");
                            JSONArray arr1 = jsonObject1.getJSONArray("rows");
                            JSONArray arr2 = jsonObject2.getJSONArray("rows");
                            JSONArray arr3 = jsonObject3.getJSONArray("rows");
                            if (Objects.nonNull(arr) && arr.size() != 0
                                    && Objects.nonNull(arr1) && arr1.size() != 0
                                    && Objects.nonNull(arr2) && arr2.size() != 0
                                    && Objects.nonNull(arr3) && arr3.size() != 0) {
                                JSONObject job = arr.getJSONObject(0);
                                JSONObject job1 = arr1.getJSONObject(0);
                                JSONObject job2 = arr2.getJSONObject(0);
                                JSONObject job3 = arr3.getJSONObject(0);
                                if (Objects.isNull(job) || Objects.isNull(job1)
                                        || Objects.isNull(job2) || Objects.isNull(job3)) {
                                    continue;
                                }
                                double val = job1.getDouble("val") + job.getDouble("val")
                                        + job2.getDouble("val") + job3.getDouble("val");
                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                            }
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler2(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam2(dateQuery);
        List<CellData> cellDataList = new ArrayList<>();
        String column = columns.get(0);
        if (StringUtils.isNotBlank(column)) {
            queryParam.put("tagNames", column);
            String result = httpUtil.get(url, queryParam);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (Objects.nonNull(jsonObject)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if(Objects.nonNull(data)){
                        JSONArray jsonArray = data.getJSONArray(column);
                        if (Objects.nonNull(jsonArray) && jsonArray.size() != 0) {
                            List<JSONObject> list=new ArrayList<>();
                            String time="";
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject job = jsonArray.getJSONObject(i);
                                Double val = job.getDouble("val");
                                if(val>0){
                                    list.add(job);
                                    if("".equals(time)){
                                        time=job.getString("clock")+"~";
                                    }else {
                                        if(",".equals(time.substring(time.length()-1))){
                                            time=time+job.getString("clock")+"~";
                                        }
                                    }
                                }else {
                                    time=job.getString("clock")+",";
                                    if(",".equals(time.substring(time.length()-1))){
                                        continue;
                                    }
                                }
                            }
                            Double val=0.0;
                            for (int i = 0; i < list.size(); i++) {
                                JSONObject job = list.get(i);
                                val+=job.getDouble("val");
                            }
                            JSONObject job = jsonArray.getJSONObject(jsonArray.size()-1);
                            if("~".equals(time.substring(time.length()-1))){
                                time=time+job.getString("clock");
                            }
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0, val/list.size());
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 1, time);
                        }
                    }

                }
            }

        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler3(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam3(dateQuery);
        List<CellData> cellDataList = new ArrayList<>();
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (Objects.nonNull(rows) && rows.size() != 0) {
                    JSONArray jsonArray = rows.getJSONArray(0);
                    if(Objects.nonNull(jsonArray) && jsonArray.size() != 0){
                        JSONObject object = jsonArray.getJSONObject(0);
                        if(Objects.nonNull(object)){
                            BigDecimal cog = object.getBigDecimal("cogCalorificvalue");
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0, cog.intValue());
                        }
                    }
                }
            }
        }

        return cellDataList;
    }

    protected List<CellData> mapDataHandler4(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam2(dateQuery);
        List<CellData> cellDataList = new ArrayList<>();
        String column = columns.get(0);
        queryParam.put("tagNames", column);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    JSONArray jsonArray = data.getJSONArray(column);
                    if (Objects.nonNull(jsonArray) && jsonArray.size() != 0) {
                        Double val=0.0;
                        for (int i = 0; i <jsonArray.size() ; i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String clock = obj.getString("clock");
                            Date date = DateUtil.strToDate(clock, DateUtil.fullFormat);
                            Map<String, String> queryParam4 = getQueryParam4(date);
                            String s = httpUtil.get(getUrl(), queryParam4);
                            if(StringUtils.isNotBlank(s)){
                                JSONObject jsonObject1 = JSONObject.parseObject(s);
                                if (Objects.nonNull(jsonObject1)) {
                                    JSONArray arr1 = jsonObject1.getJSONArray("rows");
                                    if (Objects.nonNull(arr1) && arr1.size() != 0) {
                                        JSONObject job1 = arr1.getJSONObject(0);
                                        if (Objects.isNull(job1)) {
                                            continue;
                                        }
                                        val += job1.getDouble("val");
                                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0, val);
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


    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("time", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam1(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("time", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam3(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("datetime", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("currentPage", "1");
        result.put("pageSize", "1");
        return result;
    }

    protected Map<String, String> getQueryParam4(Date date) {
        Map<String, String> result = new HashMap<>();
        result.put("time", DateUtil.getFormatDateTime(date, "yyyy/MM/dd HH:mm:00"));
        result.put("tagName", "CK67_L1R_CB_CBAmtTol_1m_max");
        return result;
    }

    protected String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getVauleByNameAndTime";
    }

    protected String getUrl1() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getTagValue";
    }

    protected String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/cokingStatementParameter/getByDateTime";
    }
}
