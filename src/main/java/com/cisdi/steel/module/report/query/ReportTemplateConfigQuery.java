package com.cisdi.steel.module.report.query;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import com.cisdi.steel.common.base.vo.PageQuery;

/**
 * <p>Description: 报表动态模板配置 查询参数(不需要可删除) </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ReportTemplateConfigQuery extends PageQuery implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 模板的名称
     */
    private String templateName;
    /**
     * 模板生成所在临时路径
     */
    private String templatePath;
    /**
     * 时间划分方式
     */
    private Integer timeDivideType;
    /**
     * 开始时间
     */
    private String startTimeslot;
    /**
     * 结束时间
     */
    private String endTimeslot;
    /**
     * 时间间隔
     */
    private String timeslotInterval;
    /**
     * 是否添加平均值
     */
    private String isAddAvg;
    /**
     * 平均值计算方式
     */
    private Integer avgDivideType;
}
