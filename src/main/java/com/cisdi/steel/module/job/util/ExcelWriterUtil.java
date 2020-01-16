package com.cisdi.steel.module.job.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.CellValInfo;
import com.cisdi.steel.module.job.dto.RowCellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.util.*;

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
     * 设置表格单元格样式（四周边框加粗）
     * @param workbook
     * @param sheet
     * @param beginRowNum 最上侧边框行
     * @param endRowNum 最下侧边框行
     * @param beginColumnNum 最左侧边框行
     * @param endColumnNum 最右侧边框行
     */
    public static void setBorderStyle(Workbook workbook, Sheet sheet, int beginRowNum, int endRowNum, int beginColumnNum, int endColumnNum) {
        //设置每个单元格的四周边框
        CellStyle cellNormalStyle = workbook.createCellStyle();
        cellNormalStyle.setBorderRight(BorderStyle.THIN);
        cellNormalStyle.setBorderBottom(BorderStyle.THIN);
        for (int i = beginRowNum; i <= endRowNum - 1; i++) {
            for (int j = beginColumnNum; j < endColumnNum; j++) {
                Cell cell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, i), j);
                cell.setCellStyle(cellNormalStyle);
            }
        }
        // 最左侧列边框
        CellStyle cellLeftStyle = workbook.createCellStyle();
        cellLeftStyle.setBorderLeft(BorderStyle.THICK);
        cellLeftStyle.setBorderBottom(BorderStyle.THIN);
        cellLeftStyle.setBorderRight(BorderStyle.THIN);
        for (int i = beginRowNum; i <= endRowNum; i++) {
            Cell cell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, i), beginColumnNum);
            cell.setCellStyle(cellLeftStyle);
        }
        // 最右侧边框
        CellStyle cellRightStyle = workbook.createCellStyle();
        cellRightStyle.setBorderRight(BorderStyle.THICK);
        cellRightStyle.setBorderBottom(BorderStyle.THIN);
        for (int i = beginRowNum; i <= endRowNum; i++) {
            Cell cell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, i), endColumnNum);
            cell.setCellStyle(cellRightStyle);
        }
        // 最后一行下边框
        CellStyle cellBottomStyle = workbook.createCellStyle();
        cellBottomStyle.setBorderBottom(BorderStyle.THICK);
        cellBottomStyle.setBorderRight(BorderStyle.THIN);
        Cell cell = null;
        for (int i = beginColumnNum; i <= endColumnNum; i++) {
            cell = ExcelWriterUtil.getCellOrCreate(sheet.getRow(endRowNum), i);
            if (i == beginColumnNum) {
                CellStyle cellBottomLeftStyle = workbook.createCellStyle();
                cellBottomLeftStyle.setBorderLeft(BorderStyle.THICK);
                cellBottomLeftStyle.setBorderBottom(BorderStyle.THICK);
                cellBottomLeftStyle.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(cellBottomLeftStyle);
                continue;
            }
            if (i == endColumnNum) {
                CellStyle cellBottomRightStyle = workbook.createCellStyle();
                cellBottomRightStyle.setBorderRight(BorderStyle.THICK);
                cellBottomRightStyle.setBorderBottom(BorderStyle.THICK);
                cell.setCellStyle(cellBottomRightStyle);
                continue;
            }

            cell.setCellStyle(cellBottomStyle);
        }
    }

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
    public static Row getRowOrCreate(Sheet sheet, Integer rowNum) {
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
    public static Cell getCellOrCreate(Row row, Integer columnNum) {
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
            if (StringUtils.isBlank(column)) {
                continue;
            }
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
                    if (null == value) {
                        value = "";
                    }
                    ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, value);
                } else if (o instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) o;
                    int childIndex = starRow;
                    for (Object obj : jsonArray) {
                        JSONObject item = (JSONObject) obj;
                        CaseInsensitiveMap<String, Object> map = new CaseInsensitiveMap<>(item);
                        Object value = map.get(keyChild);
                        if (null == value) {
                            value = "";
                        }
                        ExcelWriterUtil.addCellData(resultData, childIndex++, columnIndex, value);
                    }
                } else if (o instanceof Map) {
                    Map<String, Object> object = (Map<String, Object>) o;
                    Object value = object.get(keyChild);
                    if (null == value) {
                        value = "";
                    }
                    ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, value);
                } else {
                    if (null == o) {
                        o = "";
                    }
                    ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, o);
                }
            }
        }
        return resultData;
    }

    /**
     *      *
     *      * @param ImgPath:图片路径
     *      * @param row1：起始行
     *      * @param row2：终止行
     *      * @param col1：起始列
     *      * @param col2：终止列
     *      * @throws IOException
     *     
     */
    public static void setImg(Workbook workbook, byte[] bytes, String sheetName, int row1, int row2, int col1, int col2) {
        // 插入 PNG 图片至 Excel
        int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        Sheet sheet2 = workbook.getSheet(sheetName);
        CreationHelper helper = workbook.getCreationHelper();
        Drawing drawing = sheet2.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();

        // 图片插入坐标
        anchor.setDx1(0);
        anchor.setDy1(0);
        anchor.setDx2(0);
        anchor.setDy2(0);
        anchor.setRow1(row1);
        anchor.setRow2(row2);
        anchor.setCol1(col1);
        anchor.setCol2(col2);
        // 插入图片
        drawing.createPicture(anchor, pictureIdx);

    }

    /**
     * 重载handlerRowData方法 处理每一行数据
     * 列名格式为 aasdf/asdf 下划线分隔
     *
     * @param columns tag点的别名列
     * @param tagColumns tag点名列
     * @param starRow 开始行
     * @param rowData 每一行对应的数据
     * @return List<CellData>
     */
    public static List<CellData> handlerRowData(List<String> columns, List<String> tagColumns, int starRow, Map<String, Object> rowData) {
        List<CellData> resultData = new ArrayList<>();
        int size = columns.size();
        // 忽略大小写
        CaseInsensitiveMap<String, Object> rowDataMap = new CaseInsensitiveMap<>(rowData);
        for (int columnIndex = 0; columnIndex < size; columnIndex++) {
            String column = columns.get(columnIndex);
            if (StringUtils.isBlank(column)) {
                continue;
            }
            column = getMatchTagName(column, tagColumns);
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
                    if (null == value) {
                        value = "";
                    }
                    ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, value);
                } else if (o instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) o;
                    int childIndex = starRow;
                    for (Object obj : jsonArray) {
                        JSONObject item = (JSONObject) obj;
                        CaseInsensitiveMap<String, Object> map = new CaseInsensitiveMap<>(item);
                        Object value = map.get(keyChild);
                        if (null == value) {
                            value = "";
                        }
                        ExcelWriterUtil.addCellData(resultData, childIndex++, columnIndex, value);
                    }
                } else if (o instanceof Map) {
                    Map<String, Object> object = (Map<String, Object>) o;
                    Object value = object.get(keyChild);
                    if (null == value) {
                        value = "";
                    }
                    ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, value);
                } else {
                    if (null == o) {
                        o = "";
                    }
                    ExcelWriterUtil.addCellData(resultData, starRow, columnIndex, o);
                }
            }
        }

        return resultData;
    }

    /**
     * 通过tag点别名找到tag点
     * @param column tag点的别名
     * @param tagColumns 所有tag点别名对应的tag点
     * @return tag点
     */
    public static String getMatchTagName(String column, List<String> tagColumns) {
        if (Objects.nonNull(tagColumns) && tagColumns.size() > 0) {
            for (String tagColumn : tagColumns) {
                String noPrefixTagColumn = column.substring(3);
                if (tagColumn.indexOf(noPrefixTagColumn) != -1) {
                    return tagColumn;
                }
            }
        }

        return column;
    }

    /**
     * 处理一个别名对应多个tag点的问题
     * @param executeWay 处理方法
     * @param specialValues 被处理的List
     * @return
     */
    public static double executeSpecialList(String executeWay, List<Double> specialValues) {
        double val = 0.0d;

        if (executeWay != "" && Objects.nonNull(specialValues) && specialValues.size() > 0) {
            switch (executeWay) {
                case "max":
                    val = specialValues.stream().mapToDouble(Double :: doubleValue).max().getAsDouble();
                    break;
                case "min":
                    val = specialValues.stream().mapToDouble(Double :: doubleValue).min().getAsDouble();
                    break;
                case "avg":
                    val = specialValues.stream().mapToDouble(Double :: doubleValue).average().getAsDouble();
                    break;
                case "sum":
                    // 解决sum求和时，使用double可能造成精度损失问题，使用BigDecimal处理
                    for (Double item: specialValues) {
                        BigDecimal p1 = new BigDecimal(Double.toString(item));
                        BigDecimal p2 = new BigDecimal(Double.toString(val));
                        val = p1.add(p2).doubleValue();
                    }
                    break;
                default:
                    // 默认求list中最大值
                    val = specialValues.stream().mapToDouble(Double :: doubleValue).max().getAsDouble();
            }
        }

        return val;
    }

    /**
     * 动态替换报表首行标题中的日期
     * @param sheet
     * @param date
     * @param date
     */
    public static void replaceCurrentDateInTitle(Sheet sheet, String placeHolder, Date date) {
        Cell titleCell = PoiCustomUtil.getCellByValue(sheet, placeHolder);
        String stringCellValue = titleCell.getStringCellValue();
        String currentDate = DateFormatUtils.format(date, DateUtil.yyyyMMddChineseFormat);
        stringCellValue = stringCellValue.replaceAll(placeHolder, currentDate);
        titleCell.setCellValue(stringCellValue);
    }

    /**
     * 动态替换报表首行标题中的日期
     * @param sheet
     * @param row
     * @param column
     * @param date
     */
    public static void replaceCurrentMonthInTitle(Sheet sheet, int row, int column, Date date) {
        Cell titleCell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, row), column);
        String stringCellValue = titleCell.getStringCellValue();
        String currentMonth = DateFormatUtils.format(date, DateUtil.yyyyMM);
        stringCellValue = stringCellValue.replaceAll("%当前月份%", currentMonth);
        titleCell.setCellValue(stringCellValue);
    }

    /**
     * 动态替换报表中的当月天数
     * @param sheet
     * @param row
     * @param column
     * @param date
     */
    public static void replaceDaysOfMonthInTitle(Sheet sheet, int row, int column, Date date) {
        Cell titleCell = ExcelWriterUtil.getCellOrCreate(ExcelWriterUtil.getRowOrCreate(sheet, row), column);
        String stringCellValue = titleCell.getStringCellValue();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int currentMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        stringCellValue = stringCellValue.replaceAll("%当月天数%", String.valueOf(currentMonth));
        titleCell.setCellValue(stringCellValue);
    }
}
