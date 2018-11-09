package com.cisdi.steel.module.job.a1.readwriter;

import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.strategy.ExecuteInfo;
import com.cisdi.steel.module.job.strategy.StrategyFactory;
import com.cisdi.steel.module.job.strategy.date.DateStragegy;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 一个通过的 读取和写入方法
 * 规则统一 就可以使用
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class BaseReadWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery dateQuery = excelDTO.getDateQuery();
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (Objects.nonNull(sheet) && sheet.getSheetName().contains("_")) {
                ExecuteInfo executeInfo = StrategyFactory.getApiBySheetName(sheet.getSheetName());
                if (Objects.nonNull(executeInfo)) {
                    DateStragegy dateStragegy = executeInfo.getDateStragegy();
                    List<DateQuery> dateQueries = dateStragegy.execute(dateQuery.getRecordDate());
                    SheetRowCellData execute = executeInfo.getApiStrategy().execute(workbook, sheet, dateQueries);
                    execute.allValueWriteExcel();
                }
            }
        }
        return workbook;
    }
}
