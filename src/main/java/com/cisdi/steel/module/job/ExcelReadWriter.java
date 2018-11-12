package com.cisdi.steel.module.job;

import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.strategy.ExecuteInfo;
import com.cisdi.steel.module.job.strategy.date.DateStrategy;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 默认实现 数据写入
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ExcelReadWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            if (Objects.nonNull(sheet) && sheet.getSheetName().startsWith("_")) {
                // 获取的对应的策略
                ExecuteInfo executeInfo = strategyContext.getApiBySheetName(sheet.getSheetName());
                if (Objects.nonNull(executeInfo)) {
                    // 1、时间处理 变成一个时间范围
                    DateStrategy dateStrategy = executeInfo.getDateStrategy();
                    DateQuery handlerDate = dateStrategy.handlerDate(date.getRecordDate());
                    // 2、option处理
                    List<DateQuery> dateQueries = executeInfo.getOptionsStrategy().execute(handlerDate);
                    // 3、接口处理
                    SheetRowCellData execute = executeInfo.getApiStrategy()
                            .execute(workbook, sheet, dateQueries);
                    // 设置所有值
                    execute.allValueWriteExcel();
                }
            }
        }
        return workbook;
    }
}
