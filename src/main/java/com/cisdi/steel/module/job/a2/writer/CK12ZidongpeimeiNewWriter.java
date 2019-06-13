package com.cisdi.steel.module.job.a2.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.dto.DateSegment;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 自动配煤
 */
@SuppressWarnings("ALL")
@Component
@Slf4j
public class CK12ZidongpeimeiNewWriter extends AbstractExcelReadWriter {

    private static final String CBShiftRunTime = "CBShiftRunTime";
    private static final String CBShiftAmt = "CBShiftAmt";
    private static final String CBAcTol = "CBAcTol";
    private static final int workStartTimeoffset = -60;
    private static final int workHours = 12;
    private Date shiftStart;
    private Date shiftEnd;
    private String version;

    private static final Map<String, String> pd2mtTagNames = new HashMap<>();
    private static final Map<String, Integer> lz2pd = new HashMap<>();
    private final Map<String, List<DateSegment>> pdRunSegments = new HashMap<>();

    static{
        int[] piDai = {214,215,216,217};
        short[] meiTa =  {1,2};
        String pd2mtTagName = "CK12_L1R_CC_B%dto%dCT_1m_avg";
        for (int pd : piDai) {
            for (short mt : meiTa) {
                pd2mtTagNames.put(pd+"-"+mt, String.format(pd2mtTagName,pd,mt));
            }
        }

        int[] liaoCang = {1,2,3,4,5,6,7,8,9,10,11,12};
        int[] liaozui = {1,2,3,4};
        String lzAmtTagName = "CK12_L1R_CB_%d_%dCBShiftAmt_1m_avg";
        String lzRunTimeTagName = "CK12_L1R_CB_%d_%dCBShiftRunTime_1m_avg";
        for (int lc : liaoCang) {
            for (int lz : liaozui) {
                if (isOdd(lc)) {
                    if(isOdd(lz)){
                        lz2pd.put(String.format(lzAmtTagName,lc,lz), 216);
                        lz2pd.put(String.format(lzRunTimeTagName,lc,lz), 216);
                    }else{
                        lz2pd.put(String.format(lzAmtTagName,lc,lz), 217);
                        lz2pd.put(String.format(lzRunTimeTagName,lc,lz), 217);
                    }
                }else {
                    if(isOdd(lz)){
                        lz2pd.put(String.format(lzAmtTagName,lc,lz), 214);
                        lz2pd.put(String.format(lzRunTimeTagName,lc,lz), 214);
                    }else{
                        lz2pd.put(String.format(lzAmtTagName,lc,lz), 215);
                        lz2pd.put(String.format(lzRunTimeTagName,lc,lz), 215);
                    }
                }
            }
        }
    }

    /**
     * 是奇数
     * @param a
     * @return
     */
    private static boolean isOdd(int a){
        return (a & 1) == 1;
    }

    public Double compareTagVal(String tagName, DateQuery dateQuery,String version) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tagName", tagName);
        String time = DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:00");
//        map.put("startDate", time);
//        map.put("endDate", time);
        String s = httpUtil.get(getUrl3(version), map);
        Double val = null;
        if (StringUtils.isNotBlank(s)) {
            JSONObject jsonObject = JSONObject.parseObject(s);
            if (Objects.nonNull(jsonObject)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (Objects.nonNull(rows) && rows.size() > 0) {
                    JSONObject tagValue = rows.getJSONObject(0);
                    val = tagValue.getDouble("val");
                }
            }

        }
        return val;
    }

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);

        try{
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        }catch(Exception e){
        }

        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                String name = sheetSplit[1];
                // 自动配煤
                if ("auto".equals(name)) {
                    String shift = "";
                    Integer shiftNum = 0;
                    Date todayBeginTime = DateUtil.getTodayBeginTime();
                    Date workStart = DateUtil.addMinute(todayBeginTime, workStartTimeoffset);
                    Date date1 = DateUtil.addHours(workStart, workHours);
                    Date date2 = DateUtil.addHours(date1, workHours);
                    if (DateUtil.isEffectiveDate(date.getRecordDate(), workStart, date1)) {
                        shift = "夜班";
                        shiftNum = 1;
                        shiftStart = workStart;
                        shiftEnd = date1;
                    } else if (DateUtil.isEffectiveDate(date.getRecordDate(), date1, date2)) {
                        shift = "白班";
                        shiftNum = 2;
                        shiftStart = date1;
                        shiftEnd = date2;
                    }
//                    //测试时的偏移，发布时要去掉
//                    int offsetHours = -23;
//                    date.setRecordDate(DateUtil.addHours(date.getRecordDate(), offsetHours));
//                    shiftStart = DateUtil.addHours(shiftStart, offsetHours);
//                    shiftEnd = DateUtil.addHours(shiftEnd, offsetHours);

                    if(pdRunSegments.isEmpty()){
                        pdRunSegments.putAll(getDateSegment());
                    }

                    int rowIndex = 50;
                    Row row = sheet.createRow(rowIndex);
                    row.createCell(0).setCellValue(shift);
                    row.getCell(0).setCellType(CellType.STRING);

                    Double yiedNum = this.mapDataHandler1(shiftNum, getUrl4(version), date);
                    Row row2 = sheet.createRow(++rowIndex);
                    row2.createCell(0).setCellValue(yiedNum);
                    row2.getCell(0).setCellType(CellType.NUMERIC);

                    Integer[] shiftArr = {1, 2};
                    Double yiedNumLeiji = this.mapDataHandler2(shiftArr, getUrl4(version), date);
                    Row row3 = sheet.createRow(++rowIndex);
                    row3.createCell(0).setCellValue(yiedNumLeiji);
                    row3.getCell(0).setCellType(CellType.NUMERIC);
                    List<CellData> cellDataList = this.handlerData(date, sheet);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    public Double peimeiLeiji(String url, DateQuery dateQuery) {
        Date startTime = DateQueryUtil.getMonthStartTime(dateQuery.getRecordDate());
        dateQuery.setStartTime(startTime);
        Map<String, String> queryParam = getQueryParamX1(dateQuery);
        List<CellData> cellDataList = new ArrayList<>();
        String tagName = "CK67_L1R_CB_CBAmtTol_1m_evt";
        queryParam.put("tagNames", tagName);
        String result = httpUtil.get(url, queryParam);
        Double val = 0.0;
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    JSONArray arr = data.getJSONArray(tagName);
                    if (Objects.nonNull(arr) && arr.size() != 0) {
                        for (int i = 0; i < arr.size(); i++) {
                            JSONObject jsonObject1 = arr.getJSONObject(i);
                            if (jsonObject1.getDouble("val") > 3) {
                                val += jsonObject1.getDouble("val");
                            }
                        }
                    }
                }
            }
        }
        return val;
    }

    private List<CellData> handlerData(DateQuery dateQuery, Sheet sheet) {
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        List<CellData> cellDataList = new ArrayList<>();
        short meita = 1;
        short lineCount = 0;
        for (int rowIndex = firstRowNum; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (Objects.nonNull(row)) {
                boolean nextmeita = false;
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int i = firstCellNum; i < lastCellNum; i++) {
                    Cell cell = row.getCell(i);
                    String cellValue = PoiCellUtil.getCellValue(cell);
                    if (StringUtils.isNotBlank(cellValue)) {
                        if(cellValue.contains(CBShiftRunTime)){
                            handlerBySegment(cellDataList, rowIndex, i, cellValue, meita);
                            continue;
                        }else if(cellValue.contains(CBShiftAmt)){
                            handlerBySegment(cellDataList, rowIndex, i, cellValue, meita);
                            nextmeita = true;
                            continue;
                        }

                        Map<String, String> map = getQueryParam(dateQuery);
                        map.put("tagNames", cellValue);
                        JSONObject data = getTagValues(map);
                        if(null != data){
                            JSONArray arr = data.getJSONArray(cellValue);
                            if (Objects.nonNull(arr) && arr.size() != 0) {
                                double val = 0;
                                for (int m = 0; m< arr.size()-1; m++) {
                                    JSONObject o = arr.getJSONObject(m);
                                    if(o.getDouble("val")>val){
                                        val = o.getDouble("val");
                                    }
                                }

                                Integer rowa = cell.getRowIndex() + 1;
                                Integer col = cell.getColumnIndex();
                                ExcelWriterUtil.addCellData(cellDataList, rowa, col, val);
                            }
                        }
                    }

                }
                if(nextmeita){
                    lineCount++;
                }
                if(lineCount == 2){
                    meita = 2;
                }
                ExcelWriterUtil.getRowOrCreate(sheet, rowIndex);
            }
        }
        return cellDataList;
    }

    /**
     * 查询tag值
     * @param map
     * @return
     */
    private JSONObject getTagValues(Map<String, String > map){
        String result = httpUtil.get(getUrl(version), map);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    return data;
                }
            }
        }
        return null;
    }

    private void handlerBySegment(List<CellData> cellDataList,int row,int col,String tagName, short meita){
        double sum = 0;
        int pidai = lz2pd.get(tagName);
        List<DateSegment> segments = pdRunSegments.get(pidai+"-"+meita);
        for (DateSegment segment : segments) {
            Map<String, String> map = getQueryParamBySegment(segment);
            map.put("tagNames", tagName);
            JSONObject data = getTagValues(map);
            if(null != data){
                JSONArray arr = data.getJSONArray(tagName);
                if (Objects.nonNull(arr) && arr.size() >1) {
                    double start = arr.getJSONObject(0).getDouble("val");
                    double end1 = arr.getJSONObject(arr.size()-1).getDouble("val");
                    double end2 = arr.getJSONObject(arr.size()-2).getDouble("val");
                    double end = Math.max(end1,end2);
                    sum += end-start;
                }
            }
        }
        if(sum != 0){
            cellDataList.add(new CellData(row+1, col, sum));
        }
    }

    /**
     * 检索所有皮带工作时间段
     * @return
     */
    private Map<String, List<DateSegment>> getDateSegment(){
        Map<String, List<DateSegment>> map = new HashMap<>();
        for (Map.Entry<String, String> kv : pd2mtTagNames.entrySet()) {
            map.put(kv.getKey(), getDateSegment(kv.getValue()));
        }
        return map;
    }

    /**
     * 检索皮带工作时间段
     * @param tagName
     * @return
     */
    private List<DateSegment> getDateSegment(String tagName){
        List<DateSegment> list = new ArrayList<>();
        Map<String, String> map = getQueryParamByShift();
        map.put("tagNames", tagName);
        JSONObject data = getTagValues(map);
        if(null != data){
            JSONArray arr = data.getJSONArray(tagName);
            if (Objects.nonNull(arr) && arr.size() != 0) {
                boolean isNew = true;
                Date startDate = null;
                for (int i = 0; i<= arr.size()-1; i++) {
                    JSONObject o = arr.getJSONObject(i);
                    if(isNew && (o.getDouble("val")>0.1)){
                        startDate = o.getDate("clock");
                        isNew = false;
                    }else if((!isNew) && (o.getDouble("val")<0.1)){
                        list.add(new DateSegment(startDate, arr.getJSONObject(i-1).getDate("clock")));
                        startDate = null;
                        isNew = true;
                    }
                }
                if(null != startDate){
                    list.add(new DateSegment(startDate, arr.getJSONObject(arr.size()-1).getDate("clock")));
                }
            }
        }
        return list;
    }

    public List<CellData> handlerDataX(String url, List<String> columns, DateQuery date) {
        Map<String, String> queryParam = getQueryParamX(date);
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            String tagName = columns.get(i);
            queryParam.put("tagNames", tagName);
            String result = httpUtil.get(url, queryParam);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (Objects.nonNull(jsonObject)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONArray arr = data.getJSONArray(tagName);
                        if (Objects.nonNull(arr) && arr.size() != 0) {
                            JSONObject jsonObject1 = arr.getJSONObject(0);
                            Double val = jsonObject1.getDouble("val");
                            ExcelWriterUtil.addCellData(cellDataList, 1, i, val);
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler(String url, List<String> columns, int rowBatch, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam2(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }
        int startRow = 1;
        return ExcelWriterUtil.handlerRowData(columns, startRow, data);
    }

    protected Double mapDataHandler1(Integer shiftNum, String url, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam3(shiftNum, dateQuery);
        String result = httpUtil.get(url, queryParam);
        Double confirmWgt = 0.0;
        if (StringUtils.isBlank(result)) {
            return confirmWgt;
        }
        JSONArray array = JSONObject.parseArray(result);
        if (Objects.isNull(array) || array.size() == 0) {
            return confirmWgt;
        }
        JSONObject jsonObject = array.getJSONObject(0);
        if (Objects.nonNull(jsonObject)) {
            confirmWgt = jsonObject.getDouble("confirmWgt");
        }
        return confirmWgt;
    }

    protected Double mapDataHandler2(Integer[] shiftArr, String url, DateQuery dateQuery) {
        Double confirmWgt = 0.0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getRecordDate());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date start = calendar.getTime();
        Date todayBeginTime = DateUtil.getTodayBeginTime();
        int betweenDays = DateUtil.getTimeDistance(start, todayBeginTime);
        for (int i = 0; i < betweenDays; i++) {
            for (int j = 0; j < shiftArr.length; j++) {
                Map<String, String> queryParam = getQueryParam4(shiftArr[j], DateUtil.addDays(start, i));
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isNotBlank(result)) {
                    JSONArray array = JSONObject.parseArray(result);
                    if (Objects.nonNull(array) && array.size() != 0) {
                        JSONObject jsonObject = array.getJSONObject(0);
                        if (Objects.nonNull(jsonObject)) {
                            confirmWgt += jsonObject.getDouble("confirmWgt");
                        }
                    }
                }
            }
        }

        return confirmWgt;
    }

    private Map<String, String> getQueryParamByShift() {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(shiftStart, "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(shiftEnd, "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    private Map<String, String> getQueryParamBySegment(DateSegment segment) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(segment.getStartDate(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(segment.getEndDate(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(), -10), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParamX(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(),-2), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(), 2), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParamX1(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(), 3), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam3(Integer shiftNum, DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd 00:00:00"));
        result.put("shift", shiftNum.toString());
        result.put("code", "GHJY");
        result.put("flag", "O");
        return result;
    }

    protected Map<String, String> getQueryParam4(Integer shiftNum, Date date) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(date, "yyyy/MM/dd 00:00:00"));
        result.put("shift", shiftNum.toString());
        result.put("code", "GHJY");
        result.put("flag", "O");
        return result;
    }

    protected String getUrl(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getTagValue";
    }

    private String getUrl2(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getCoalSiloName";
    }

    private String getUrl3(String version) {
        return httpProperties.getJHUrlVersion(version) + "/manufacturingState/getTagValue";
    }

    private String getUrl4(String version) {
        return httpProperties.getJHUrlVersion(version) + "/cokingYieldAndNumberHoles/getCokeActuPerfByDateAndShiftAndCode";
    }

}