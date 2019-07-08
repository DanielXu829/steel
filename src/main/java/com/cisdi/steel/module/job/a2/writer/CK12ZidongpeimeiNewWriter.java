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
    private static final int workStartTimeoffset = -60;
    private static final int workHours = 12;
    private Date shiftStart;
    private Date shiftEnd;
    private String version;

    // 皮带对应的tag点
    private static final Map<String, String> pd2mtTagNames = new HashMap<>();
    // 料嘴对应的皮带
    private static final Map<String, Integer> lz2pd = new HashMap<>();
    // 皮带的运行时段
    private Map<String, List<DateSegment>> pdRunSegments = new HashMap<>();
    // 清零时刻
    private List<Date> CBReset = null;
    // 甲乙丙班月累计
    private Map<Integer, Double> teamTotal = new HashMap<>();

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

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        //测试时的偏移，发布时要去掉
//        int offsetHours = -4;
//        date.setRecordDate(DateUtil.addHours(date.getRecordDate(), offsetHours));

        Date recordDate = date.getRecordDate();

        try{
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        }catch(Exception e){
            version = "12.0";
        }

        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                String name = sheetSplit[1];
                // 自动配煤
                if ("auto".equals(name)) {
                    String shift = "";
                    Integer[] shiftCur = {};
                    Date todayBeginTime = getTodayBegin(recordDate);
                    Date workStart = DateUtil.addMinute(todayBeginTime, workStartTimeoffset);
                    Date date1 = DateUtil.addHours(workStart, workHours);
                    Date date2 = DateUtil.addHours(date1, workHours);
                    if (DateUtil.isEffectiveDate(recordDate, workStart, date1)) {
                        shift = "夜班";
                        Integer[]  tmp = {1};
                        shiftCur = tmp;
                        shiftStart = workStart;
                        shiftEnd = date1;
                    } else if (DateUtil.isEffectiveDate(recordDate, date1, date2)) {
                        shift = "白班";
                        Integer[]  tmp = {2,3};
                        shiftCur = tmp;
                        shiftStart = date1;
                        shiftEnd = date2;
                    }

                    pdRunSegments = getDateSegment();
                    CBReset = getCBReset();
                    teamTotal.clear();

                    // 班次
                    int rowIndex = 50;
                    Row row = sheet.createRow(rowIndex);
                    row.createCell(0).setCellValue(shift);
                    row.getCell(0).setCellType(CellType.STRING);

                    String url = getUrl4();
                    // 焦油渣 当班
                    Double yiedNum = this.mapDataHandler2(url, shiftCur, getTodayBegin(recordDate), recordDate);
                    Row row2 = sheet.createRow(++rowIndex);
                    row2.createCell(0).setCellValue(yiedNum);
                    row2.getCell(0).setCellType(CellType.NUMERIC);

                    // 焦油渣 月累计
                    Integer[] shiftArr = {1, 2, 3};
                    Double yiedNumLeiji = this.mapDataHandler2(url, shiftArr, getMonthBegin(recordDate), recordDate);
                    Row row3 = sheet.createRow(++rowIndex);
                    row3.createCell(0).setCellValue(yiedNumLeiji);
                    row3.getCell(0).setCellType(CellType.NUMERIC);

                    url = getUrl();
                    String[] peimei = {"CK12_L1R_CB_CBAmtTol1_evt","CK12_L1R_CB_CBAmtTol2_evt"};
                    // 配煤量1# 月累计
                    Row row4 = sheet.createRow(++rowIndex);
                    Double double4 = peimeiLeiji(url, date, peimei[0]);
                    row4.createCell(0).setCellValue(double4);
                    row4.getCell(0).setCellType(CellType.NUMERIC);

                    // 配煤量2# 月累计
                    Row row5 = sheet.createRow(++rowIndex);
                    Double double5 = peimeiLeiji(url, date,peimei[1]);
                    row5.createCell(0).setCellValue(double5);
                    row5.getCell(0).setCellType(CellType.NUMERIC);

                    // 配煤量 甲乙丙班月累计
                    Row row6 = sheet.createRow(++rowIndex);
                    for (Map.Entry<Integer, Double> entry : teamTotal.entrySet()) {
                        row6.createCell(entry.getKey()).setCellValue(entry.getValue());
                        row6.getCell(entry.getKey()).setCellType(CellType.NUMERIC);
                    }

                    List<CellData> cellDataList = this.handlerData(date, sheet);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);

                    // 煤种
                    initCoalSiloName(sheet, recordDate);
                }
            }
        }
        return workbook;
    }

    private void initCoalSiloName(Sheet sheet, Date recordDate){
        Map<String, String> queryParam = getQueryParam3(recordDate);
        String result = httpUtil.get(getUrl2(), queryParam);
        if (StringUtils.isBlank(result)) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return;
        }
        Map<String, Object> innerMap = data.getInnerMap();
        Set<String> strings = innerMap.keySet();
        List<CellData> cellDataList = new ArrayList<>();
        int row = 19;
        for (int j = 0; j < strings.size(); j++) {
            String s = "coalSiloName" + (j + 1);
            if(innerMap.containsKey(s)){
                ExcelWriterUtil.addCellData(cellDataList, row, j, innerMap.get(s).toString());
            }
        }
        ExcelWriterUtil.setCellValue(sheet, cellDataList);
    }

    private Double peimeiLeiji(String url, DateQuery dateQuery, String tagName) {
        Date startTime = getMonthBegin(dateQuery.getRecordDate());
        dateQuery.setStartTime(startTime);
        Map<String, String> queryParam = getQueryParamX1(dateQuery);
        queryParam.put("tagNames", tagName);
        String result = httpUtil.get(url, queryParam);
        Double total = 0.0;
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    JSONArray arr = data.getJSONArray(tagName);
                    if (Objects.nonNull(arr) && arr.size() != 0) {
                        for (int i = 0; i < arr.size(); i++) {
                            JSONObject jsonObject1 = arr.getJSONObject(i);
                            if (jsonObject1.getDouble("val") > 0) {
                                String clock = jsonObject1.getString("clock");
                                Double val = jsonObject1.getDouble("val");
                                shiftTotal(clock,val);
                                total += val;
                            }
                        }
                    }
                }
            }
        }
        return total;
    }

    private void shiftTotal(String clock, Double val){
        Date tmp = DateUtil.strToDate(clock, DateUtil.fullFormat);
        String date = DateUtil.getFormatDateTime(tmp, "yyyy-MM-dd 00:00:00");
        String shiftNo = (tmp.getHours()<=10) ? "1" : "2";
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("date",date);
        queryParam.put("shiftNo",shiftNo);
        JSONArray data = getDataArray(getUrl5(), queryParam);
        if (Objects.nonNull(data) && data.size() != 0) {
            Integer teamNo = data.getJSONObject(0).getInteger("WORK_TEAM");
            Double v= teamTotal.get(teamNo);
            if(null != v){
                val += v;
            }
            teamTotal.put(teamNo, val);
        }
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
                        JSONObject data = getDataObject(getUrl(), map);
                        if(null != data){
                            JSONArray arr = data.getJSONArray(cellValue);
                            if (Objects.nonNull(arr) && arr.size() != 0) {
                                double val = 0;
                                for (int m = 0; m< arr.size(); m++) {
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
     * 返回data
     * @param map
     * @return
     */
    private JSONObject getDataObject(String url, Map<String, String > map){
        String result = httpUtil.get(url, map);
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

    /**
     * 返回rows
     * @param map
     * @return
     */
    private JSONArray getRowsObject(String url, Map<String, String > map){
        String result = httpUtil.get(url, map);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (Objects.nonNull(rows)) {
                    return rows;
                }
            }
        }
        return null;
    }

    /**
     * 返回data
     * @param map
     * @return
     */
    private JSONArray getDataArray(String url, Map<String, String > map){
        String result = httpUtil.get(url, map);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray data = jsonObject.getJSONArray("data");
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
            for (Map.Entry<String, String> kv : map.entrySet()) {
                log.info("key:{},val:{}",kv.getKey(),kv.getValue());
            }
            JSONObject data = getDataObject(getUrl(), map);
            if(null != data){
                JSONArray arr = data.getJSONArray(tagName);
                if (Objects.nonNull(arr) && arr.size() >1) {
                    double start = arr.getJSONObject(0).getDouble("val");

                    int n=arr.size()-1;
                    for (int m = n; m>=0 ; m--) {
                        JSONObject o = arr.getJSONObject(m);
                        if(o.getDouble("val")>0){
                            n = m;
                            break;
                        }
                    }
                    double end1 = arr.getJSONObject(n).getDouble("val");
                    double end2 = n>1 ? arr.getJSONObject(n-1).getDouble("val") : 0;
                    double end = Math.max(end1,end2);
                    sum += end-start;
                    if(CBReset.size()>0){// 清零点超过0个，说明中途清零过
                        Date startDate = arr.getJSONObject(0).getDate("clock");
                        Date endDate = n>1 ? arr.getJSONObject(n-1).getDate("clock") : startDate;
                        log.info("CB:startDate:{},endDate:{}", DateUtil.getFormatDateTime(startDate,DateUtil.yyyyMMddHHmm), DateUtil.getFormatDateTime(endDate,DateUtil.yyyyMMddHHmm));
                        for (Date date : CBReset) {
                            if(date.after(startDate)&&date.before(endDate)){// 清零点在运行段之间
                                int pos = getPosByDate(startDate,0, date);
                                if(pos > arr.size()-1){// 越界，说明步长不一致
                                    log.info("CB:{},pos:{},maxPos:{}", DateUtil.getFormatDateTime(date,DateUtil.yyyyMMddHHmm),pos,arr.size()-1);
                                    continue;
                                }
                                double tmp = arr.getJSONObject(pos).getDouble("val");
                                if(Math.abs(tmp - end) > 0.01){
                                    log.info("CB:{},tmp:{}", DateUtil.getFormatDateTime(date,DateUtil.yyyyMMddHHmm),tmp);
                                    sum += tmp;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(sum != 0){
            cellDataList.add(new CellData(row+1, col, sum));
        }
    }

    private Integer getPosByDate(Date startDate,int startPos, Date date){
        int minutes = (int)(date.getTime()-startDate.getTime())/(1000*60);
        return startPos+minutes;
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
        JSONObject data = getDataObject(getUrl(), map);
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
        log.info("tagName:{},size:{}",tagName,list.size());
        for (DateSegment dateSegment : list) {
            log.info("start:{},end:{}",dateSegment.getStartDate(),dateSegment.getEndDate());
        }
        return list;
    }

    private List<Date> getCBReset() {
        List<Date> list = new ArrayList<>();
        Map<String, String> map = getQueryParamByShift();
        map.put("tagName", "CK12_L1R_CB_CBReset_4_report");
        JSONArray rows = getRowsObject(getUrl3(), map);
        if (null != rows) {
            for (int i = 0; i < rows.size()-1; i++) {// 默认升序，忽略最后一个清零点
                JSONObject jsonObject1 = rows.getJSONObject(i);
                String clock = jsonObject1.getString("clock");
                list.add(DateUtil.strToDate(clock, DateUtil.fullFormat, DateUtil.yyyyMMddHHmm));
            }
        }
        for (Date date : list) {
            log.info("CBReset:{}",date);
        }
        return list;
    }

    private List<CellData> handlerDataX(String url, List<String> columns, DateQuery date) {
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

    private List<CellData> mapDataHandler(String url, List<String> columns, int rowBatch, DateQuery dateQuery) {
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

    private Date getTodayBegin(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    private Date getMonthBegin(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    private Double mapDataHandler2(String url, Integer[] shiftArr, Date start, Date end) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);

        Double confirmWgt = 0.0;
        while (start.before(end)) {
            for (int j = 0; j < shiftArr.length; j++) {
                Map<String, String> queryParam = getQueryParam4(shiftArr[j], start);
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
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            start = calendar.getTime();
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

    private Map<String, String> getQueryParamX(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(),-2), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(), 2), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    private Map<String, String> getQueryParamX1(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(), 3), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    private Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    private Map<String, String> getQueryParam3(Date date) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(date, "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    private Map<String, String> getQueryParam4(Integer shiftNum, Date date) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(date, "yyyy/MM/dd 00:00:00"));
        result.put("shift", shiftNum.toString());
        result.put("code", "GHJY");
        result.put("flag", "O");
        return result;
    }

    private String getUrl() {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getTagValue";
    }

    private String getUrl2() {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getCoalSiloName";
    }

    private String getUrl3() {
        return httpProperties.getJHUrlVersion(version) + "/manufacturingState/getTagValue";
    }

    private String getUrl4() {
        return httpProperties.getJHUrlVersion(version) + "/cokingYieldAndNumberHoles/getCokeActuPerfByDateAndShiftAndCode";
    }

    private String getUrl5() {
        return httpProperties.getJHUrlVersion(version) + "/getShiftTeamByDateAndNo";
    }

}