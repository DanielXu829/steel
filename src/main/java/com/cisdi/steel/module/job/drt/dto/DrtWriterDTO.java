package com.cisdi.steel.module.job.drt.dto;

import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@Builder(toBuilder = true)
public class DrtWriterDTO implements Serializable {
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

    /**
     * 执行状态
     */
    private JobExecuteEnum jobExecuteEnum;

    /**
     * 记录开始时间
     */
    private Date startTime;
}
