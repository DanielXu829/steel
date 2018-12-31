package com.cisdi.steel.module.job.a2.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 炼焦报表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class Luwenjilu6Writer extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        int numberOfSheets = workbook.getNumberOfSheets();
        DateQuery dateQuery = this.getDateQuery(excelDTO);
        String dd = DateUtil.getFormatDateTime(dateQuery.getStartTime(), "dd");
        String format = DateUtil.getFormatDateTime(dateQuery.getStartTime(), "MM.dd");
        int ss = Integer.parseInt(dd);
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheetAt = workbook.getSheetAt(i);
            String sheetName = sheetAt.getSheetName();
            if (format.equals(sheetName)) {
                workbook.removeSheetAt(i);
                numberOfSheets--;
            }
            if ("6炉机侧炉温管控".equals(sheetName)) {
                int rowNum = 5;
                for (int j = 3; j < 59; j++) {
                    Row row = sheetAt.getRow(j);
                    Cell cell = row.getCell(ss);
                    String cellValue = "IF('" + format + "'!I" + rowNum + "=\"\",\"\",'" + format + "'!I" + rowNum + ")";
                    cell.setCellFormula(cellValue);
                    cell.setCellType(CellType.FORMULA);
                    rowNum++;
                }
            } else if ("6炉焦侧炉温管控".equals(sheetName)) {
                int rowNum = 5;
                for (int j = 3; j < 59; j++) {
                    Row row = sheetAt.getRow(j);
                    Cell cell = row.getCell(ss);
                    String cellValue = "IF('" + format + "'!Q" + rowNum + "=\"\",\"\",'" + format + "'!Q" + rowNum + ")";
                    cell.setCellFormula(cellValue);
                    cell.setCellType(CellType.FORMULA);
                    rowNum++;
                }
            }
        }
        workbook.cloneSheet(0);
        workbook.setSheetName(numberOfSheets, format);
        Sheet sheet = workbook.getSheet(format);
        Row row = sheet.getRow(0);
        Cell cell = row.getCell(1);
        cell.setCellValue(DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd"));
        HashMap<String, Integer> map = mapDataHandler2(getUrl2(), dateQuery);
        Row row1 = sheet.getRow(66);
        for (int r = 1; r < 8; r++) {
            Cell cell1 = row1.getCell(r);
            cell1.setCellValue(map.get("standardTempMach"));
        }
        for (int r = 9; r < 16; r++) {
            Cell cell1 = row1.getCell(r);
            cell1.setCellValue(map.get("standardTempCoke"));
        }
        List<String> rowCelVal1 = getRowCelVal1(sheet, 3);
        List<CellData> cellData1 = mapDataHandler(4, getUrl(), rowCelVal1, "CO6", dateQuery);
        ExcelWriterUtil.setCellValue(sheet, cellData1);

        return workbook;
    }

    public static List<String> getRowCelVal1(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        short lastCellNum = 15;
        List<String> result = new ArrayList<>();
        for (int index = 0; index < lastCellNum; index++) {
            Cell cell = row.getCell(index);
            result.add(PoiCellUtil.getCellValue(cell));
        }
        return result;
    }

    protected HashMap<String, Integer> mapDataHandler2(String url, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam2(dateQuery);
        String result = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSONObject.parseObject(result);
        Integer standardTempMach = jsonObject.getInteger("standardTempMach");
        Integer standardTempCoke = jsonObject.getInteger("standardTempCoke");
        HashMap<String, Integer> map = new HashMap<>();
        map.put("standardTempMach", standardTempMach);
        map.put("standardTempCoke", standardTempCoke);
        return map;
    }

    protected List<CellData> mapDataHandler(int rowIndex, String url, List<String> columns, String version, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam(version, dateQuery);
        List<CellData> cellDataList = new ArrayList<>();
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (Objects.nonNull(rows)) {
                    for (int j = 0; j < rows.size(); j++) {
                        JSONObject obj = rows.getJSONObject(j);
                        if (Objects.nonNull(obj)) {
                            List<CellData> cellData1 = handlerRowData(columns, rowIndex, obj);
                            cellDataList.addAll(cellData1);
                        }
                        rowIndex++;
                    }
                }
            }

        }
        return cellDataList;
    }

    public static List<CellData> handlerRowData(List<String> columns, int starRow, Map<String, Object> rowData) {
        List<CellData> resultData = new ArrayList<>();
        int size = columns.size();
        // 忽略大小写
        CaseInsensitiveMap<String, Object> rowDataMap = new CaseInsensitiveMap<>(rowData);
        for (int columnIndex = 0; columnIndex < size; columnIndex++) {
            String column = columns.get(columnIndex);
            if (StringUtils.isBlank(column)) {
                continue;
            }
            Object value = rowDataMap.get(column);
            ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, value);

        }

        return resultData;
    }


    protected Map<String, String> getQueryParam(String version, DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getDateBeginTime(dateQuery.getRecordDate()).getTime() + "");
        result.put("jlno", version);
        return result;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("date", dateQuery.getRecordDate().toString());
        return result;
    }

    protected String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/tmmirbtmpDataTable/selectByDateAndType";
    }

    protected String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/thermalRegulation/getCurrentByDate";
    }

}
