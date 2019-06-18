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
import java.util.*;

/**
 * 产耗综合报表
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
        String version = "67.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
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
                int size = dateQueries.size();
                if ("tagcha".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler(rowIndex, getUrl(version), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("tag".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler6(rowIndex, getUrl1(version), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("code".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler8(rowIndex, getUrl6(version), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("ther".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler7(rowIndex, getUrl4(version), item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("tagday0".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler2(rowIndex, getUrl1(version), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("taghe".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler1(rowIndex, getUrl(version), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("reval".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler3(rowIndex, getUrl2(version), item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("shizhong".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler4(getUrl5(version), columns, 1, item, rowIndex);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                }else if ("yield".equals(sheetSplit[1])) {
                    for (int k = 0; k < size; k++) {
                        DateQuery item = dateQueries.get(k);
                        int rowIndex = k + 1;
                        List<CellData> cellDataList = this.mapDataHandler5(rowIndex, getUrl3(version), item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler6(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam6(dateQuery);
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
                            JSONArray arr = data.getJSONArray(column);
                            if (Objects.nonNull(arr) && arr.size() != 0) {
                                JSONObject jsonObject1 = arr.getJSONObject(arr.size() - 1);
                                Double val = jsonObject1.getDouble("val");
                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                            }
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler8(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        int size = columns.size();
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Double val = 0.0;
            String column = columns.get(i);
            for (int j = 1; j <= 3; j++) {
                if (StringUtils.isNotBlank(column)) {
                    Map<String, String> queryParam = getQueryParam7(dateQuery, column, j);
                    String result = httpUtil.get(url, queryParam);
                    if (StringUtils.isNotBlank(result)) {
                        JSONArray array = JSONObject.parseArray(result);
                        if (Objects.nonNull(array) && array.size() > 0) {
                            JSONObject jsonObject = array.getJSONObject(0);
                            val += jsonObject.getDouble("confirmWgt");
                        }
                    }
                }
            }
            ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
        }
        return cellDataList;
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
                    if (Objects.nonNull(data)) {
                        JSONArray jsonArray = data.getJSONArray(column);
                        if (Objects.nonNull(jsonArray) && jsonArray.size() != 0) {
                            List<JSONObject> list = new ArrayList<>();
                            String time = "";
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject job = jsonArray.getJSONObject(i);
                                Double val = job.getDouble("val");
                                if (val > 0) {
                                    list.add(job);
                                    if ("".equals(time)) {
                                        time = job.getString("clock") + "~";
                                    } else {
                                        if (",".equals(time.substring(time.length() - 1))) {
                                            time = time + job.getString("clock") + "~";
                                        }
                                    }
                                } else {
                                    time = job.getString("clock") + ",";
                                    if (",".equals(time.substring(time.length() - 1))) {
                                        continue;
                                    }
                                }
                            }
                            Double val = 0.0;
                            for (int i = 0; i < list.size(); i++) {
                                JSONObject job = list.get(i);
                                val += job.getDouble("val");
                            }
                            JSONObject job = jsonArray.getJSONObject(jsonArray.size() - 1);
                            if ("~".equals(time.substring(time.length() - 1))) {
                                time = time + job.getString("clock");
                            }
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0, val / list.size());
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 1, time);
                        }
                    }

                }
            }

        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler3(Integer rowIndex, String url, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam3(dateQuery);
        List<CellData> cellDataList = new ArrayList<>();
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (Objects.nonNull(rows) && rows.size() != 0) {
                    JSONArray jsonArray = rows.getJSONArray(0);
                    if (Objects.nonNull(jsonArray) && jsonArray.size() != 0) {
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (Objects.nonNull(object)) {
                            BigDecimal cog = object.getBigDecimal("cogCalorificvalue");
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0, cog.intValue());
                        }
                    }
                }
            }
        }

        return cellDataList;
    }

    public List<CellData> mapDataHandler4(String url, List<String> columns, int rowBatch, DateQuery dateQuery, int startRow) {
        Map<String, String> queryParam = getQueryParame4(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONArray objects = JSONObject.parseArray(result);
        if (Objects.isNull(objects) || objects.size() == 0) {
            return null;
        }
        return handlerJsonArray(columns, rowBatch, objects, startRow);
    }


    protected List<CellData> mapDataHandler5(Integer rowIndex, String url, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam5(dateQuery);
        List<CellData> cellDataList = new ArrayList<>();
        String result = httpUtil.get(url, queryParam);
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonObject = JSONObject.parseObject(result);
            if(Objects.nonNull(jsonObject)){
                JSONObject data = jsonObject.getJSONObject("data");
                if(Objects.nonNull(data)){
                    Double currentYield = data.getDouble("currentYield");
                    ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0, currentYield);
                }
            }
        }
        return cellDataList;
    }


    protected List<CellData> mapDataHandler7(Integer rowIndex, String url, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam5(dateQuery);
        List<CellData> cellDataList = new ArrayList<>();
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                Double backn1 = jsonObject.getDouble("backn2");
                ExcelWriterUtil.addCellData(cellDataList, rowIndex, 0,backn1);
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

    protected Map<String, String> getQueryParame4(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getFormatDateTime(DateUtil.getDateBeginTime(dateQuery.getRecordDate()), "yyyy/MM/dd HH:mm:ss"));
        result.put("shift", "3");
        return result;
    }

    protected Map<String, String> getQueryParam3(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("datetime", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("currentPage", "1");
        result.put("pageSize", "1");
        return result;
    }


    protected Map<String, String> getQueryParam5(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }


    protected Map<String, String> getQueryParam6(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(), -10), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(), 10), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam7(DateQuery dateQuery, String column, int shiftNum) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        result.put("shift", shiftNum + "");
        String[] split = column.split("/");
        result.put("code", split[0]);
        result.put("flag", split[1]);
        return result;
    }

    protected String getUrl(String version) {
        return httpProperties.getJHUrlVersion(version) + "/coalBlendingStatus/getVauleByNameAndTime";
    }

    protected String getUrl1(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getTagValue";
    }

    protected String getUrl2(String version) {
        return httpProperties.getJHUrlVersion(version) + "/cokingStatementParameter/getByDateTime";
    }

    protected String getUrl3(String version) {
        return httpProperties.getJHUrlVersion(version) + "/cokingYieldAndNumberHoles/getYieldByDate";
    }

    protected String getUrl4(String version) {
        return httpProperties.getJHUrlVersion(version) + "/thermalRegulation/getCurrentByDate";
    }

    protected String getUrl5(String version) {
        return httpProperties.getJHUrlVersion(version) + "/cokeActualPerformance/getCokeActuPerfByDateAndShift";
    }

    protected String getUrl6(String version) {
        return httpProperties.getJHUrlVersion(version) + "/cokingYieldAndNumberHoles/getCokeActuPerfByDateAndShiftAndCode";
    }
}
