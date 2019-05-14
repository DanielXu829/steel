package com.cisdi.steel.module.job.a2.execute;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.Luwenjilu6Writer;
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
public class Luwenjilu6Execute extends AbstractJobExecuteExecute {

    @Autowired
    private Luwenjilu6Writer luwenjilu6Writer;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return luwenjilu6Writer;
    }

    @Override
    protected void replaceTemplatePath(ReportIndex reportIndex, ReportCategoryTemplate template) {

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
        workbook.close();
        //月末清除模板数据到最初
        String formatDateTime = DateUtil.getFormatDateTime(dateQuery.getRecordDate(), DateUtil.yyyyMMFormat);
        String formatDateTime1 = DateUtil.getFormatDateTime(new Date(), DateUtil.yyyyMMFormat);
        if (!formatDateTime1.equals(formatDateTime)) {
            FileUtils.deleteFile(writerExcelDTO.getTemplate().getTemplatePath());
            // FileUtils.copyFile("D:\\template\\焦化\\CK67-炼焦-6#炉温记录报表（日）copy.xlsx",writerExcelDTO.getTemplate().getTemplatePath());
            FileUtils.copyFile("/u01/templates/焦化/CK67-炼焦-6#炉温记录报表（日）copy.xlsx", writerExcelDTO.getTemplate().getTemplatePath());
        } else {
            FileUtils.deleteFile(writerExcelDTO.getTemplate().getTemplatePath());
            FileUtils.copyFile(excelPathInfo.getSaveFilePath(), writerExcelDTO.getTemplate().getTemplatePath());
        }
        }

}
