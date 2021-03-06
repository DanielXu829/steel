package com.cisdi.steel;

import cn.afterturn.easypoi.util.PoiCellUtil;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
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

    public static void main(String[] args) {
        Date dateEndTime = DateUtil.getDateEndTime(new Date());
    }

    @Test
    public void test22() throws Exception {
        String path = "C:\\Users\\cj\\Desktop\\8高炉英文\\BF Stave Temperature Daily Report.xlsx";
        Workbook workbook = WorkbookFactory.create(new File(path));

        Sheet sheet = workbook.getSheet("_tag_day_all");
        List<String> celVal = PoiCustomUtil.getFirstRowCelVal(sheet);
        for (int i = 0; i < celVal.size(); i++) {
            String v = celVal.get(i);
            if (StringUtils.isNotBlank(v)) {
                v = "BF8_L2C_BD_" + v + "_1h_avg";
                Row row = sheet.getRow(0);
                if (Objects.isNull(row)) {
                    row = sheet.createRow(0);
                }
                Cell cell = row.getCell(i);
                if (Objects.isNull(cell)) {
                    cell = row.createCell(i);
                }
                PoiCustomUtil.setCellValue(cell, v);
            }
        }

        workbook.setForceFormulaRecalculation(true);
        FileOutputStream fos = new FileOutputStream("C:\\Users\\cj\\Desktop\\8高炉英文1\\BF Proper Temperature Monthly Report.xlsx");
        workbook.write(fos);
        fos.close();
        workbook.close();

    }

    @Test
    public void excelCopySheetTest() throws Exception {
        String path = "D:\\高炉本体温度月报表_2018-11-22_08.xlsx";
        String path1 = "D:\\test3.xlsx";

        Workbook workbook = WorkbookFactory.create(new File(path));
        workbook.cloneSheet(0);

        int numberOfSheets = workbook.getNumberOfSheets();
        String newSheetName = "踩踩踩" + numberOfSheets;
        workbook.setSheetName(numberOfSheets - 1, newSheetName);

        workbook.setForceFormulaRecalculation(true);
        FileOutputStream fos = new FileOutputStream(path1);
        workbook.write(fos);
        fos.close();
        workbook.close();

        del(path);
    }


    public void del(final String path) {

        File file = new File(path);
        if (file.isFile() && file.exists()) {
            file.delete();
        }

        if (file.exists()) {
            String cmd = "cmd /c del ";
            String system = System.getProperties().getProperty("os.name");
            // System.getProperties().getProperty("file.separator");
            if (!system.toLowerCase().startsWith("win")) {
                cmd = "rm -rf ";
            }
            Runtime run = Runtime.getRuntime();
            try {
                cmd = cmd + path;
                System.out.println(cmd);
                Process p = run.exec(cmd);
                p.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        del(path);
                    }
                }, 10000);
            }
        }
    }

    @Test
    public void test() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        List<DateQuery> list = new ArrayList<>();
        for (int i = week - 1; i >= 0; i--) {
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            instance.add(Calendar.DATE, -(i * 7));
            DateQuery dateQuery = getDateQuery(instance.getTime());
            list.add(dateQuery);
        }
        list.forEach(System.out::println);
    }

    private DateQuery getDateQuery(Date date) {
        System.err.println(DateUtil.getFormatDateTime(date, DateUtil.fullFormat));
        Date weekBeginTime = DateUtil.getWeekBeginTime(date);
        Date weekEndTime = DateUtil.getWeekEndTime(date);
        Date dateBeginTime = DateUtil.getDateBeginTime(weekBeginTime);
        Date dateEndTime = DateUtil.getDateEndTime(weekEndTime);
        return new DateQuery(dateBeginTime, dateEndTime, date);
    }

    @Test
    public void test2() throws Exception {
        String fileName = "C:\\Users\\cj\\Desktop\\Main_Daily-主報表.xlsx";
        String saveFilePath = "D:\\1.xlsx";

        Workbook workbook = WorkbookFactory.create(new File(fileName));
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            commonMethod(sheet);
        }
        FileOutputStream fos = new FileOutputStream(saveFilePath);
//        sheets.write(fos);
        fos.close();
    }

    /**
     * 若是Tag类别的就
     *
     * @param sheet
     */
    public void commonMethod(Sheet sheet) {
        String sheetName = sheet.getSheetName();
        String[] s = sheetName.split("_");
        System.out.println(sheetName);
        if (s.length > 1) {
            String mainName = s[1];

            switch (mainName) {
                case "metadata":
                    dealMetadata();
                    break;
                case "Tag":
                    dealTag(sheet);
                    break;
            }
        }

    }

    private void dealMetadata() {
        //查询模版相关信息

    }

    private void dealTag(Sheet sheet) {
        //1.获取报表名称等，跳转到对应的处理类
        int lastRowNum = sheet.getLastRowNum();
        int firstRowNum = sheet.getFirstRowNum();
        for (int j = firstRowNum; j < lastRowNum; j++) {
            Row row = sheet.getRow(j);
            if (null == row) {
                continue;
            }
            short firstCellNum = row.getFirstCellNum();
            short lastCellNum = row.getLastCellNum();
            for (int k = firstCellNum; k < lastCellNum; k++) {
                Cell cell = row.getCell(k);
                if (null == cell) {
                    continue;
                }
                String cellValue = PoiCellUtil.getCellValue(cell);
                if (StringUtils.isNotBlank(cellValue)) {
                    commonDealCol(cellValue);
                }
            }
        }
    }

    //处理每一个字段
    private void commonDealCol(String cellValue) {
        String[] split = cellValue.split("/");
        if (split.length == 1) {//简单处理查询

        } else if (split.length == 2) {//获取第二个参数 例如：MAX

        }
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


    /**
     * 第一行值写入
     *
     * @throws Exception
     */
    @Test
    public void test4() throws Exception {
        String fileName = "C:\\Users\\yp\\Desktop\\需要做的\\高炉冷却壁温度日报表.xlsx";
        String saveFilePath = "D:\\1.xlsx";
        Workbook workbook = WorkbookFactory.create(new File(fileName));
        Sheet sheet = getBySheetName(workbook, "_tag_hour_each");
        Row row = sheet.getRow(sheet.getFirstRowNum());
        short firstCellNum = row.getFirstCellNum();
        short lastCellNum = row.getLastCellNum();
        for (int i = firstCellNum; i < lastCellNum; i++) {
            Cell cell = row.getCell(i);
            if (Objects.nonNull(cell)) {
                String cellValue = PoiCellUtil.getCellValue(cell);
                if (StringUtils.isNotBlank(cellValue)) {
                    PoiCustomUtil.setCellValue(cell, cellValue + "/avg");
                }
            }
        }
        workbook.setForceFormulaRecalculation(true);
        FileOutputStream fos = new FileOutputStream(saveFilePath);
        workbook.write(fos);
        fos.close();
    }

    @Test
    public void test5() {
        List<DateQuery> dateQueries = DateQueryUtil.buildMonthDayEach(new Date());
        if (Objects.nonNull(dateQueries)) {
            dateQueries.forEach(item -> {
                System.err.println(DateUtil.getFormatDateTime(item.getStartTime(), DateUtil.fullFormat) + "--" + DateUtil.getFormatDateTime(item.getEndTime(), DateUtil.fullFormat));
            });
        }

    }
}
