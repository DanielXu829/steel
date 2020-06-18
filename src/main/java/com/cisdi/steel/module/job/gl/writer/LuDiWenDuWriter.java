package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@SuppressWarnings("ALL")
@Slf4j
public class LuDiWenDuWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery dateQuery = getDateQuery(excelDTO);
        String version = "8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }
        List<Date> allDayBeginTimeInCurrentMonth = DateUtil.getAllDayBeginTimeInCurrentMonthBeforeDays(dateQuery.getRecordDate(), 1);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            // 以下划线开头的sheet 表示 隐藏表 待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                List<CellData> cellDataList = new ArrayList<>();
                for (int dateIndex = 0; dateIndex < allDayBeginTimeInCurrentMonth.size(); dateIndex++) {
                    DateQuery eachDateQuery = DateQueryUtil.buildDayAheadTwoHour(allDayBeginTimeInCurrentMonth.get(dateIndex));
                    Map<String, Double> stringValueMap = jsonToMap(getTagData(getUrl(version), columns, eachDateQuery));
                    for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                        Double value = stringValueMap.get(columns.get(columnIndex));
                        ExcelWriterUtil.addCellData(cellDataList, dateIndex + 1, columnIndex, value);
                    }
                }
                ExcelWriterUtil.setCellValue(sheet, cellDataList);
            }
            if (!Objects.isNull(sheet) && !workbook.isSheetHidden(sheetIndex)) {
                // 全局替换 当前日期
                ExcelWriterUtil.replaceCurrentMonthInTitle(sheet, 0, 0, allDayBeginTimeInCurrentMonth.get(0));
            }
        }

        return workbook;
    }

    /**
     * json字符串转map
     * @param jsonData
     * @return
     */
    protected Map<String, Double> jsonToMap(String jsonData) {
        JSONObject jsonObject = JSONObject.parseObject(jsonData).getJSONObject("data");
        Map<String, Double> tagNameToValuemap = new HashMap<>();
        for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
            JSONObject timeToValue = (JSONObject) stringObjectEntry.getValue();
            List<Object> arrayList = new ArrayList(timeToValue.values());
            if (arrayList.size() > 0) {
                tagNameToValuemap.put(stringObjectEntry.getKey(), new Double(arrayList.get(0).toString()));
            }
        }

        return tagNameToValuemap;
    }

    /**
     * 获取tag点数据
     * @param url
     * @param columns
     * @param dateQuery
     * @return
     */
    protected String getTagData(String url, List<String> columns, DateQuery dateQuery) {
        JSONObject jsonObject = new JSONObject();
        Map<String, String> queryParam = dateQuery.getQueryParam();
        String starttime = queryParam.get("starttime");
        String endtime = queryParam.get("endtime");
        jsonObject.put("starttime", starttime);
        jsonObject.put("endtime", endtime);
        jsonObject.put("tagnames", columns);

        return httpUtil.postJsonParams(url, jsonObject.toJSONString());
    }

    /**
     * 通过tag点拿数据API
     * @param version
     * @return
     */
    protected String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }
}
