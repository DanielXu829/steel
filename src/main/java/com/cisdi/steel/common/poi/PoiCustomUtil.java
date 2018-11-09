package com.cisdi.steel.common.poi;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.MetadataDTO;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * <p>Description:  自定义poi工具类  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/30 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class PoiCustomUtil {


    /**
     * 获取第一行的值
     *
     * @param sheet sheet值
     * @return 结果
     */
    public static List<String> getFirstRowCelVal(Sheet sheet) {
        return getRowCelVal(sheet, 0);
    }

    /**
     * 获取 某一行的值
     *
     * @param sheet  sheet文件
     * @param rowNum 指定行
     * @return 某一行的数据
     */
    public static List<String> getRowCelVal(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        short lastCellNum = row.getLastCellNum();
        List<String> result = new ArrayList<>();
        for (int index = 0; index < lastCellNum; index++) {
            Cell cell = row.getCell(index);
            if (Objects.isNull(cell)) {
                continue;
            }
            result.add(PoiCellUtil.getCellValue(cell));
        }
        return result;
    }

    public static List<Cell> getFirstRowCel(Sheet sheet) {
        return getRowCel(sheet, 0);
    }

    /**
     * 获取 cell单元格
     *
     * @param sheet  表格
     * @param rowNum 行
     * @return 结果
     */
    public static List<Cell> getRowCel(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        short lastCellNum = row.getLastCellNum();
        List<Cell> result = new ArrayList<>();
        for (int index = 0; index < lastCellNum; index++) {
            Cell cell = row.getCell(index);
            if (Objects.nonNull(cell)) {
                result.add(cell);
            }
        }
        return result;
    }

    /**
     * 获取第一列所有值
     *
     * @param sheet 指定sheet
     * @return 结果
     */
    public static List<String> getFirstColumnCellVal(Sheet sheet) {
        return getColumnCellVal(sheet, 0);
    }

    /**
     * 获取某一列的所有值
     *
     * @param sheet     当前sheet
     * @param columnNum 指定列
     * @return 结果
     */
    public static List<String> getColumnCellVal(Sheet sheet, int columnNum) {
        List<String> result = new ArrayList<>();
        if (Objects.isNull(sheet)) {
            return result;
        }
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        for (int rowNum = firstRowNum; rowNum < lastRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (Objects.isNull(row)) {
                continue;
            }
            Cell cell = row.getCell(columnNum);
            result.add(PoiCellUtil.getCellValue(cell));
        }
        return result;
    }

    /**
     * 获取某一行的值
     *
     * @param filePath  文件路径
     * @param sheetName sheetName
     * @param rowNum    行
     * @return 结果
     */
    public static List<String> getRowCelVal(String filePath, String sheetName, int rowNum) {
        Workbook workBook = getWorkBook(filePath);
        Sheet sheet = Objects.requireNonNull(workBook).getSheet(sheetName);
        return getRowCelVal(sheet, rowNum);
    }

    /**
     * 获取指定单元格的值
     *
     * @param workbook  文件
     * @param sheetName sheetName
     * @param rowNum    行
     * @param columnNum 列
     * @return 单元格的值
     */
    public static String getSheetCell(Workbook workbook, String sheetName, Integer rowNum, Integer columnNum) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (Objects.isNull(sheet)) {
            throw new NullPointerException("sheetName is not exists");
        }
        return PoiCellUtil.getCellValue(sheet, rowNum, columnNum);
    }

    /**
     * 获取指定单元格的值
     *
     * @param filePath  文件路径
     * @param sheetName sheetName
     * @param rowNum    行
     * @param columnNum 列
     * @return 单元格的值
     */
    public static String getSheetCell(String filePath, String sheetName, Integer rowNum, Integer columnNum) {
        Workbook workBook = getWorkBook(filePath);
        return getSheetCell(Objects.requireNonNull(workBook), sheetName, rowNum, columnNum);
    }

    /**
     * 单元格设置值
     *
     * @param cell  单元格
     * @param value 值
     */
    public static void setCellValue(Cell cell, Object value) {
        if (Objects.isNull(value) || Objects.isNull(cell)) {
            return;
        }
        if (value instanceof Byte) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(((Byte) value).doubleValue());
        } else if (value instanceof Short) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(((Short) value).doubleValue());
        } else if (value instanceof Integer) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(((Integer) value).doubleValue());
        } else if (value instanceof Float) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(((Float) value).doubleValue());
        } else if (value instanceof Double) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(((Double) value));
        } else if (value instanceof Long) {
            Long result = (Long) value;
            try {
                if (result.toString().length() == 13) {
                    Date date = new Date(result);
                    cell.setCellValue(date);
                } else if (result.toString().length() == 10) {
                    Date date = new Date(result * 1000);
                    cell.setCellValue(date);
                }
            } catch (Exception e) {
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(result.doubleValue());
            }
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
            cell.setCellType(CellType.BOOLEAN);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue(value.toString());
            cell.setCellType(CellType.STRING);
        }
    }

    /**
     * 构建元数据
     * 默认取_metadata
     *
     * @param workbook 当前文件
     * @param excelDTO 数据
     */
    public static void buildMetadata(Workbook workbook, WriterExcelDTO excelDTO) {
        if (Objects.nonNull(workbook)) {
            Sheet sheet = workbook.getSheet("_metadata");
            MetadataDTO metadataDTO = new MetadataDTO(excelDTO);
            if (Objects.nonNull(sheet)) {
                buildMetadata(sheet, metadataDTO);
            } else {
                sheet = workbook.createSheet("_metadata");
                writeAllMetadata(sheet, metadataDTO);
            }
        }
    }

    /**
     * 完全写入数据
     *
     * @param sheet       指定sheet
     * @param metadataDTO 数据
     */
    private static void writeAllMetadata(Sheet sheet, MetadataDTO metadataDTO) {
        Map<String, Object> map = metadataDTO.buildMap();
        int index = 0;
        for (String key : map.keySet()) {
            Row row = sheet.createRow(index);
            Cell cellOne = row.createCell(0);
            Cell cellTwo = row.createCell(1);
            PoiCustomUtil.setCellValue(cellOne, key);
            PoiCustomUtil.setCellValue(cellTwo, map.get(key));
            index++;
        }
    }

    /**
     * 构建元数据
     *
     * @param sheet       sheet
     * @param metadataDTO 数据
     */
    private static void buildMetadata(Sheet sheet, MetadataDTO metadataDTO) {
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        if (firstRowNum == lastRowNum) {
            writeAllMetadata(sheet, metadataDTO);
            return;
        }
        Map<String, Object> map = metadataDTO.buildMap();
        for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (Objects.isNull(row)) {
                continue;
            }
            Cell cell = row.getCell(0);
            String cellValue = PoiCellUtil.getCellValue(cell);
            if (StringUtils.isNotBlank(cellValue)) {
                Object obj = map.get(cellValue);
                Cell cellTwo = row.getCell(1);
                if (Objects.isNull(cellTwo)) {
                    cellTwo = row.createCell(1);
                }
                PoiCustomUtil.setCellValue(cellTwo, obj);
            }
        }
    }

    /**
     * 获取当前文件的workbook
     *
     * @param filePath 文件全路径
     * @return 结果
     */
    private static Workbook getWorkBook(String filePath) {
        try {
            return WorkbookFactory.create(new File(filePath));
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

}
