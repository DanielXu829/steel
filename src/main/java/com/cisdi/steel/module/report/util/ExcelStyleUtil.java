package com.cisdi.steel.module.report.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

public class ExcelStyleUtil {

    private static final short FONT_SIZE_ELEVEN = 11;
    private static final short FONT_SIZE_TWELVE = 12;
    private static final short FONT_SIZE_EIGHTEEN = 18;


    private static CellStyle getBaseCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //下边框
        style.setBorderBottom(BorderStyle.THIN);
        //左边框
        style.setBorderLeft(BorderStyle.THIN);
        //上边框
        style.setBorderTop(BorderStyle.THIN);
        //右边框
        style.setBorderRight(BorderStyle.THIN);
        //水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        //上下居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置自动换行
        //style.setWrapText(true);
        return style;
    }

    public static CellStyle getHeaderTitleStyle(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);

        Font font = getFont(workbook, FONT_SIZE_EIGHTEEN, true);
        style.setFont(font);

        //下边框
        style.setBorderBottom(BorderStyle.NONE);
        //左边框
        style.setBorderLeft(BorderStyle.NONE);
        //上边框
        style.setBorderTop(BorderStyle.NONE);
        //右边框
        style.setBorderRight(BorderStyle.NONE);

        return style;
    }

    public static CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);

        Font font = getFont(workbook, FONT_SIZE_ELEVEN, false);
        style.setFont(font);
        //设置背景颜色
        XSSFColor color = new XSSFColor(new java.awt.Color(166, 166, 166));
        ((XSSFCellStyle)style).setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    public static CellStyle getCellStyle(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);

        Font font = getFont(workbook, FONT_SIZE_ELEVEN, false);
        style.setFont(font);

        return style;
    }

    public static CellStyle getCellStyle(Workbook workbook, int scale) {
        CellStyle cellStyle = getCellStyle(workbook);
        String format = "0";
        if (scale > 0) {
            format = format + ".";
            for (int k = 0; k < scale; k++) {
                format = format + "0";
            }
        }
        //此处设置数据格式
        DataFormat dataFormat = workbook.createDataFormat();
        cellStyle.setDataFormat(dataFormat.getFormat(format));

        return cellStyle;
    }

    /**
     * 字体样式
     *
     * @param size   字体大小
     * @param isBold 是否加粗
     * @return
     */
    private static Font getFont(Workbook workbook, short size, boolean isBold) {
        Font font = workbook.createFont();
        //是否加粗
        font.setBold(isBold);
        //字体大小
        font.setFontHeightInPoints(size);
        return font;
    }

}
