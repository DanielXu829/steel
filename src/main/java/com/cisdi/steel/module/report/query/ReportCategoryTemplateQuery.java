package com.cisdi.steel.module.report.query;

import com.cisdi.steel.common.base.vo.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>Description: 分类模板配置 查询参数(不需要可删除) </p>
 * <P>Date: 2018-10-23 </P>
 *
 * @author leaf
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ReportCategoryTemplateQuery extends PageQuery implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 模板的编码
     */
    private String reportCategoryCode;

    /**
     * 状态 1 禁止 0 不禁止 1 失效
     */
    private String forbid;
}
