package com.cisdi.steel.module.job.a5.writer;

import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.strategy.ExecuteInfo;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class BaseNjWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (Objects.nonNull(sheet) && sheet.getSheetName().contains("_")) {
                ExecuteInfo executeInfo = strategyContext.getApiBySheetName(sheet.getSheetName());
                if (Objects.nonNull(executeInfo)) {
                    List<DateQuery> dateQueries = new ArrayList<>();
                    dateQueries.add(excelDTO.getDateQuery());
                    SheetRowCellData execute = executeInfo.getApiStrategy().execute(workbook, sheet, dateQueries);
                    execute.allValueWriteExcel();
                }
            }
        }
        return workbook;
    }
}
