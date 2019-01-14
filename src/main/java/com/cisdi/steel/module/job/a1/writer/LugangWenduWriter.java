package com.cisdi.steel.module.job.a1.writer;

import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.strategy.api.ApiStrategy;
import com.cisdi.steel.module.job.strategy.date.DateStrategy;
import com.cisdi.steel.module.job.strategy.options.OptionsStrategy;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/11 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LugangWenduWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            if (sheetName.startsWith("_tag")) {
                String[] split = sheetName.split("_");
                DateStrategy dateStrategy = strategyContext.getDate(split[2]);
                DateQuery handlerDate = dateStrategy.handlerDate(date.getRecordDate());
                OptionsStrategy option = strategyContext.getOption(split[3]);
                List<DateQuery> dateQueries = option.execute(handlerDate);
                ApiStrategy tag = strategyContext.getApi("tag");
                // 3、接口处理
                SheetRowCellData execute = tag.execute(workbook, sheet, dateQueries);
                // 设置所有值
                execute.allValueWriteExcel();
            }
        }
        return workbook;
    }
}
