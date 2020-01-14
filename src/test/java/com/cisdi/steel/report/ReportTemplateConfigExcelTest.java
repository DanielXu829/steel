package com.cisdi.steel.report;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.report.enums.SequenceEnum;
import com.cisdi.steel.module.report.util.ExcelStyleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * - 如何动态设置多行表头
 * - 如何动态创建sheet以及插入公式链接
 * - 如何左边和上面空出行
 * - 如何动态添加行
 * TODO - 如何动态添加边框
 * TODO - 如何动态设置背景颜色
 * TODO - 如何设置斜线表头
 * TODO - 如何设置单元格高宽，字体大小，加粗
 */
@Slf4j
public class ReportTemplateConfigExcelTest extends SteelApplicationTests {

    @Autowired
    JobProperties jobProperties;

    @Test
    public void testExcelTemplate() throws Exception{
        String templatePath = jobProperties.getTemplatePath();
        Workbook workbook = WorkbookFactory.create(POICacheManager.getFile(templatePath + File.separator + "动态报表模板" + File.separator + "基础模板1.xlsx"));
        Sheet sheetAt = workbook.getSheetAt(0);
        System.out.println(sheetAt.getSheetName());

        workbook.createSheet("sheet2");

        String tempPath = jobProperties.getTempPath();
        String excelFileName = new StringBuilder().append(tempPath).append(File.separator)
                .append("test").append(System.currentTimeMillis()).append(".xlsx").toString();
        FileOutputStream fos = new FileOutputStream(excelFileName);
        workbook.write(fos);
        fos.close();

        System.out.println(excelFileName);

    }

    @Test
    public void testEasyPOI() {
        try {
            List<ExcelExportEntity> entity = new ArrayList<ExcelExportEntity>();
            //构造对象等同于@Excel
            ExcelExportEntity timeCol = new ExcelExportEntity("时间", "time");
            entity.add(timeCol);

            ExcelExportEntity nameCol = new ExcelExportEntity("姓名", "name");
            nameCol.setGroupName("name");
            entity.add(nameCol);

            ExcelExportEntity sexCol = new ExcelExportEntity("性别", "sex");
            sexCol.setGroupName("sex");
            entity.add(sexCol);

            //添加list
            ExcelExportEntity studentsCol = new ExcelExportEntity("学生", "students");
            List<ExcelExportEntity> studentColList = new ArrayList<ExcelExportEntity>();
            ExcelExportEntity studentNameCol = new ExcelExportEntity("学生姓名", "stuname");
            studentColList.add(studentNameCol);

            //设置学生姓名英文名和中文名
            List<ExcelExportEntity> studentNameColList = new ArrayList<ExcelExportEntity>();
            ExcelExportEntity studentNameChineseCol = new ExcelExportEntity("中文姓名", "stuname_chinese");
            studentNameColList.add(studentNameChineseCol);
            ExcelExportEntity studentNameEnglishCol = new ExcelExportEntity("英文姓名", "stuname_english");
            studentNameColList.add(studentNameEnglishCol);
            studentNameCol.setList(studentNameColList);

            ExcelExportEntity studentSexCol = new ExcelExportEntity("学生姓别", "stusex");
            studentColList.add(studentSexCol);

            //构造List等同于@ExcelCollection
            studentsCol.setList(studentColList);

            entity.add(studentsCol);

            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            //把我们构造好的bean对象放到params就可以了
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("测试", "测试"), entity, list);
            FileOutputStream fos = new FileOutputStream("C:/testing/ExcelExportForMap.xls");
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testReportTemplateExport() throws Exception{
        try {
            List<ExcelExportEntity> entity = new ArrayList<ExcelExportEntity>();
            //构造对象等同于@Excel
            ExcelExportEntity blankCol = new ExcelExportEntity(" ", "blank");
            entity.add(blankCol);

            ExcelExportEntity timeCol = new ExcelExportEntity("时间", "time");
            timeCol.setGroupName("项目");
            entity.add(timeCol);

            ExcelExportEntity nameCol = new ExcelExportEntity("批/h", "name");
            nameCol.setGroupName("小时料批");
            entity.add(nameCol);

            ExcelExportEntity sexCol = new ExcelExportEntity("批", "sex");
            sexCol.setGroupName("累计料批");
            entity.add(sexCol);

            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            Map<String, Object> data1 = new LinkedHashMap<String, Object>();
            data1.put("blank", "");
            data1.put("time", "1:00");
            data1.put("name", "11");
            data1.put("sex", "11");
            list.add(data1);

            Map<String, Object> data2 = new LinkedHashMap<String, Object>();
            data2.put("blank", "");
            data2.put("time", "2:00");
            data2.put("name", "123");
            data2.put("sex", "12");

            list.add(data2);
            //把我们构造好的bean对象放到params就可以了
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("8高炉小时炉况输入表", "Sheet1", ExcelType.XSSF), entity, list);

            //创建第二个sheet, 并且在第二个sheet中输入点位。
//            createTagsSheet(workbook);
//
//
//            //在第一个sheet中写入公式。=IF(_lukuang_day_all!C2="","",_lukuang_day_all!C2)
//            Sheet sheet1 = workbook.getSheetAt(0);
//            //在最顶部插入空行 TODO 插入后，合并单元格消失。
//            sheet1.shiftRows(0, sheet1.getLastRowNum(), 1);
//
//            Row firstDataRow = ExcelWriterUtil.getRowOrCreate(sheet1,4);
//            Cell cell = firstDataRow.getCell(2);
//            cell.setCellFormula("IF(_xiaoshilukuang_day_hour!C2=\"\",\"N/A\",_xiaoshilukuang_day_hour!C2)");
//            cell.setCellType(CellType.FORMULA);
//
//            //创建_dictionary sheet
//            createDictionarySheet(workbook);

            // 输出excel到文件中。
            String excelName = "C:/testing/ExcelExportForMap_" + System.currentTimeMillis() + ".xlsx";
            FileOutputStream fos = new FileOutputStream(excelName);
            workbook.write(fos);
            fos.close();

            System.out.println("生成excel成功：" + excelName);
        } catch (Exception e) {
            throw e;
        }
    }


    @Test
    public void testGenerateTableHeader() throws Exception{
        try {
            String firstSheetName = "report_info";
            String excelTitle = "模板标题";
            int titleLastColumnIndexForMerge = 5;
            Workbook workbook = new XSSFWorkbook();
            //创建第一个sheet
            Sheet firstSheet = workbook.createSheet(firstSheetName);
            //添加空白行
            Row firstBlankRow = ExcelWriterUtil.getRowOrCreate(firstSheet, 0);
            //设置标题
            Row secondTitleRow = ExcelWriterUtil.getRowOrCreate(firstSheet, 1);
            PoiMergeCellUtil.addMergedRegion(firstSheet, 1,1,1, titleLastColumnIndexForMerge);
            Cell titleCell = ExcelWriterUtil.getCellOrCreate(secondTitleRow, 1);
            titleCell.setCellValue(excelTitle);
            CellStyle titleCellStyle = workbook.createCellStyle();
            titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
            titleCell.setCellStyle(titleCellStyle);
            titleCell.setCellType(CellType.STRING);

            //创建表头
            Row thirdTagsRow = ExcelWriterUtil.getRowOrCreate(firstSheet, 2);
            Cell tagsHeader = ExcelWriterUtil.getCellOrCreate(thirdTagsRow, 1);
            tagsHeader.setCellValue("项目");
            Cell tags1 = ExcelWriterUtil.getCellOrCreate(thirdTagsRow, 2);
            tags1.setCellValue("温度");
            Cell tags2 = ExcelWriterUtil.getCellOrCreate(thirdTagsRow, 3);
            tags2.setCellValue("风量");

            //创建单位列
            Row fourthTagsRow = ExcelWriterUtil.getRowOrCreate(firstSheet, 3);
            Cell timesHeader = ExcelWriterUtil.getCellOrCreate(fourthTagsRow, 1);
            timesHeader.setCellValue("时间");
            Cell unit1 = ExcelWriterUtil.getCellOrCreate(fourthTagsRow, 2);
            unit1.setCellValue("℃");
            Cell unit2 = ExcelWriterUtil.getCellOrCreate(fourthTagsRow, 3);
            unit2.setCellValue("Nm3/min");

            // 输出excel到文件中。
            String excelName = "C:/testing/ExcelExportForMap_" + System.currentTimeMillis() + ".xlsx";
            FileOutputStream fos = new FileOutputStream(excelName);
            workbook.write(fos);
            fos.close();

            System.out.println("生成excel成功：" + excelName);
        } catch (Exception e) {
            throw e;
        }
    }

    private void createTagsSheet(Workbook workbook) {
        Sheet secondSheet = workbook.createSheet("_xiaoshilukuang_day_hour");
        List<CellData> cellDataList = new ArrayList<CellData>();
        ExcelWriterUtil.addCellData(cellDataList, 0, 0, "tags1");
        ExcelWriterUtil.addCellData(cellDataList, 0, 1, "tags2");
        ExcelWriterUtil.addCellData(cellDataList, 0, 2, "tags3");

        SheetRowCellData.builder()
            .cellDataList(cellDataList)
            .sheet(secondSheet)
            .workbook(workbook)
            .build().allValueWriteExcel();
    }

    private void createDictionarySheet(Workbook workbook) {
        Sheet dictionarySheet = workbook.createSheet("_dictionary");
        Row dictionarySheetFirstRow = ExcelWriterUtil.getRowOrCreate(dictionarySheet,0);
        Cell dictionarySheetFirstRowCell1 = ExcelWriterUtil.getCellOrCreate(dictionarySheetFirstRow, 0);
        dictionarySheetFirstRowCell1.setCellValue("version");

        Cell dictionarySheetFirstRowCell2 = ExcelWriterUtil.getCellOrCreate(dictionarySheetFirstRow, 1);
        ;
        dictionarySheetFirstRowCell2.setCellValue(SequenceEnum.getVersion("高炉8"));
    }

    /**
     * TODO 导入获取Key-Value
     */
    @Test
    public void testKeyValue() {
        try {
            ImportParams params = new ImportParams();
            params.setKeyMark("：");
            params.setReadSingleCell(true);
            params.setTitleRows(7);
            params.setLastOfInvalidRow(9);
            ExcelImportResult<Map> result = ExcelImportUtil.importExcelMore(
                    new File("C:/testing/ExcelExportForKeyvalue.xlsx"),
                    Map.class, params);
            for (int i = 0; i < result.getList().size(); i++) {
                System.out.println(result.getList().get(i));
            }
            Assert.assertTrue(result.getList().size() == 10);
            System.out.println(result.getMap());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }


    @Test
    public void testReportTemplateConfigExport() {
        try {
            List<ExcelExportEntity> entity = new ArrayList<ExcelExportEntity>();
//构造对象等同于@Excel
            ExcelExportEntity excelentity = new ExcelExportEntity("姓名", "name");
            excelentity.setNeedMerge(true);
            entity.add(excelentity);
            entity.add(new ExcelExportEntity("性别", "sex"));
            excelentity = new ExcelExportEntity(null, "students");


            List<ExcelExportEntity> temp = new ArrayList<ExcelExportEntity>();
            temp.add(new ExcelExportEntity("姓名", "name"));
            temp.add(new ExcelExportEntity("性别", "sex"));
//构造List等同于@ExcelCollection
            excelentity.setList(temp);

            entity.add(excelentity);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//把我们构造好的bean对象放到params就可以了
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("测试", "测试"), entity, list);
            FileOutputStream fos = new FileOutputStream("C:/testing/ExcelExportForMap.xlsx");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testCellStype() {
        try {
            String templatePath = "C:\\testing\\高炉动态报表1_2020-01-06_19.xlsx";
            Workbook workbook = WorkbookFactory.create(POICacheManager.getFile(templatePath));

            Sheet sheet = workbook.getSheetAt(0);
            Cell cellOrCreate = ExcelWriterUtil.getCellOrCreate(sheet.getRow(6), 2);
            System.out.println("原始值：" + cellOrCreate.getNumericCellValue());

            CellStyle cellStyle = ExcelStyleUtil.getCellStyle(workbook);

            // 设置小数点位数
            DataFormat dataFormat = workbook.createDataFormat();//此处设置数据格式
            int scale = 1;
            String format = "0";
            if (scale > 0) {
                format = format + ".";
                for (int i = 0; i < scale; i++) {
                    format = format + "0";
                }
            }
            cellStyle.setDataFormat(dataFormat.getFormat(format)); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));
            cellOrCreate.setCellStyle(cellStyle);


            // 输出excel到文件中。
            String excelName = "C:/testing/ExcelExportForTesting_" + System.currentTimeMillis() + ".xlsx";
            FileOutputStream fos = new FileOutputStream(excelName);
            workbook.write(fos);
            fos.close();

            System.out.println("生成excel成功：" + excelName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
