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
import org.apache.poi.ss.usermodel.*;
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
public class ZidongpeimeiWriter extends AbstractExcelReadWriter {

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
                // 自动配煤
                if ("auto".equals(name)) {
                    String shift = "";
                    if (dateQueries.size() == 1) {
                        shift = "夜班";
                    } else if (dateQueries.size() == 2) {
                        shift = "白班";
                    } else if (dateQueries.size() == 3) {
                        shift = "中班";
                    }
                    Row row = sheet.createRow(29);
                    row.createCell(0).setCellValue(shift);
                    row.getCell(0).setCellType(CellType.STRING);

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
        String dateTime = DateUtil.getFormatDateTime(new Date(), DateUtil.fullFormat);
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

}