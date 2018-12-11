package com.cisdi.steel.module.report.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.cisdi.steel.common.base.vo.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Description: 报表文件-索引 查询参数(不需要可删除) </p>
 * <P>Date: 2018-10-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ReportIndexQuery extends PageQuery implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 分类编码
     */
    private String reportCategoryCode;
    /**
     * 序列
     */
    private String sequence;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 创建时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 类型
     */
    private String indexType;

    /**
     * 语言
     */
    private String indexLang;

    /**
     * 报表时间（今天 昨天 本月等）
     */
    private String toDay;

    /**
     * 目录父类编码
     */
    private String parentCode;
}
