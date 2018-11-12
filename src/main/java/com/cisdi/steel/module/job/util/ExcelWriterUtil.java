package com.cisdi.steel.module.job.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.CellValInfo;
import com.cisdi.steel.module.job.dto.RowCellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * excel 写入的工具类
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class ExcelWriterUtil {

    /**
     * 存放的单元数据
     *
     * @param cellData 集合
     * @param rowIndex 行索引
     * @param column   列索引
     * @param value    值
     */
    public static void addCellData(List<CellData> cellData, Integer rowIndex, Integer column, Object value) {
        if (Objects.nonNull(value)) {
            cellData.add(new CellData(rowIndex, column, value));
        }
    }

    /**
     * 设置数据
     *
     * @param sheetRowCelData 数据
     */
    public static void setSheetRowCelData(SheetRowCellData sheetRowCelData) {
        if (Objects.nonNull(sheetRowCelData)) {
            Sheet sheet = sheetRowCelData.getSheet();
            List<RowCellData> rowCellDataList = sheetRowCelData.getRowCellDataList();
            if (Objects.nonNull(rowCellDataList) && !rowCellDataList.isEmpty()) {
                for (RowCellData rowCellData : rowCellDataList) {
                    if (Objects.nonNull(rowCellData)) {
                        // 指定行
                        Row row = getRowOrCreate(sheet, rowCellData.getRowIndex());
                        List<CellValInfo> values = rowCellData.getValues();
                        if (Objects.nonNull(values) && !values.isEmpty()) {
                            for (CellValInfo value : values) {
                                // 指定单元格
                                Cell cell = getCellOrCreate(row, value.getColumnIndex());
                                PoiCustomUtil.setCellValue(cell, value.getCellValue());
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     * 获取某一行 如果不存在就创建
     *
     * @param sheet  指定sheet
     * @param rowNum 指定行
     * @return 结果
     */
    private static Row getRowOrCreate(Sheet sheet, Integer rowNum) {
        Row row = sheet.getRow(rowNum);
        if (Objects.isNull(row)) {
            return sheet.createRow(rowNum);
        }
        return row;
    }

    /**
     * 获取某一个单元格 如果不存在则创建
     *
     * @param row       指定行
     * @param columnNum 指定列
     * @return 结果
     */
    private static Cell getCellOrCreate(Row row, Integer columnNum) {
        Cell cell = row.getCell(columnNum);
        if (Objects.isNull(cell)) {
            return row.createCell(columnNum);
        }
        return cell;
    }

    /**
     * 对每个单元格写入数据
     *
     * @param sheet        表
     * @param cellDataList 所有单元格的值
     */
    public static void setCellValue(Sheet sheet, List<CellData> cellDataList) {
        Objects.requireNonNull(sheet);
        if (Objects.nonNull(cellDataList)) {
            for (CellData cellData : cellDataList) {
                int rowNum = cellData.getRowIndex();
                Row row = getRowOrCreate(sheet, rowNum);
                Integer column = cellData.getColumnIndex();
                Cell cell = getCellOrCreate(row, column);
                PoiCustomUtil.setCellValue(cell, cellData.getCellValue());
            }
        }
    }

    /**
     * 处理每一行数据
     * 列名格式为 aasdf/asdf  下划线分隔
     *
     * @param columns 所有列名
     * @param starRow 开始行
     * @param rowData 每一行对应的数据
     * @return 结果
     */
    public static List<CellData> handlerRowData(List<String> columns, int starRow, Map<String, Object> rowData) {
        List<CellData> resultData = new ArrayList<>();
        int size = columns.size();
        // 忽略大小写
        CaseInsensitiveMap<String, Object> rowDataMap = new CaseInsensitiveMap<>(rowData);
        for (int columnIndex = 0; columnIndex < size; columnIndex++) {
            String column = columns.get(columnIndex);
            if (!column.contains("/")) {
                Object value = rowDataMap.get(column);
                ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, value);
            } else {
                String[] split = column.split("/");
                String key = split[0];
                String keyChild = split[1];
                Object o = rowDataMap.get(key);
                if (o instanceof JSONObject) {
                    JSONObject object = (JSONObject) o;
                    CaseInsensitiveMap<String, Object> map = new CaseInsensitiveMap<>(object);
                    Object value = map.get(keyChild);
                    ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, value);
                } else if (o instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) o;
                    int childIndex = starRow;
                    for (Object obj : jsonArray) {
                        JSONObject item = (JSONObject) obj;
                        CaseInsensitiveMap<String, Object> map = new CaseInsensitiveMap<>(item);
                        Object value = map.get(keyChild);
                        ExcelWriterUtil.addCellData(resultData, childIndex++, columnIndex, value);
                    }
                }
            }
        }
        return resultData;
    }
}
