package com.cisdi.steel;

import cn.afterturn.easypoi.util.PoiCellUtil;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * <p>Description: 普通测试类  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/27 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class CommonTests {

    @Test
    public void test(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        for(int i=0;i<24;i++){
            GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), i, 0);
            Date time = gregorianCalendar.getTime();
            System.err.println(DateUtil.getFormatDateTime(time,DateUtil.hhmmFormat));
        }
    }

    @Test
    public void test2() throws Exception {
        String fileName = "D:\\template\\出鐵作業日報表.xlsx";
        String saveFilePath = "D:\\1.xlsx";

        Workbook sheets = WorkbookFactory.create(new File(fileName));
        Sheet sheet = sheets.getSheet("_tag1");
        int firstRowNum = sheet.getFirstRowNum();
        Row row = sheet.getRow(firstRowNum);
        short firstCellNum = row.getFirstCellNum();
        short lastCellNum = row.getLastCellNum();

        String sheetName = row.getSheet().getSheetName();

        Row row2 = sheet.createRow(1);

        for (int i = firstCellNum; i < lastCellNum; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellValue(RandomUtils.nextInt(0,99));
//            System.err.println(cell.getStringCellValue());
        }
        System.err.println(1);
        FileOutputStream fos = new FileOutputStream(saveFilePath);
        sheets.write(fos);
        fos.close();
    }

    @Test
    public void test1() throws Exception {
        String fileName = "D:\\template\\出鐵作業日報表.xlsx";
        String saveFilePath = "D:\\1.xlsx";

        Workbook workbook = WorkbookFactory.create(new File(fileName));
        Sheet sheet = getBySheetName(workbook, "_tag1");

        List<String> sheetRowCelVal = PoiCustomUtil.getFirstRowCelVal(sheet);

        List<List<Integer>> data = getData();
        int row = 0;
        for (List<Integer> a : data) {
            Row row1 = sheet.createRow(++row);
            Integer firstCol = null;
            for (int i = 0; i < sheetRowCelVal.size(); i++) {
                Cell cell = row1.createCell(i);
                if (Objects.isNull(firstCol) && StringUtils.isBlank(sheetRowCelVal.get(i))) {
                    firstCol = i;
                } else {
                    if (Objects.nonNull(firstCol) && StringUtils.isNotBlank(sheetRowCelVal.get(i))) {
                        PoiMergeCellUtil.addMergedRegion(sheet, row, row, firstCol, firstCol - 1);
                        firstCol = null;
                    }
                }
                cell.setCellValue(a.get(i));

            }
        }
        workbook.setForceFormulaRecalculation(true);
        FileOutputStream fos = new FileOutputStream(saveFilePath);
        workbook.write(fos);
        fos.close();
    }

    private List<List<Integer>> getData() {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            List<Integer> a = new ArrayList<>();
            for (int j = 0; j < 50; j++) {
                a.add(RandomUtils.nextInt(0, 99));
            }
            result.add(a);
        }
        return result;
    }

    private Sheet getBySheetName(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (Objects.isNull(sheet)) {
            throw new NullPointerException("sheetName 不存在");
        }
        return sheet;
    }

    private List<String> getSheetRowCelVal(Sheet sheet, Integer rowNum) {
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
     * 获取指定行的值
     *
     * @param workbook  文件
     * @param sheetName 指定的sheetName
     */
    private String getSheetCell(Workbook workbook, String sheetName, Integer rowNum, Integer columnNum) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (Objects.isNull(sheet)) {
            throw new NullPointerException("sheetName 不存在");
        }
        return PoiCellUtil.getCellValue(sheet, rowNum, columnNum);
    }

}
