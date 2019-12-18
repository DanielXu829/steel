package com.cisdi.steel.module.report.query;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import com.cisdi.steel.common.base.vo.PageQuery;

/**
 * <p>Description: 报表动态模板 - 参数列表 查询参数(不需要可删除) </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ReportTemplateTagsQuery extends PageQuery implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 外键，对应report_tempalte_config中的主键
     */
    private Long templateConfigId;
    /**
     * 外键，对应target_management中的主键
     */
    private Long targetId;
    /**
     * 参数排序，定义了excel中的顺序
     */
    private Integer sequence;
}
