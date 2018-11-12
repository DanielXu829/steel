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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

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
public class PeimeizuoyequWriter extends AbstractExcelReadWriter {

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
                String name = sheetSplit[1];
                // 粉碎
                if ("crushing".equals(name)) {
                    // TODO:待处理
                    List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                    for (DateQuery dateQuery : dateQueries) {
                        List<CellData> cellDataList = this.mapDataHandler(getUrlTwo(), columns, dateQuery, 1);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                } else if ("auto".equals(name)) {
                    for (DateQuery dateQuery : dateQueries) {
                        List<CellData> cellDataList = this.handlerData2(dateQuery, sheet);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                }
            }
        }
        return workbook;
    }

    public List<CellData> handlerData2(DateQuery dateQuery, Sheet sheet) {
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


    @Override
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getRecordDate());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String dateTime = DateUtil.getFormatDateTime(calendar.getTime(), DateUtil.fullFormat);
        result.put("time", dateTime);
        return result;
    }

    private String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getVauleByNameAndTime";
    }

    private String getUrlTwo() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/particleDistributionByDate";
    }

}