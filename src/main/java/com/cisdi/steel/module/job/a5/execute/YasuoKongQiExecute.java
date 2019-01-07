package com.cisdi.steel.module.job.a5.execute;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a5.writer.ThreeFourKongWriter;
import com.cisdi.steel.module.job.a5.writer.YasuoKongQiWriter;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * 压缩空气生产情况汇总表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class YasuoKongQiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private YasuoKongQiWriter yasuoKongQiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return yasuoKongQiWriter;
    }

    @Override
    public void execute(JobExecuteInfo jobExecuteInfo) {
        super.execute(jobExecuteInfo);
        //生成昨天的
//        super.executeDateParam(jobExecuteInfo, -1);
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

        super.clearTemp(dateQuery, excelPathInfo, writerExcelDTO, "/u01/templates/temp/能介/压缩空气生产情况汇总表.xlsx");
    }
}
