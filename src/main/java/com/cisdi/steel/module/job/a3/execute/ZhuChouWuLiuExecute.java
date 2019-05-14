package com.cisdi.steel.module.job.a3.execute;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a3.writer.ZhuChouWuLiuWriter;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * 5烧6烧主抽电耗跟踪表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@Slf4j
public class ZhuChouWuLiuExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ZhuChouWuLiuWriter zhuChouWuLiuWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return zhuChouWuLiuWriter;
    }

    @Override
    public void createFile(Workbook workbook, ExcelPathInfo excelPathInfo, WriterExcelDTO writerExcelDTO, DateQuery dateQuery) throws IOException {
        // 隐藏 下划线的sheet  强制计算
        FileOutputStream fos = new FileOutputStream(excelPathInfo.getSaveFilePath());
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            // 以下划线开头的全部隐藏掉
            if (sheetName.startsWith("_")) {
                if (sheet.isSelected()) {
                    sheet.setSelected(false);
                }
                workbook.setSheetHidden(i, Workbook.SHEET_STATE_HIDDEN);
            }
        }
        workbook.setForceFormulaRecalculation(true);
        workbook.write(fos);
        fos.close();
        //月末清除模板数据到最初
        String formatDateTime = DateUtil.getFormatDateTime(dateQuery.getRecordDate(), DateUtil.yyyyMMFormat);
        String formatDateTime1 = DateUtil.getFormatDateTime(new Date(), DateUtil.yyyyMMFormat);
        if (!formatDateTime.equals(formatDateTime1)) {
            FileOutputStream modelFos = new FileOutputStream(writerExcelDTO.getTemplate().getTemplatePath());
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                //清楚隐藏表
                if (sheetName.startsWith("_")) {
                    for (int j = 1; j < 500; j++) {
                        Row row = sheet.getRow(j);
                        if (Objects.nonNull(row)) {
                            for (int k = 0; k < 300; k++) {
                                Cell cell = row.getCell(k);
                                if (Objects.nonNull(cell)) {
                                    cell.setCellValue("");
                                    cell.setCellType(CellType.STRING);
                                }
                            }
                        }
                    }
                }
                //清chu主表手输数据
                if ("主抽数据".equals(sheetName)) {
                    for (int j = 4; j < 97; j++) {
                        Row row = sheet.getRow(j);
                        if (Objects.nonNull(row)) {
                            //4 5 8 9 14 15 16 17 19 20 23 24 29 30 31 32 33 34 36 37
                            int[] a = {4, 5, 8, 9, 14, 15, 16, 17, 19, 20, 23, 24, 29, 30, 31, 32, 33, 34, 36, 37};
                            for (int k = 0; k < a.length; k++) {
                                Cell cell = row.getCell(a[k]);
                                if (Objects.nonNull(cell)) {
                                    cell.setCellValue("");
                                    cell.setCellType(CellType.STRING);
                                }
                            }

                        }
                    }
                }
                if ("错峰用电".equals(sheetName)) {
                    for (int j = 3; j < 189; j++) {
                        Row row = sheet.getRow(j);
                        if (Objects.nonNull(row)) {
                            //4 5 8 9 14 15 16 17 19 20 23 24 29 30 31 32 33 34 36 37
                            int[] a = {7, 8, 22, 23};
                            for (int k = 0; k < a.length; k++) {
                                Cell cell = row.getCell(a[k]);
                                if (Objects.nonNull(cell)) {
                                    cell.setCellValue("");
                                    cell.setCellType(CellType.STRING);
                                }
                            }

                        }
                    }
                }
                if ("5烧主抽电耗".equals(sheetName) || "6烧主抽电耗".equals(sheetName)) {
                    for (int j = 2; j < 95; j++) {
                        Row row = sheet.getRow(j);
                        if (Objects.nonNull(row)) {
                            //4 5 8 9 14 15 16 17 19 20 23 24 29 30 31 32 33 34 36 37
                            int[] a = {23, 24, 25};
                            for (int k = 0; k < a.length; k++) {
                                Cell cell = row.getCell(a[k]);
                                if (Objects.nonNull(cell)) {
                                    cell.setCellValue("");
                                    cell.setCellType(CellType.STRING);
                                }
                            }

                        }
                    }
                }
            }
            workbook.setForceFormulaRecalculation(true);
            workbook.write(modelFos);
            modelFos.close();
            workbook.close();
        } else {
            workbook.close();
            FileUtils.deleteFile(writerExcelDTO.getTemplate().getTemplatePath());
            FileUtils.copyFile(excelPathInfo.getSaveFilePath(), writerExcelDTO.getTemplate().getTemplatePath());
        }
    }
}
