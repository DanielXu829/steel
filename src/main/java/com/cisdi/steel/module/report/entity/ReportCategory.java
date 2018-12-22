package com.cisdi.steel.module.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>Description: 报表分类 实体类 </p>
 * <P>Date: 2018-10-23 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReportCategory extends Model<ReportCategory> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父类id
     */
    private Long parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 编码唯一
     */
    private String code;

    /**
     * 0 父 1叶子
     */
    private String leafNode;

    @TableField(value = "sort")
    private Integer sort;

    /**
     * 备注（预留字段）
     */
    private String remark;

    private String attr1;

    private String attr2;

    private String attr3;

    private String attr4;

    private String attr5;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<ReportCategory> childList;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
