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
 * 主要工艺参数
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ZhuyaogycsWriter extends AbstractExcelReadWriter {

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
                if ("analysis".equals(sheetSplit[1])) {
                    for (int k = 0; k < columns.size(); k++) {
                        if (StringUtils.isNotBlank(columns.get(k))) {
                            String[] split = columns.get(k).split("/");
                            Double cellDataList = mapDataHandlerx(getUrlx(), split[0], split[1],dateQuery);
                            setSheetValue(sheet, rowIndex, k, cellDataList);
                        }
                    }
                }
                else if ("peimei".equals(sheetSplit[1])) {
                    Row row = sheet.createRow(1);
                    row.createCell(2).setCellValue(shift);
                    row.getCell(2).setCellType(CellType.STRING);
                    row.createCell(3).setCellValue(shiftDate);
                    row.getCell(3).setCellType(CellType.STRING);

                    List<CellData> cellDataList = mapDataHandler1(rowIndex, getUrl12(), columns, dateQuery);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else if ("crushing".equals(sheetSplit[1])) {
                    Double cellDataList = this.valHandler(getUrl4(), dateQuery);
                    setSheetValue(sheet, 1, 0, cellDataList);
                } else if ("actual".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = this.mapDataHandler2(getUrl5(), columns, 1, dateQuery, shiftNum);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else if ("yield".equals(sheetSplit[1])) {
                    Double cellDataList = this.mapDataHandler3(getUrl6(), dateQuery, shiftNum);
                    setSheetValue(sheet, 1, 0, cellDataList);
                } else if ("luwen".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = this.mapDataHandler4(getUrl7(), dateQuery, columns);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else if ("jtcl".equals(sheetSplit[1])) {
                    Date dateBeginTime = DateUtil.getDateBeginTime(dateQuery.getRecordDate());
                    int rowNum = 1;
                    for (int j = 1; j < 8; j++) {
                        Date date1 = DateUtil.addDays(dateBeginTime, j - 7);
                        Double cellDataList = this.mapDataHandler5(getUrl6(), date1);
                        setSheetValue(sheet, rowNum, 0, cellDataList);
                        rowNum++;
                    }
                } else if ("standard".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = this.mapDataHandler9(getUrl10(), columns, date, 1);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else if ("kstatis".equals(sheetSplit[1])) {
                    Date dateBeginTime = DateUtil.getDateBeginTime(dateQuery.getRecordDate());
                    int rowNum = 1;
                    for (int j = 1; j < 8; j++) {
                        Date start = DateUtil.addDays(dateBeginTime, j - 7);
                        Date end = DateUtil.addDays(dateBeginTime, j - 6);
                        List<CellData> cellDataList = this.mapDataHandler6(getUrl8(), columns, start, end, rowNum);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        rowNum++;
                    }
                } else if ("peimeicsnowed".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = this.mapDataHandler7(getUrl13(), sheet, 1);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }else if ("kankavg".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = this.mapDataHandler10(getUrl11(), dateQuery,shiftNum+"");
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else {
                    Date startTime = dateQuery.getStartTime();
                    Date endTime = dateQuery.getEndTime();
                    Long betweenMin = DateUtil.getBetweenMin(endTime, startTime);
                    for (int j = 0; j < betweenMin.intValue(); j++) {
                        Row row = sheet.createRow(j + 1);
                        String time = DateUtil.getFormatDateTime(DateUtil.addMinute(startTime, j), "yyyy/MM/dd HH:mm");
                        row.createCell(0).setCellValue(time);
                        row.getCell(0).setCellType(CellType.STRING);

                        String time1 = DateUtil.getFormatDateTime(DateUtil.addMinute(startTime, j), "yyyy/MM/dd HH:mm:ss");
                        Double cellDataList = this.mapDataHandler8(getUrl3(), time1);
                        setSheetValue(sheet, j + 1, 1, cellDataList);
                    }
                }
            }
        }
        return workbook;
    }

    protected Double mapDataHandler(String url, DateQuery dateQuery, String brandcode, String anaitemname) {
        Map<String, String> queryParam = getQueryParam(dateQuery, brandcode, anaitemname);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        Double data = jsonObject.getDouble("data");
        return data;
    }

    protected Double mapDataHandlerx(String url, String brandcode, String anaitemname,DateQuery dateQuery) {
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("brandcode", brandcode);
        queryParam.put("anaitemname", anaitemname);
        queryParam.put("unitCode", "JH67");
        queryParam.put("time", DateUtil.getFormatDateTime(dateQuery.getRecordDate(),"yyyy/MM/dd hh:mm:ss"));
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        Double data = jsonObject.getDouble("data");
        if ("48MM".equals(anaitemname)) {
            Map<String, String> queryParam1 = new HashMap<>();
            queryParam1.put("brandcode", brandcode);
            queryParam1.put("anaitemname", "LD40-60");
            Map<String, String> queryParam2 = new HashMap<>();
            queryParam2.put("brandcode", brandcode);
            queryParam2.put("anaitemname", "LD60-80");
            String result1 = httpUtil.get(url, queryParam1);
            String result2 = httpUtil.get(url, queryParam2);
            if (StringUtils.isBlank(result1) || StringUtils.isBlank(result2)) {
                return null;
            }
            JSONObject jsonObject1 = JSONObject.parseObject(result1);
            JSONObject jsonObject2 = JSONObject.parseObject(result2);
            data = jsonObject1.getDouble("data") + jsonObject2.getDouble("data");
        }
        return data;
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

    protected Double valHandler(String url, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam3(dateQuery);
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

    protected Double mapDataHandler3(String url, DateQuery dateQuery, int shiftNum) {
        Map<String, String> queryParam = getQueryParam5(dateQuery, shiftNum);
        String result = httpUtil.get(url, queryParam);
        Double ganxi = 0.0;
        if (StringUtils.isNotBlank(result)) {
            JSONArray array = JSONObject.parseArray(result);
            if (Objects.nonNull(array) && array.size() != 0) {
                JSONObject jsonObject = array.getJSONObject(0);
                if (Objects.nonNull(jsonObject)) {
                    Double backn5 = jsonObject.getDouble("backn5");
                    Double backn6 = jsonObject.getDouble("backn6");
                    ganxi = backn5 / (backn5 + backn6);
                }
            } else {
                Map<String, String> queryParam1 = getQueryParam5x();
                String result1 = httpUtil.get(getUrl6x(), queryParam1);
                if (StringUtils.isBlank(result1)) {
                    return null;
                }
                JSONArray array1 = JSONObject.parseArray(result1);
                JSONObject jsonObject1 = array1.getJSONObject(0);
                Double backn5x = jsonObject1.getDouble("backn5");
                Double backn6x = jsonObject1.getDouble("backn6");
                ganxi = backn5x / (backn5x + backn6x);
            }
        }
        return ganxi;
    }

    protected List<CellData> mapDataHandler4(String url, DateQuery dateQuery, List<String> columns) {
        Map<String, String> queryParam = getQueryParam6(dateQuery, 1, "CO6", 1);
        Map<String, String> queryParam1 = getQueryParam6(dateQuery, 1, "CO6", 2);
        Map<String, String> queryParam2 = getQueryParam6(dateQuery, 1, "CO7", 1);
        Map<String, String> queryParam3 = getQueryParam6(dateQuery, 1, "CO7", 2);
        String result = httpUtil.get(url, queryParam);
        String result1 = httpUtil.get(url, queryParam1);
        String result2 = httpUtil.get(url, queryParam2);
        String result3 = httpUtil.get(url, queryParam3);
        if (StringUtils.isBlank(result) || StringUtils.isBlank(result1) ||
                StringUtils.isBlank(result2) || StringUtils.isBlank(result3)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject jsonObject1 = JSONObject.parseObject(result1);
        JSONObject jsonObject2 = JSONObject.parseObject(result2);
        JSONObject jsonObject3 = JSONObject.parseObject(result3);
        if (Objects.isNull(jsonObject) || Objects.isNull(jsonObject1) ||
                Objects.isNull(jsonObject2) || Objects.isNull(jsonObject3)) {
            return null;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject data1 = jsonObject1.getJSONObject("data");
        JSONObject data2 = jsonObject2.getJSONObject("data");
        JSONObject data3 = jsonObject3.getJSONObject("data");
        if (Objects.isNull(data) || Objects.isNull(data1) ||
                Objects.isNull(data2) || Objects.isNull(data3)) {
            return null;
        }
        JSONArray arr = data.getJSONArray("TmmirbtmpDataTables");
        JSONArray arr1 = data1.getJSONArray("TmmirbtmpDataTables");
        JSONArray arr2 = data2.getJSONArray("TmmirbtmpDataTables");
        JSONArray arr3 = data3.getJSONArray("TmmirbtmpDataTables");
        if (Objects.isNull(arr) || Objects.isNull(arr1) ||
                Objects.isNull(arr2) || Objects.isNull(arr3)) {
            return null;
        }
        arr.addAll(arr1);
        arr2.addAll(arr3);
        Map<String, Object> map = new HashMap<>();
        Double CO61 = 0.0;
        int a = 0;
        Double CO62 = 0.0;
        int a1 = 0;
        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            Double wd = obj.getDouble("wd");
            int sidenum = obj.getIntValue("sidenum");
            if (sidenum == 1) {
                CO61 += wd;
                a++;
            } else if (sidenum == 2) {
                CO62 += wd;
                a1++;
            }
        }
        Double CO71 = 0.0;
        int a2 = 0;
        Double CO72 = 0.0;
        int a3 = 0;
        for (int i = 0; i < arr2.size(); i++) {
            JSONObject obj = arr2.getJSONObject(i);
            Double wd = obj.getDouble("wd");
            int sidenum = obj.getIntValue("sidenum");
            if (sidenum == 1) {
                CO71 += wd;
                a2++;
            } else if (sidenum == 2) {
                CO72 += wd;
                a3++;
            }
        }
        map.put("CO61", CO61 / a);
        map.put("CO62", CO62 / a1);
        map.put("CO71", CO71 / a2);
        map.put("CO72", CO72 / a3);

        List<CellData> cellData = ExcelWriterUtil.handlerRowData(columns, 1, map);
        return cellData;
    }

    protected List<CellData> mapDataHandler2(String url, List<String> columns, int rowBatch, DateQuery dateQuery, int shiftNum) {
        Map<String, String> queryParam = getQueryParam4(dateQuery, shiftNum);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONArray array = JSONObject.parseArray(result);
        if (Objects.isNull(array)) {
            return null;
        }
        JSONObject jsonObject = array.getJSONObject(0);
        Map<String, Object> innerMap = jsonObject.getInnerMap();
        List<CellData> cellData = ExcelWriterUtil.handlerRowData(columns, rowBatch, innerMap);
        return cellData;
    }

    protected Double mapDataHandler5(String url, Date date) {
        Double val = 0.0;
        for (int i = 1; i < 4; i++) {
            Map<String, String> queryParam = getQueryParam7(date, i);
            String result = httpUtil.get(url, queryParam);
            if (StringUtils.isNotBlank(result)) {
                JSONArray array = JSONObject.parseArray(result);
                if (Objects.nonNull(array) && array.size() != 0) {
                    JSONObject jsonObject = array.getJSONObject(0);
                    val += jsonObject.getDouble("confirmWgt");
                }
            }
        }

        return val;
    }

    public List<CellData> mapDataHandler9(String url, List<String> columns, DateQuery dateQuery, int startRow) {
        Map<String, String> queryParam = getQueryParame4(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject objects = JSONObject.parseObject(result);
        if (Objects.isNull(objects)) {
            return null;
        }
        return ExcelWriterUtil.handlerRowData(columns, startRow, objects);
    }

    protected Map<String, String> getQueryParame4(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected List<CellData> mapDataHandler6(String url, List<String> columns, Date start, Date end, int rowBatch) {
        Map<String, String> queryParam = getQueryParam8(start, end);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (Objects.isNull(jsonObject)) {
            return null;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }
        JSONObject yesterdayAvg = data.getJSONObject("yesterdayAvg");
        if (Objects.isNull(yesterdayAvg)) {
            return null;
        }
        Map<String, Object> innerMap = yesterdayAvg.getInnerMap();
        innerMap.put("date", DateUtil.getFormatDateTime(start, "yyyy/MM/dd"));
        List<CellData> cellData = ExcelWriterUtil.handlerRowData(columns, rowBatch, innerMap);
        return cellData;
    }

    protected List<CellData> mapDataHandler7(String url, Sheet sheet, int len) {
        List<CellData> cellDataList = new ArrayList<>();
        String result = httpUtil.get(url);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if(Objects.nonNull(jsonObject)){
                JSONArray rows = jsonObject.getJSONArray("rows");
                for (int i = 0; i < rows.size(); i++) {
                    JSONArray jsonArray = rows.getJSONArray(i);
                    for (int j = 0; j < jsonArray.size(); j++) {
                        JSONObject jsonObject1 =  jsonArray.getJSONObject(j);
                        String recTime = jsonObject1.getString("recTime");
                        String descr = jsonObject1.getString("descr");
                        Double planRate = jsonObject1.getDouble("planRate");
                        if(i==0){
                            ExcelWriterUtil.addCellData(cellDataList, 1, j, recTime);
                            ExcelWriterUtil.addCellData(cellDataList, 2, j, descr);
                            ExcelWriterUtil.addCellData(cellDataList, 3, j, planRate);
                        }else {
                            ExcelWriterUtil.addCellData(cellDataList, 4, j, recTime);
                            ExcelWriterUtil.addCellData(cellDataList, 5, j, descr);
                            ExcelWriterUtil.addCellData(cellDataList, 6, j, planRate);
                        }
                    }
                }
            }
        }
        return cellDataList;
    }


    public List<CellData> mapDataHandler10(String url, DateQuery dateQuery, String shift) {
        String[] coke = {"CO6", "CO7"};
        List<Double> list = new ArrayList<>();
        List<CellData> cellDataList = new ArrayList<>();
        Double k2 = 0.0;
        Double kAvg7 = 0.0;
        Double kAn7 = 0.0;
        Double kAvg6 = 0.0;
        Double kAn6 = 0.0;
        for (int i = 0; i < coke.length; i++) {
            String cokeNo = coke[i];
            Map<String, String> queryParam = getQueryParam10(dateQuery, shift, cokeNo);
            String result = httpUtil.get(url, queryParam);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (Objects.nonNull(jsonObject)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONObject obj = data.getJSONObject("DayTemperatureStatistics");
                        if (Objects.nonNull(obj)) {
                            if("CO6".equals(cokeNo)){
                                k2 = obj.getDouble("k3");
                                kAvg6=obj.getDouble("shiftKAvg");
                                kAn6=obj.getDouble("shiftKan");
                            }else if("CO7".equals(cokeNo)){
                                kAvg7=obj.getDouble("shiftKAvg");
                                kAn7=obj.getDouble("shiftKan");
                            }
                        }
                    }
                }
            }
        }
        list.add(k2);
        list.add(kAvg6);
        list.add(kAvg7);
        list.add(kAn6);
        list.add(kAn7);
        for (int i = 0; i < list.size(); i++) {
            Double val = list.get(i);
            ExcelWriterUtil.addCellData(cellDataList, 1, i, val);
        }
        return cellDataList;
    }

    protected Double mapDataHandler8(String url, String time) {
        Map<String, String> queryParam = getQueryParam2(time);
        queryParam.put("tagName", "CK67_L1R_CDQ_ARA_31101BHH2O_1m_avg");
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (Objects.isNull(jsonObject)) {
            return null;
        }
        JSONArray rows = jsonObject.getJSONArray("rows");
        if (Objects.isNull(rows) || rows.size() == 0) {
            return null;
        }
        JSONObject jsonObject1 = rows.getJSONObject(0);
        if (Objects.isNull(jsonObject1)) {
            return null;
        }
        Double val = jsonObject1.getDouble("val");
        return val;
    }


    protected Map<String, String> getQueryParam(DateQuery dateQuery, String brandcode, String anaitemname) {
        Map<String, String> result = new HashMap<>();
        result.put("brandcode", brandcode);
        result.put("starttime", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endtime", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("anaitemname", anaitemname);
        result.put("source", "三回收");
        return result;
    }


    protected Map<String, String> getQueryParam2(String date) {
        Map<String, String> result = new HashMap<>();
        result.put("time", date);
        return result;
    }

    protected Map<String, String> getQueryParam3(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam4(DateQuery dateQuery, int shiftNum) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getFormatDateTime(DateUtil.getDateBeginTime(dateQuery.getRecordDate()), "yyyy/MM/dd HH:mm:ss"));
        result.put("shift", shiftNum + "");
        return result;
    }

    protected Map<String, String> getQueryParam5(DateQuery dateQuery, int shiftNum) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd 00:00:00"));
        result.put("shift", shiftNum + "");
        result.put("code", "KH-Y");
        result.put("flag", "O");
        return result;
    }

    protected Map<String, String> getQueryParam5x() {
        Map<String, String> result = new HashMap<>();
        result.put("code", "KH-Y");
        result.put("flag", "O");
        return result;
    }

    protected Map<String, String> getQueryParam6(DateQuery dateQuery, int shiftNum, String cokeovenNo, int therMometryNum) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd 00:00:00"));
        result.put("shift", shiftNum + "");
        result.put("cokeovenNo", cokeovenNo);
        result.put("therMometryNum", therMometryNum + "");
        return result;
    }

    protected Map<String, String> getQueryParam7(Date date, int shiftNum) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(date, "yyyy/MM/dd 00:00:00"));
        result.put("shift", shiftNum + "");
        result.put("code", "KH-Y");
        result.put("flag", "O");
        return result;
    }

    protected Map<String, String> getQueryParam8(Date start, Date end) {
        Map<String, String> result = new HashMap<>();
        result.put("start", DateUtil.getFormatDateTime(start, "yyyy/MM/dd HH:mm:ss"));
        result.put("end", DateUtil.getFormatDateTime(end, "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam10(DateQuery dateQuery, String shift, String cokeNo) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd 00:00:00"));
        result.put("shift", shift);
        result.put("cokeNo", cokeNo);
        return result;
    }

    protected Map<String, String> getQueryParamX(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        return result;
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


    private String getUrlx() {
        return httpProperties.getUrlApiJHOne() + "/analyses/getIfAnaitemLatest";
    }

    private String getUrl3() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getVauleByNameAndTime";
    }

    private String getUrl4() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getParticleDistributionLatest";
    }

    private String getUrl5() {
        return httpProperties.getUrlApiJHOne() + "/cokeActualPerformance/getCokeActuPerfByDateAndShift";
    }

    private String getUrl6() {
        return httpProperties.getUrlApiJHOne() + "/cokingYieldAndNumberHoles/getCokeActuPerfByDateAndShiftAndCode";
    }

    private String getUrl6x() {
        return httpProperties.getUrlApiJHOne() + "/cokingYieldAndNumberHoles/getCokeActuPerfLastest";
    }

    private String getUrl7() {
        return httpProperties.getUrlApiJHOne() + "/tmmirbtmpDataTable/selectByDateAndShift";
    }

    private String getUrl8() {
        return httpProperties.getUrlApiJHOne() + "/cokeActualPerformance/getKlineStatistics";
    }

    protected String getUrl10() {
        return httpProperties.getUrlApiJHOne() + "/thermalRegulation/getCurrentByDate";
    }

    private String getUrl11() {
        return httpProperties.getUrlApiJHOne() + "/dayTemperatureStatistics/selectByDateAndShift";
    }

    protected String getUrl12() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getTagValue";
    }

    protected String getUrl13() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingParameter/getNowAndEd";
    }
}
