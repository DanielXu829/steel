package com.cisdi.steel.module.job.a2.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
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
 * 鼓风冷凝
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
public class GufenglengningWriter extends BaseJhWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        String version ="67.0";
        try{
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        }catch(Exception e){
        }
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            int k = 1;
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = new ArrayList<>();
                String no = "";
                if ("_gfln2_day_hour".equals(sheetName)) {
                    Row row1 = sheet.getRow(0);
                    Row row2 = sheet.getRow(1);
                    Row row3 = sheet.getRow(2);

                    Cell cell1 = row1.getCell(0);
                    Cell cell2 = row2.getCell(0);
                    Cell cell3 = row3.getCell(0);

                    String cellValue1 = PoiCellUtil.getCellValue(cell1);
                    String cellValue2 = PoiCellUtil.getCellValue(cell2);
                    String cellValue3 = PoiCellUtil.getCellValue(cell3);
                    DateQuery dateQuery = dateQueries.get(0);
                    double a1 = getCellValue(dateQuery, cellValue1,version);
                    double a2 = getCellValue(dateQuery, cellValue2,version);
                    double a3 = getCellValue(dateQuery, cellValue3,version);
                    double max = (a1 > a2) ? a1 : a2;
                    max = (max > a3) ? max : a3;
                    if (max == a1) {
                        columns = PoiCustomUtil.getRowCelVal(sheet, 0);
                        no = "A";
                    } else if (max == a2) {
                        columns = PoiCustomUtil.getRowCelVal(sheet, 1);
                        no = "B";
                    } else {
                        columns = PoiCustomUtil.getRowCelVal(sheet, 2);
                        no = "C";
                    }

                    k = 3;
                } else {
                    columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                }
                int size = dateQueries.size();
                for (int j = 0; j < size; j++) {
                    DateQuery item = dateQueries.get(j);
                    int rowIndex = j + k;
                    List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl(version), columns, item);
                    if ("_gfln2_day_hour".equals(sheetName)) {
                        if (item.getRecordDate().before(new Date())) {
                            Row row = sheet.getRow(rowIndex);
                            if (Objects.isNull(row)) {
                                row = sheet.createRow(rowIndex);
                            }
                            Cell cell = row.getCell(32);
                            if (Objects.isNull(cell)) {
                                cell = row.createCell(32);
                            }
                            cell.setCellValue(no);
                        }
                    }
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    private double getCellValue(DateQuery dateQuery, String cellValue,String version) {
        double data = 0;
        Map<String, String> queryParam = getQueryParam(dateQuery);
        queryParam.put("tagName", cellValue);
        String result = httpUtil.get(getUrl(version), queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (Objects.nonNull(rows) && rows.size() != 0) {
                    JSONObject obj = rows.getJSONObject(0);
                    if (Objects.nonNull(obj)) {
                        data = obj.getDouble("val");
                    }
                }
            }
        }
        return data;
    }
}