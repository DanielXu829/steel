package com.cisdi.steel.module.job.drt.dto;

import com.cisdi.steel.module.job.enums.JobExecuteEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class DrtJobExecuteInfo {
    /**
     * 模板id
     */
    private Long reportCategoryTemplateId;
    /**
     * 模板code
     */
    private String reportCategoryCode;

    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 执行的状态
     * 如果值为null 表示自动
     */
    private JobExecuteEnum jobExecuteEnum;

    /**
     * 查询的时间
     * 如果为null 表示 查询当前时间
     */
    private DateQuery dateQuery;

    /**
     * 重新生成时报表ID
     */
    private Long indexId;

    /**
     * 序号
     */
    private String sequence;
}
