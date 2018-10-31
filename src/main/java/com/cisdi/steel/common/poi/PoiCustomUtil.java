package com.cisdi.steel.common.poi;

import cn.afterturn.easypoi.util.PoiCellUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    /**
     * 获取指定单元格的值
     *
     * @param workbook  文件
     * @param sheetName 指定的sheetName
     */
    public static String getSheetCell(Workbook workbook, String sheetName, Integer rowNum, Integer columnNum) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (Objects.isNull(sheet)) {
            throw new NullPointerException("sheetName is not exists");
        }
        return PoiCellUtil.getCellValue(sheet, rowNum, columnNum);
    }
}
