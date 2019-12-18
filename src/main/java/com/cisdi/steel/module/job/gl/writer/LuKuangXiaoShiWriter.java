package com.cisdi.steel.module.job.gl.writer;

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
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Description: 变料执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/14 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class LuKuangXiaoShiWriter extends AbstractExcelReadWriter {
    @Autowired
    private TargetManagementMapper targetManagementMapper;

    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        String version ="8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch(Exception e){
            log.error("在模板中获取version失败", e);
        }
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                // 拿到tag点别名
                List<String> columnCells = PoiCustomUtil.getFirstRowCelVal(sheet);
                // 根据别名获取tag点名, 没有查到也留空
                for (int j = 0; j < columnCells.size(); j++) {
                    if (columnCells.get(j).startsWith("ZP")) {
                        String tagName = targetManagementMapper.selectTargetFormulaByTargetName(columnCells.get(j));
                        if (StringUtils.isBlank(tagName)) {
                            columnCells.set(j, "");
                        } else {
                            columnCells.set(j, tagName);
                        }
                    }
                }

                List<CellData> rowCellDataList = new ArrayList<>();
                int size = dateQueries.size();
                for (int rowNum = 0; rowNum < size; rowNum++) {
                    DateQuery eachDate = dateQueries.get(rowNum);
                    List<CellData> cellDataList = eachData(columnCells, getUrl(version), eachDate.getQueryParam(), rowNum + 1);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
//                    rowCellDataList.addAll(cellValInfoList);
                }
//                for (int j = 0; j < size; j++) {
//                    DateQuery item = dateQueries.get(j);
//                    if (item.getRecordDate().before(new Date())) {
//                        int rowIndex = j + 1;
//                        List<CellData> cellDataList = this.mapDataHandler(getUrl(version), columnCells, item);
//                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
//                    } else {
//                        break;
//                    }
//                }
            }
        }

        return workbook;
    }

    private List<CellData> eachData(List<String> cellList, String url, Map<String, String> queryParam, int writeRow) {
        JSONObject jsonObject = new JSONObject();
        String starttime = queryParam.get("starttime");
        jsonObject.put("starttime", starttime);
        String endtime = queryParam.get("endtime");
        jsonObject.put("endtime", endtime);
        jsonObject.put("tagnames", cellList);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        List<CellData> resultList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < cellList.size(); columnIndex++) {
            String cell = cellList.get(columnIndex);
            if (StringUtils.isNotBlank(cell)) {
                JSONObject data = obj.getJSONObject(cell);
                if (Objects.nonNull(data)) {
                    Set<String> keys = data.keySet();
                    Long[] list = new Long[keys.size()];
                    int k = 0;
                    for (String key : keys) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    // 按照顺序排序
                    Arrays.sort(list);
                    List<DateQuery> dayEach = DateQueryUtil.buildDayHourOneEach(new Date(Long.valueOf(starttime)), new Date(Long.valueOf(endtime)));
                    handleWriteData(dayEach, list, data, resultList, writeRow, columnIndex, cell);
                }
            }
        }

        return resultList;
    }

    private void handleWriteData(List<DateQuery> dayEach, Long[] list, JSONObject data, List<CellData> resultList, int rowIndex, int columnIndex, String cell) {
        for (int j = 0; j < dayEach.size(); j++) {
            DateQuery query = dayEach.get(j);
            Date queryStartTime = query.getStartTime();
            Date queryEndTime = query.getEndTime();
            if (cell.endsWith("_evt")) {
                Double maxDoubleValue = 0.0d;
                Double doubleValue = 0.0d;
                for (int i = 0; i < list.length; i++) {
                    Long tempTime = list[i];
                    Date date = new Date(tempTime);
//                    String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd HH:mm:ss");
//                    Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                    if (date.after(queryStartTime) && date.before(queryEndTime)) {
                        doubleValue = data.getDouble(tempTime + "");
                        if (doubleValue > maxDoubleValue) {
                            maxDoubleValue = doubleValue;
                        }
                    }
                }
                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, maxDoubleValue);
            } else {
                for (int i = 0; i < list.length; i++) {
                    Long tempTime = list[i];
                    Date date = new Date(tempTime);
//                    String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd HH:00:00");
//                    Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                    if (date.after(queryStartTime) && date.before(queryEndTime)) {
                        Object o = data.get(tempTime + "");
                        ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 通过tag点拿数据的API，路径可能会改变
     * @param version
     * @return
     */
    protected String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }
}
