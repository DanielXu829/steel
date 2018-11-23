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
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
public class LianjiaoWDWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        workbook.cloneSheet(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        String format = dateFormat.format(new Date());
        workbook.setSheetName(numberOfSheets,"炉温记录"+format);
      //  String[] sheetSplit={"lianjiaolw","day","hour"};
        Sheet sheet = workbook.getSheet("炉温记录" + format);

        List<String> rowCelVal1 = getRowCelVal1(sheet, 3);
        List<CellData> cellData1 = mapDataHandler(4,getUrl(),rowCelVal1, date, "CO6");
        ExcelWriterUtil.setCellValue(sheet, cellData1);

        List<String> rowCelVal2 = getRowCelVal2(sheet, 3);
        List<CellData> cellData2 = mapDataHandler(4, getUrl(), rowCelVal2, date, "CO7");
        ExcelWriterUtil.setCellValue(sheet, cellData2);
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

    public static List<String> getRowCelVal2(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        short lastCellNum = 33;
        List<String> result = new ArrayList<>();
        for (int index = 18; index < lastCellNum; index++) {
            Cell cell = row.getCell(index);
            result.add(PoiCellUtil.getCellValue(cell));
        }
        return result;
    }

    protected List<CellData> mapDataHandler(int rowIndex,String url,List<String> columns, DateQuery dateQuery, String version) {
        Map<String, String> queryParam = getQueryParam(dateQuery, version);
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
                            List<CellData> cellData1 = handlerRowData(columns, rowIndex, obj,version);
                            cellDataList.addAll(cellData1);
                        }
                        rowIndex++;
                    }
                }
            }

        }
        return cellDataList;
    }

    public static List<CellData> handlerRowData(List<String> columns, int starRow, Map<String, Object> rowData,String version) {
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
                if("CO6".equals(version)){
                    ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, value);
                }else {
                    ExcelWriterUtil.addCellData(resultData, starRow, columnIndex+18, value);
                }

            }


        return resultData;
    }


    protected Map<String, String> getQueryParam(DateQuery dateQuery, String version) {
        Map<String, String> result = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getRecordDate());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
//        result.put("date", calendar.getTime().getTime() + "");
        result.put("date", "1540137600000");
        result.put("jlno", version);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        return result;
    }


    protected String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/tmmirbtmpDataTable/selectByDateAndType";
    }
}
