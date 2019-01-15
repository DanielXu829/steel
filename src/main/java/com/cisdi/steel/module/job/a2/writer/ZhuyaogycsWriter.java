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
import java.util.stream.Collectors;

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
                            Double cellDataList = mapDataHandler(getUrl(), dateQuery, split[0], split[1]);
                            setSheetValue(sheet, rowIndex, k, cellDataList);
                        }
                    }
                } else if ("peimei".equals(sheetSplit[1])) {
                    Row row = sheet.createRow(1);
                    row.createCell(2).setCellValue(shift);
                    row.getCell(2).setCellType(CellType.STRING);
                    row.createCell(3).setCellValue(shiftDate);
                    row.getCell(3).setCellType(CellType.STRING);

                    List<CellData> cellDataList = mapDataHandler1(rowIndex, getUrl2(), columns, dateQuery);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else if ("crushing".equals(sheetSplit[1])) {
                    Double cellDataList = this.valHandler(getUrl4(), dateQuery);
                    setSheetValue(sheet, 1, 0, cellDataList);
                } else if ("actual".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = this.mapDataHandler2(getUrl5(), columns, 1, dateQuery, shiftNum);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }else if("yield".equals(sheetSplit[1])){
                    Double cellDataList = this.mapDataHandler3(getUrl6(), dateQuery, shiftNum);
                    setSheetValue(sheet, 1, 0, cellDataList);
                }else if("luwen".equals(sheetSplit[1])){
                    List<CellData> cellDataList = this.mapDataHandler4(getUrl7(), dateQuery,columns,shiftNum);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
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
                                        Map<String, String> queryParam1 = getQueryParam2(DateUtil.getFormatDateTime(date, "yyyy/MM/dd HH:mm:00"));
                                        queryParam1.put("tagName", "CK67_L1R_CB_CBAcTol_1m_avg");
                                        String result1 = httpUtil.get(getUrl3(), queryParam1);
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

    public Double valHandler(String url, DateQuery dateQuery) {
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

    public Double mapDataHandler3(String url, DateQuery dateQuery,int shiftNum) {
        Map<String, String> queryParam = getQueryParam5(dateQuery,shiftNum);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONArray array = JSONObject.parseArray(result);
        if (Objects.isNull(array) || array.size()==0) {
            return null;
        }
        JSONObject jsonObject = array.getJSONObject(0);
        if (Objects.isNull(jsonObject)) {
            return null;
        }
        Double backn5 = jsonObject.getDouble("backn5");
        Double backn6 = jsonObject.getDouble("backn6");
        Double ganxi = backn5/(backn5+backn6);
        return ganxi;
    }

    public List<CellData> mapDataHandler4(String url, DateQuery dateQuery ,List<String> columns, int shiftNum) {
        Map<String, String> queryParam = getQueryParam6(dateQuery, 1,"CO6",1);
        Map<String, String> queryParam1 = getQueryParam6(dateQuery, 1,"CO6",2);
        Map<String, String> queryParam2 = getQueryParam6(dateQuery, 1,"CO7",2);
        Map<String, String> queryParam3 = getQueryParam6(dateQuery, 1,"CO7",2);
        String result = httpUtil.get(url, queryParam);
        String result1 = httpUtil.get(url, queryParam1);
        String result2 = httpUtil.get(url, queryParam2);
        String result3 = httpUtil.get(url, queryParam3);
        if (StringUtils.isBlank(result)||StringUtils.isBlank(result1)||
                StringUtils.isBlank(result2)||StringUtils.isBlank(result3)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject jsonObject1 = JSONObject.parseObject(result1);
        JSONObject jsonObject2 = JSONObject.parseObject(result2);
        JSONObject jsonObject3 = JSONObject.parseObject(result3);
        if(Objects.isNull(jsonObject)||Objects.isNull(jsonObject1)||
                Objects.isNull(jsonObject2)||Objects.isNull(jsonObject3)){
            return null;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject data1 = jsonObject1.getJSONObject("data");
        JSONObject data2 = jsonObject2.getJSONObject("data");
        JSONObject data3 = jsonObject3.getJSONObject("data");
        if(Objects.isNull(data)||Objects.isNull(data1)||
                Objects.isNull(data2)||Objects.isNull(data3)){
            return null;
        }
        JSONArray arr = data.getJSONArray("TmmirbtmpDataTables");
        JSONArray arr1 = data1.getJSONArray("TmmirbtmpDataTables");
        JSONArray arr2 = data2.getJSONArray("TmmirbtmpDataTables");
        JSONArray arr3 = data3.getJSONArray("TmmirbtmpDataTables");
        if(Objects.isNull(arr)||Objects.isNull(arr1)||Objects.isNull(arr2)||Objects.isNull(arr3)){
            return null;
        }
        arr.addAll(arr1);
        arr2.addAll(arr3);
        Map<String, Object> map = new HashMap<>();
        Double CO61=0.0;
        int a=0;
        Double CO62=0.0;
        int a1=0;
        for (int i = 0; i <arr.size() ; i++) {
            JSONObject obj = arr.getJSONObject(i);
            Double wd = obj.getDouble("wd");
            int sidenum = obj.getIntValue("sidenum");
            if(sidenum==1){
                CO61+=wd;
                a++;
            }else if(sidenum==2){
                CO62+=wd;
                a1++;
            }
        }
        Double CO71=0.0;
        int a2=0;
        Double CO72=0.0;
        int a3=0;
        for (int i = 0; i <arr2.size() ; i++) {
            JSONObject obj = arr2.getJSONObject(i);
            Double wd = obj.getDouble("wd");
            int sidenum = obj.getIntValue("sidenum");
            if(sidenum==1){
                CO71+=wd;
                a2++;
            }else if(sidenum==2){
                CO72+=wd;
                a3++;
            }
        }
        map.put("CO61",CO61/a);
        map.put("CO62",CO62/a1);
        map.put("CO71",CO71/a2);
        map.put("CO72",CO72/a3);

        List<CellData> cellData = ExcelWriterUtil.handlerRowData(columns, 1, map);
        return cellData;
    }


    public List<CellData> mapDataHandler2(String url, List<String> columns, int rowBatch, DateQuery dateQuery, int shiftNum) {
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

    protected Map<String, String> getQueryParam(DateQuery dateQuery, String brandcode, String anaitemname) {
        Map<String, String> result = new HashMap<>();
        result.put("brandcode", brandcode);
        result.put("starttime", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endtime", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("anaitemname", anaitemname);
        result.put("source", "三回收");
        return result;
    }

    protected Map<String, String> getQueryParam1(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));

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

    protected Map<String, String> getQueryParam5(DateQuery dateQuery,int shiftNum) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd 00:00:00"));
        result.put("shift", shiftNum+"");
        result.put("code", "KH-Y");
        result.put("flag", "O");
        return result;
    }

    protected Map<String, String> getQueryParam6(DateQuery dateQuery,int shiftNum,String cokeovenNo,int therMometryNum) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd 00:00:00"));
        result.put("shift", shiftNum+"");
        result.put("cokeovenNo", cokeovenNo);
        result.put("therMometryNum", therMometryNum+"");
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

    private String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/analyses/getIfAnaitemValByCodeOrSource";
    }

    private String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/manufacturingState/getTagValue";
    }

    private String getUrl3() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getVauleByNameAndTime";
    }

    private String getUrl4() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getParticleDistributionLatest";
    }

    protected String getUrl5() {
        return httpProperties.getUrlApiJHOne() + "/cokeActualPerformance/getCokeActuPerfByDateAndShift";
    }

    protected String getUrl6() {
        return httpProperties.getUrlApiJHOne() + "/cokingYieldAndNumberHoles/getCokeActuPerfByDateAndShiftAndCode";
    }
    protected String getUrl7() {
        return httpProperties.getUrlApiJHOne() + "/tmmirbtmpDataTable/selectByDateAndShift";
    }

}
