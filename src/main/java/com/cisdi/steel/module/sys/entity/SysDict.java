package com.cisdi.steel.module.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>Description: 系统字典 实体类 </p>
 * <P>Date: 2018-08-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dict")
public class SysDict extends Model<SysDict> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 类型 1父类 没有表示子类
     */
    private String type;

    /**
     * 上级编码
     */
    @TableField("parent_code")
    private String parentCode;

    /**
     * 排序号
     */
    private Integer sort;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
