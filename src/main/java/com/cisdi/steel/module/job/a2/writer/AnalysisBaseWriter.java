package com.cisdi.steel.module.job.a2.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
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
 * 公共检化验处理类
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class AnalysisBaseWriter extends BaseJhWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        String version ="67.0";
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
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                int size = dateQueries.size();
                if ("analysis".equals(sheetSplit[1])) {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        for (int k = 0; k < columns.size(); k++) {
                            if (StringUtils.isNotBlank(columns.get(k))) {
                                String[] split = columns.get(k).split("/");
                                int rowIndex = 1 + j;
                                if (split.length == 2) {
                                    Double cellDataList = mapDataHandler2(getUrl2(version), item, split[0], split[1],"", version);
                                    setSheetValue(sheet, rowIndex, k, cellDataList);
                                } else if (split.length == 3) {
                                    Double cellDataList = mapDataHandler2(getUrl2(version), item, split[0], split[1], split[2],version);
                                    setSheetValue(sheet, rowIndex, k, cellDataList);
                                } else {
                                    Double cellDataList = mapDataHandler4(getUrl4(version), item, split[0], split[1]);
                                    setSheetValue(sheet, rowIndex, k, cellDataList);
                                }
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        if (item.getRecordDate().before(new Date())) {
                            int rowIndex = j + 1;
                            List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl(version), columns, item);
                            ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        } else {
                            break;
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

    protected Double mapDataHandler2(String url, DateQuery dateQuery, String brandcode, String anaitemname,String source, String version) {
        Map<String, String> queryParam = getQueryParam2(dateQuery, brandcode, anaitemname, source,version);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        Double data = jsonObject.getDouble("data");
        return data;
    }


    protected Double mapDataHandler4(String url, DateQuery dateQuery, String code, String flag) {
        Map<String, String> queryParam = getQueryParam4(dateQuery, code, flag);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONArray row = JSONArray.parseArray(result);
        if (row.isEmpty()) {
            return null;
        }
        JSONObject data = (JSONObject) row.get(0);
        if (Objects.isNull(data)) {
            return null;
        }
        Double confirmWgt = data.getDouble("confirmWgt");
        return confirmWgt;
    }


    protected Map<String, String> getQueryParam2(DateQuery dateQuery, String brandcode, String anaitemname,String source, String version) {
        Map<String, String> result = new HashMap<>();
        result.put("brandcode", brandcode);
        result.put("starttime", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endtime", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("anaitemname", anaitemname);
        if ("12.0".equals(version)) {
            if(StringUtils.isBlank(source)){
                result.put("source", "1#-2#焦炉");
            }else {
                result.put("source", source);
            }
            result.put("unitno", "JH12");
        } else if ("67.0".equals(version)) {
            if(StringUtils.isBlank(source)){
                result.put("source", "6#-7#焦炉");
            }else {
                result.put("source", source);
            }
            result.put("unitno", "JH67");
        } else {
            if(StringUtils.isBlank(source)){
                result.put("source", "4#-5#焦炉");
            }else {
                result.put("source", source);
            }
            result.put("unitno", "JH45");
        }
        return result;
    }

    protected Map<String, String> getQueryParam4(DateQuery dateQuery, String code, String flag) {
        Map<String, String> result = new HashMap<>();
        result.put("code", code);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getRecordDate());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        result.put("dateTime", DateUtil.getFormatDateTime(calendar.getTime(), "yyyy/MM/dd HH:mm:ss"));
        if (DateUtil.getFormatDateTime(dateQuery.getEndTime(), "HH").equals("08")) {
            result.put("shift", "1");
        } else if (DateUtil.getFormatDateTime(dateQuery.getEndTime(), "HH").equals("16")) {
            result.put("shift", "2");
        } else {
            result.put("shift", "3");
        }
        result.put("flag", flag);
        return result;
    }


    protected String getUrl2(String version) {
        return httpProperties.getJHUrlVersion(version) + "/analyses/getIfAnaitemValByCodeOrSource";
    }


    protected String getUrl4(String version) {
        return httpProperties.getJHUrlVersion(version) + "/cokingYieldAndNumberHoles/getCokeActuPerfByDateAndShiftAndCode";
    }
}
