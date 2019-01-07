package com.cisdi.steel.module.job.a5.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a5.writer.GuifengjimeiyajiWriter;
import com.cisdi.steel.module.job.a5.writer.YasuoKongQiWriter;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 柜区风机煤压机时间统计表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GuifengjimeiyajiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private GuifengjimeiyajiWriter guifengjimeiyajiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return guifengjimeiyajiWriter;
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

        super.clearTemp(dateQuery, excelPathInfo, writerExcelDTO, "/u01/templates/temp/能介/柜区风机煤压机时间统计表.xlsx");
    }
}
