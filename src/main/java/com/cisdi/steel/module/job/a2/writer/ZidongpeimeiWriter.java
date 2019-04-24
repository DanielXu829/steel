package com.cisdi.steel.module.job.a2.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 自动配煤
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
@Slf4j
public class ZidongpeimeiWriter extends AbstractExcelReadWriter {

    public Double compareTagVal(String tagName, DateQuery dateQuery) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tagName", tagName);
        String time = DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:00");
//        map.put("startDate", time);
//        map.put("endDate", time);
        String s = httpUtil.get(getUrl3(), map);
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
        String[] tagNamesIf = {"CK67_L1R_CB_CBAmtTol_1m_max", "CK67_L1R_CB_CBAcTol_1m_avg"};
        Double max = compareTagVal(tagNamesIf[0], date);
        Double avg = compareTagVal(tagNamesIf[1], date);
        if (Objects.nonNull(max) && Objects.nonNull(avg)) {
            if (max.intValue() < 1 && avg.intValue() == 0) {
                log.error("根据条件判断停止执行自动配煤报表");
                //return null;
            }
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
                    Date date1 = DateUtil.addHours(todayBeginTime, 8);
                    Date date2 = DateUtil.addHours(date1, 8);
                    if (DateUtil.isEffectiveDate(date.getRecordDate(), todayBeginTime, date1)) {
                        shift = "夜班";
                        shiftNum = 1;
                    } else if (DateUtil.isEffectiveDate(date.getRecordDate(), date1, date2)) {
                        shift = "白班";
                        shiftNum = 2;
                    } else {
                        shift = "中班";
                        shiftNum = 3;
                    }
                    Row row = sheet.createRow(29);
                    row.createCell(0).setCellValue(shift);
                    row.getCell(0).setCellType(CellType.STRING);

                    Row row1 = sheet.createRow(33);
                    Double aDouble = peimeiLeiji(getUrl3(), date);
                    row1.createCell(0).setCellValue(aDouble);
                    row1.getCell(0).setCellType(CellType.NUMERIC);

                    Double yiedNum = this.mapDataHandler1(shiftNum, getUrl4(), date);
                    Row row2 = sheet.createRow(30);
                    row2.createCell(0).setCellValue(yiedNum);
                    row2.getCell(0).setCellType(CellType.NUMERIC);

                    Integer[] shiftArr = {1, 2, 3};
                    Double yiedNumLeiji = this.mapDataHandler2(shiftArr, getUrl4(), date);
                    Row row3 = sheet.createRow(31);
                    row3.createCell(0).setCellValue(yiedNumLeiji);
                    row3.getCell(0).setCellType(CellType.NUMERIC);
//                    for (int j = 0; j < dateQueries.size(); j++) {
                    List<CellData> cellDataList = this.handlerData(date, sheet);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
//                    }
                } else if ("evt".equals(name)) {
                    List<String> firstColumnCellVal = PoiCustomUtil.getFirstRowCelVal(sheet);
                    List<CellData> cellDataList = this.handlerDataX(getUrl(), firstColumnCellVal, date);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else {
                    List<String> firstColumnCellVal = PoiCustomUtil.getFirstRowCelVal(sheet);
                    List<CellData> cellData = this.mapDataHandler(getUrl2(), firstColumnCellVal, 1, date);
                    ExcelWriterUtil.setCellValue(sheet, cellData);
                }
            }
        }
        return workbook;
    }

    public Double peimeiLeiji(String url, DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        Map<String, String> result1 = new HashMap<>();
        Calendar instance = Calendar.getInstance();
        instance.setTime(dateQuery.getRecordDate());
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        result.put("startDate", DateUtil.getFormatDateTime(instance.getTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(DateUtil.getTodayEndTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("tagName", "CK67_L1R_CB_CBReset_4_report");
        String results = httpUtil.get(url, result);
        Double val1 = 0.0;
        if (StringUtils.isNotBlank(results)) {
            JSONObject jsonObject = JSONObject.parseObject(results);
            if (Objects.nonNull(jsonObject)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (Objects.nonNull(rows)) {
                    for (int i = 0; i < rows.size(); i++) {
                        JSONObject obj = rows.getJSONObject(i);
                        if (Objects.nonNull(obj)) {
                            String clock = obj.getString("clock");
                            result1.put("tagName", "CK67_L1R_CB_CBAmtTol_1m_max");
                            Date date = DateUtil.strToDate(clock, DateUtil.fullFormat);
                            result1.put("time", DateUtil.getFormatDateTime(date, "yyyy/MM/dd HH:mm:00"));
                            String results1 = httpUtil.get(getUrl(), result1);
                            if (StringUtils.isNotBlank(results1)) {
                                JSONObject jsonObject1 = JSONObject.parseObject(results1);
                                JSONArray rows1 = jsonObject1.getJSONArray("rows");
                                JSONObject obj1 = rows1.getJSONObject(0);
                                if (Objects.nonNull(obj1)) {
                                    val1 += obj1.getDouble("val");
                                }
                            }
                        }
                    }
                }
            }
        }
        return val1;
    }

    public List<CellData> handlerData(DateQuery dateQuery, Sheet sheet) {
        Map<String, String> queryParam = getQueryParam(dateQuery);
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        List<CellData> cellDataList = new ArrayList<>();
        for (int rowIndex = firstRowNum; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (Objects.nonNull(row)) {
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                for (int i = firstCellNum; i < lastCellNum; i++) {
                    Cell cell = row.getCell(i);
                    String cellValue = PoiCellUtil.getCellValue(cell);
                    if (StringUtils.isNotBlank(cellValue)) {
                        Map<String, String> map = getQueryParam(dateQuery);
                        map.put("tagNames", cellValue);
                        String result = httpUtil.get(getUrl(), map);
                        if (StringUtils.isNotBlank(result)) {
                            JSONObject jsonObject = JSONObject.parseObject(result);
                            if (Objects.nonNull(jsonObject)) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                if (Objects.nonNull(data)) {
                                    JSONArray arr = data.getJSONArray(cellValue);
                                    if (Objects.nonNull(arr) && arr.size() != 0) {
                                        JSONObject jsonObject1 = arr.getJSONObject(arr.size() - 1);
                                        Double val = jsonObject1.getDouble("val");
                                        Integer rowa = cell.getRowIndex() + 1;
                                        Integer col = cell.getColumnIndex();
                                        ExcelWriterUtil.addCellData(cellDataList, rowa, col, val);
                                    }
                                }
                            }
                        }
                    }

                }
                ExcelWriterUtil.getRowOrCreate(sheet, rowIndex);
            }
        }
        return cellDataList;
    }

    public List<CellData> handlerDataX(String url, List<String> columns, DateQuery date) {
        Map<String, String> queryParam = getQueryParam(date);
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i <= columns.size(); i++) {
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

    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(), -10), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParamX(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(DateUtil.addMinute(dateQuery.getRecordDate(), 1), "yyyy/MM/dd HH:mm:ss"));
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

    protected String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getTagValue";
    }

    private String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getCoalSiloName";
    }

    private String getUrl3() {
        return httpProperties.getUrlApiJHOne() + "/manufacturingState/getTagValue";
    }

    private String getUrl4() {
        return httpProperties.getUrlApiJHOne() + "/cokingYieldAndNumberHoles/getCokeActuPerfByDateAndShiftAndCode";
    }

}