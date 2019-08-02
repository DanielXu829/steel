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
@SuppressWarnings("ALL")
@Component
public class GuanjianzhibiaoWriter extends AbstractExcelReadWriter {
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
                int size = dateQueries.size();
                if ("crushing".equals(sheetSplit[1])) {
                    for (int j = 0; j < size; j++) {
                        int rowIndex = 1 + j;
                        Double cellDataList = this.valHandler(getUrl(version), dateQueries.get(j));
                        setSheetValue(sheet, rowIndex, 0, cellDataList);
                    }
                } else if ("lianjiao".equals(sheetSplit[1])) {
                    for (int j = 0; j < size; j++) {
                        int rowIndex = 1 + j;
                        List<CellData> cellData = this.valHandler1(getUrl6(version), rowIndex, dateQueries.get(j),version);
                        ExcelWriterUtil.setCellValue(sheet, cellData);
                    }
                } else if ("tag".equals(sheetSplit[1])) {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl3(version), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("analysis".equals(sheetSplit[1])) {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = mapDataHandler8(rowIndex, columns, item, version);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = mapDataHandler1(rowIndex, getUrl7(version), columns, item);
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
                    if (Objects.nonNull(arr) && arr.size() > 0) {
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

    public List<CellData> valHandler1(String url, int rowIndex, DateQuery dateQuery,String version) {
        String[] shifts = {1 + "", 2 + "", 3 + ""};
        String[] coke = {"CO6", "CO7"};
        String jhNo="CO6";
        if("12.0".equals(version)){
            coke[0] = "CO1";
            coke[1] = "CO2";
            jhNo="CO1";
        }else if("45.0".equals(version)){
            coke[0] = "CO4";
            coke[1] = "CO5";
            jhNo="CO4";
        }
        List<Double> list = new ArrayList<>();
        List<CellData> cellDataList = new ArrayList<>();
        Double k2 = 0.0;
        Double k1 = 0.0;
        Double k3 = 0.0;
        Double km = 0.0;
        Double kAvg = 0.0;
        Double kAn = 0.0;
        for (int i = 0; i < coke.length; i++) {
            String cokeNo = coke[i];
            Map<String, String> queryParam = getQueryParam4(dateQuery, 3+"", cokeNo);
            String result = httpUtil.get(url, queryParam);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (Objects.nonNull(jsonObject)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONObject obj = data.getJSONObject("DayTemperatureStatistics");
                        if (Objects.nonNull(obj)) {
                            if(kAvg==0){
                                kAvg = obj.getDouble("dayKAvg");
                            }else {
                                if(kAvg<obj.getDouble("dayKAvg")){
                                    kAvg = obj.getDouble("dayKAvg");
                                }
                            }
                            if(kAn==0){
                                kAn = obj.getDouble("dayKan");
                            }else {
                                if(kAn<obj.getDouble("dayKan")){
                                    kAn = obj.getDouble("dayKan");
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i <shifts.length ; i++) {
            String shift = shifts[i];
//            // k1、k2、k3从宝信的表中采集
//            String tmpUrl = getUrl5(version);
//            Map<String, String> tmpQueryParam = getQueryParam4(dateQuery, shift, null);
//            tmpQueryParam.remove("cokeNo");
//            String tmpResult = httpUtil.get(tmpUrl, tmpQueryParam);
//            if (StringUtils.isNotBlank(tmpResult)) {
//                JSONArray arr = JSONObject.parseArray(tmpResult);
//                if (Objects.nonNull(arr) && arr.size() > 0) {
//                    JSONObject obj = arr.getJSONObject(0);
//                    if (Objects.nonNull(obj)) {
//                        k1 += obj.getDouble("k1");
//                        k2 += obj.getDouble("k2");
//                        k3 += obj.getDouble("k3");
//                    }
//                }
//            }

            Map<String, String> queryParam = getQueryParam4(dateQuery, shift, jhNo);
            String result = httpUtil.get(url, queryParam);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (Objects.nonNull(jsonObject)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONObject obj = data.getJSONObject("DayTemperatureStatistics");
                        if (Objects.nonNull(obj)) {
                            k2 += obj.getDouble("k2");
                            k1 += obj.getDouble("k1");
                            k3 += obj.getDouble("k3");
                            km += obj.getDouble("kM");
                        }
                    }
                }
            }
        }
        list.add(k1/3);
        list.add(k2/3);
        list.add(k3/3);
        list.add(km/3);
        list.add(kAvg);
        list.add(kAn);
        for (int i = 0; i <list.size() ; i++) {
            Double val = list.get(i);
            ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler8(int rowIndex, List<String> columns,DateQuery dateQuery,String version) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = columns.size();
        for (int k = 0; k < size; k++) {
            String colName = columns.get(k);
            if (StringUtils.isNotBlank(colName)) {
                String[] split = colName.split("/");
                String url = getUrl8(version);
                Map<String, String> queryParam = getQueryParam8(dateQuery,split[0],split[1]);
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isBlank(result)) {
                    return null;
                }
                JSONObject jsonObject = JSONObject.parseObject(result);
                Double data = jsonObject.getDouble("data");
                ExcelWriterUtil.addCellData(cellDataList, rowIndex, k, data);
            }
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
        result.put("start", DateUtil.getFormatDateTime(DateUtil.addHours(dateQuery.getStartTime(), -8), "yyyy/MM/dd HH:mm:ss"));
        result.put("end", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("type", "avg");
        return result;
    }

    protected Map<String, String> getQueryParam4(DateQuery dateQuery, String shift, String cokeNo) {
        Map<String, String> result = new HashMap<>();
        Date date = DateUtil.addDays(dateQuery.getRecordDate(), -1);
        result.put("date", DateUtil.getFormatDateTime(date, "yyyy/MM/dd 00:00:00"));
        result.put("shift", shift);
        result.put("cokeNo", cokeNo);
        return result;
    }

    protected Map<String, String> getQueryParamX(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addHours(dateQuery.getRecordDate(), -8), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam8(DateQuery dateQuery, String brandcode, String anaitemname) {
        Map<String, String> result = new HashMap<>();
        result.put("starttime",DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endtime",DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("brandcode", brandcode);
        result.put("anaitemname", anaitemname);
        return result;
    }

    private String getUrl(String version) {
        return httpProperties.getJHUrlVersion(version) + "/coalBlendingStatus/getParticleDistributionLatest";
    }

    private String getUrl3(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getTagValueStatisticType";
    }

    private String getUrl5(String version) {
        return httpProperties.getJHUrlVersion(version) + "/cokeActualPerformance/getCokeActuPerfByDateAndShift";
    }

    private String getUrl6(String version) {
        return httpProperties.getJHUrlVersion(version) + "/dayTemperatureStatistics/selectByDateAndShift";
    }

    protected String getUrl7(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getTagValue";
    }

    protected String getUrl8(String version) {
        return httpProperties.getJHUrlVersion(version) + "/analyses/getAnaitemValAvg";
    }

}
