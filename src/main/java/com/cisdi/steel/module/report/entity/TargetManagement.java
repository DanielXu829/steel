package com.cisdi.steel.module.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>Description: 指标管理 实体类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019-11-05 </P>
 *
 * @version 1.0
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TargetManagement extends Model<TargetManagement>{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父编号,用于构造节点树
     */
    @TableField("father_id")
    private Long fatherId;

    /**
     * 指标名
     */
    @TableField("target_name")
    private String targetName;

    /**
     * 书面名
     */
    @TableField("written_name")
    private String writtenName;

    /**
     * 指标公式
     */
    @TableField("target_formula")
    private String targetFormula;

    /**
     * 正常值范围
     */
    @TableField("normal_range")
    private String normalRange;

    /**
     * 默认值
     */
    @TableField("default_value")
    private String defaultValue;

    /**
     * 单位
     */
    private String unit;

    /**
     * 默认列宽
     */
    @TableField("default_width")
    private Long defaultWidth;

    /**
     * 默认行高
     */
    @TableField("default_height")
    private Long defaultHeight;

    /**
     * 是否是叶子 0否 1是
     */
    @TableField("is_leaf")
    private int isLeaf;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
