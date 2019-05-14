package com.cisdi.steel.module.job.a3.execute;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a3.writer.GycanshuWriter;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 五、六号烧结机主要工艺参数及实物质量情况
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GycanshuExecute extends AbstractJobExecuteExecute {

    @Autowired
    private GycanshuWriter gycanshuWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return gycanshuWriter;
    }


    @Override
    public void createFile(Workbook workbook, ExcelPathInfo excelPathInfo, WriterExcelDTO writerExcelDTO, DateQuery dateQuery) throws IOException {
        /**
         * 23-3
         * 3-7
         * 7-11
         * 11-15
         * 15-19
         * 19-23
         */
        try {
            int dateTime = Integer.valueOf(DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "HH"));
            if ((dateTime > 0 && dateTime < 3) || dateTime == 23) {
                workbook.removeSheetAt(workbook.getSheetIndex("23"));
                workbook.removeSheetAt(workbook.getSheetIndex("19"));
                workbook.removeSheetAt(workbook.getSheetIndex("11"));
                workbook.removeSheetAt(workbook.getSheetIndex("15"));
                workbook.removeSheetAt(workbook.getSheetIndex("7"));
            } else if (dateTime < 7 && dateTime >= 3) {
                workbook.removeSheetAt(workbook.getSheetIndex("23"));
                workbook.removeSheetAt(workbook.getSheetIndex("19"));
                workbook.removeSheetAt(workbook.getSheetIndex("15"));
                workbook.removeSheetAt(workbook.getSheetIndex("11"));
                workbook.removeSheetAt(workbook.getSheetIndex("3"));
            } else if (dateTime < 11 && dateTime >= 7) {
                int sheetIndex = workbook.getSheetIndex("23");
                workbook.removeSheetAt(sheetIndex);
                workbook.removeSheetAt(workbook.getSheetIndex("19"));
                workbook.removeSheetAt(workbook.getSheetIndex("15"));
                workbook.removeSheetAt(workbook.getSheetIndex("7"));
                workbook.removeSheetAt(workbook.getSheetIndex("3"));
            } else if (dateTime < 15 && dateTime >= 11) {
                workbook.removeSheetAt(workbook.getSheetIndex("23"));
                workbook.removeSheetAt(workbook.getSheetIndex("19"));
                workbook.removeSheetAt(workbook.getSheetIndex("11"));
                workbook.removeSheetAt(workbook.getSheetIndex("7"));
                workbook.removeSheetAt(workbook.getSheetIndex("3"));
            } else if (dateTime < 19 && dateTime >= 15) {
                workbook.removeSheetAt(workbook.getSheetIndex("23"));
                workbook.removeSheetAt(workbook.getSheetIndex("15"));
                workbook.removeSheetAt(workbook.getSheetIndex("11"));
                workbook.removeSheetAt(workbook.getSheetIndex("7"));
                workbook.removeSheetAt(workbook.getSheetIndex("3"));
            } else if (dateTime < 23 && dateTime >= 19) {
                workbook.removeSheetAt(workbook.getSheetIndex("19"));
                workbook.removeSheetAt(workbook.getSheetIndex("15"));
                workbook.removeSheetAt(workbook.getSheetIndex("11"));
                workbook.removeSheetAt(workbook.getSheetIndex("7"));
                workbook.removeSheetAt(workbook.getSheetIndex("3"));
            }

        } catch (Exception e) {
        }
        super.createFile(workbook, excelPathInfo, writerExcelDTO, dateQuery);
    }
}
