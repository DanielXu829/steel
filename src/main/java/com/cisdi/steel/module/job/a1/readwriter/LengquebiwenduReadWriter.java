package com.cisdi.steel.module.job.a1.readwriter;

import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.strategy.TagStrategy;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.List;

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
public class LengquebiwenduReadWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        SheetRowCellData sheetRowCellData = this.requestData(excelDTO.getTemplate(), excelDTO.getDateQuery());
        sheetRowCellData.allValueWriteExcel();
        return sheetRowCellData.getWorkbook();
    }

    /**
     * 请求数据
     *
     * @param template  模板
     * @param dateQuery 查询时间
     * @return 结果
     */
    private SheetRowCellData requestData(ReportCategoryTemplate template, DateQuery dateQuery) {
        Workbook workbook = this.getWorkbook(template.getTemplatePath());
        // 第一个sheet值
        Sheet sheet = this.getSheet(workbook, "_tag_hour_each", template.getTemplatePath());
        List<DateQuery> queryList = DateQueryUtil.buildHourEach(dateQuery.getRecordDate());
        TagStrategy tagStrategy = new TagStrategy(template, httpUtil, httpProperties);
        return tagStrategy.execute(workbook, sheet, queryList);
    }
}
