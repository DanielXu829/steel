package com.cisdi.steel.module.job.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.module.job.dto.CellData;
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
     * 对每个单元格写入数据
     *
     * @param sheet        表
     * @param cellDataList 所有单元格的值
     */
    public static void setCellValue(Sheet sheet, List<CellData> cellDataList) {
        Objects.requireNonNull(sheet);
        for (CellData cellData : cellDataList) {
            int rowNum = cellData.getRowNum();
            rowNum++;
            Row row = sheet.getRow(rowNum);
            if (Objects.isNull(row)) {
                row = sheet.createRow(rowNum);
            }
            Integer column = cellData.getColumn();
            Cell cell = row.getCell(column);
            if (Objects.isNull(cell)) {
                cell = row.createCell(column);
            }
            PoiCustomUtil.setCellValue(cell, cellData.getValue());
        }
    }

    /**
     * 循环遍历数据
     * 每一个 dataList 对应 一行
     *
     * @param dataList 数据
     * @param columns  对应的列
     * @return 结果
     */
    public static List<CellData> loopRowData(List<Map<String, Object>> dataList, List<String> columns) {
        return loopRowData(dataList, columns, 1);
    }

    /**
     * 循环遍历数据
     * 每一个 dataList 对应 多行数据
     *
     * @param dataList 数据
     * @param columns  对应的列
     * @param rowPitch 每一个集合数据占多少行
     * @return 结果
     */
    public static List<CellData> loopRowData(List<Map<String, Object>> dataList, List<String> columns, int rowPitch) {
        int starRow = 0;
        List<CellData> resultData = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            // 每一行的数据
            JSONObject jsonObject = (JSONObject) data.get("data");
            // 存储每一个
            List<CellData> rowData = handlerRowData(columns, starRow, jsonObject);
            resultData.addAll(rowData);
            starRow += rowPitch;
        }
        return resultData;
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
