package com.cisdi.steel.module.job.a2.execute;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.Luwenjilu7Writer;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * 炼焦
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
public class Luwenjilu7Execute extends AbstractJobExecuteExecute {

    @Autowired
    private Luwenjilu7Writer luwenjilu7Writer;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return luwenjilu7Writer;
    }

    @Override
    protected void replaceTemplatePath(ReportIndex reportIndex, ReportCategoryTemplate template) {

    }

    @Override
    public void createFile(Workbook workbook, ExcelPathInfo excelPathInfo, WriterExcelDTO writerExcelDTO, DateQuery dateQuery) throws IOException {
        String version = "67.0";
        try {
            Sheet dictionary = workbook.getSheet("_dictionary");
            version = PoiCellUtil.getCellValue(dictionary, 0, 1);
        } catch (Exception e) {
        }
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
        workbook.close();
        String formatDateTime = DateUtil.getFormatDateTime(dateQuery.getRecordDate(), DateUtil.yyyyMMFormat);
        String formatDateTime1 = DateUtil.getFormatDateTime(new Date(), DateUtil.yyyyMMFormat);
        if (!formatDateTime1.equals(formatDateTime)) {
            String path ="";
            if ("12.0".equals(version)) {
                path = "/u01/templates/焦化/CK12-炼焦-2#炉温记录报表（日）copy.xlsx";
            } else if ("67.0".equals(version)) {
                path = "/u01/templates/焦化/CK67-炼焦-7#炉温记录报表（日）copy.xlsx";
            } else if ("45.0".equals(version)) {
                path = "/u01/templates/焦化/CK45-炼焦-5#炉温记录报表（日）copy.xlsx";
            }
            FileUtils.deleteFile(writerExcelDTO.getTemplate().getTemplatePath());
            //FileUtils.copyFile("D:\\template\\焦化\\CK67-炼焦-7#炉温记录报表（日）copy.xlsx",writerExcelDTO.getTemplate().getTemplatePath());
            FileUtils.copyFile(path, writerExcelDTO.getTemplate().getTemplatePath());
        } else {
            FileUtils.deleteFile(writerExcelDTO.getTemplate().getTemplatePath());
            FileUtils.copyFile(excelPathInfo.getSaveFilePath(), writerExcelDTO.getTemplate().getTemplatePath());
        }
    }

}
