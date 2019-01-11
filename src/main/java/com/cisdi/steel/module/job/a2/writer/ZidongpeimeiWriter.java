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
import sun.rmi.runtime.Log;

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
@SuppressWarnings("ALL")
@Component
@Slf4j
public class ZidongpeimeiWriter extends AbstractExcelReadWriter {

    public Double compareTagVal(String tagName){
        HashMap<String, String> map = new HashMap<>();
        map.put("tagName",tagName);
        String s = httpUtil.get(getUrl3(), map);
        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject tagValue = data.getJSONObject("TagValue");
        Double val = tagValue.getDouble("val");
        return val;
    }

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        String[] tagNamesIf={"CK67_L1R_CB_CBAmtTol_1m_max","CK67_L1R_CB_CBAcTol_1m_avg"};
        Double max = compareTagVal(tagNamesIf[0]);
        Double avg = compareTagVal(tagNamesIf[1]);
        if(max<1 && avg==0){
            log.error("根据条件判断停止执行自动配煤报表");
            System.exit(0);
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
                    Date todayBeginTime = DateUtil.getTodayBeginTime();
                    Date date1 = DateUtil.addHours(todayBeginTime, 8);
                    Date date2 = DateUtil.addHours(date1, 8);
                    if (DateUtil.isEffectiveDate(new Date(),todayBeginTime,date1)) {
                        shift = "夜班";
                    } else if (DateUtil.isEffectiveDate(new Date(),date1,date2)) {
                        shift = "白班";
                    } else{
                        shift = "中班";
                    }
                    Row row = sheet.createRow(29);
                    row.createCell(0).setCellValue(shift);
                    row.getCell(0).setCellType(CellType.STRING);

                    Row row1 = sheet.createRow(33);
                    Double aDouble = peimeiLeiji(getUrl3());
                    row1.createCell(0).setCellValue(aDouble);
                    row1.getCell(0).setCellType(CellType.NUMERIC);
//                    for (int j = 0; j < dateQueries.size(); j++) {
                    List<CellData> cellDataList = this.handlerData(dateQueries.get(dateQueries.size() - 1), sheet);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
//                    }
                }else{
                    List<String> firstColumnCellVal = PoiCustomUtil.getFirstRowCelVal(sheet);
                    List<CellData> cellData = this.mapDataHandler(getUrl2(), firstColumnCellVal, 1);
                    ExcelWriterUtil.setCellValue(sheet, cellData);
                }
            }
        }
        return workbook;
    }

    public Double peimeiLeiji(String url){
        Map<String, String> result = new HashMap<>();
        Map<String, String> result1 = new HashMap<>();
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.set(Calendar.DAY_OF_MONTH,1);
        instance.set(Calendar.HOUR_OF_DAY,0);
        instance.set(Calendar.MINUTE,0);
        instance.set(Calendar.SECOND,0);
        result.put("startDate",DateUtil.getFormatDateTime(instance.getTime(),"yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(new Date(), "yyyy/MM/dd HH:mm:ss"));
        result.put("tagName","CK67_L1R_CB_CBReset_4_report");
        String results = httpUtil.get(url, result);
        Double val1=0.0;
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
                            result1.put("time",DateUtil.getFormatDateTime(date,"yyyy/MM/dd HH:mm:00"));
                            String results1 = httpUtil.get(getUrl(), result1);
                            if (StringUtils.isNotBlank(results1)) {
                                JSONObject jsonObject1 = JSONObject.parseObject(results1);
                                JSONArray rows1 = jsonObject1.getJSONArray("rows");
                                JSONObject obj1 = rows1.getJSONObject(0);
                                if(Objects.nonNull(obj1)){
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
                        map.put("tagName", cellValue);
                        String result = httpUtil.get(getUrl(), map);
                        if (StringUtils.isNotBlank(result)) {
                            JSONObject jsonObject = JSONObject.parseObject(result);
                            if (Objects.nonNull(jsonObject)) {
                                JSONArray rows = jsonObject.getJSONArray("rows");
                                if (Objects.nonNull(rows)) {
                                    JSONObject obj = rows.getJSONObject(0);
                                    if (Objects.nonNull(obj)) {
                                        Double val = obj.getDouble("val");
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


    protected List<CellData> mapDataHandler(String url, List<String> columns, int rowBatch) {
        Map<String, String> queryParam = getQueryParam2();
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

    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        String dateTime = DateUtil.getFormatDateTime(new Date(), "yyyy/MM/dd HH:mm:00");
        result.put("time", dateTime);
        return result;
    }

    protected Map<String, String> getQueryParam2() {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(new Date(),"yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    private String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getVauleByNameAndTime";
    }
    private String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getCoalSiloName";
    }
    private String getUrl3() {
        return httpProperties.getUrlApiJHOne() + "/manufacturingState/getTagValue";
    }

}