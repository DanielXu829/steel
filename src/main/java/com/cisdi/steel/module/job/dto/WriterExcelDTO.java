package com.cisdi.steel.module.job.dto;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class WriterExcelDTO {
    /**
     * 模板
     */
    private ReportCategoryTemplate template;

    /**
     * 存储信息
     */
    private ExcelPathInfo excelPathInfo;

    /**
     * 查询时间段
     */
    private DateQuery dateQuery;
}
