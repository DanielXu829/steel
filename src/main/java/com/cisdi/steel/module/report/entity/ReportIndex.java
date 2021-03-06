package com.cisdi.steel.module.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>Description: 报表文件-索引 实体类 </p>
 * <P>Date: 2018-10-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReportIndex extends Model<ReportIndex> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分类编码
     */
    private String reportCategoryCode;

    /**
     * 序号
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
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 类型
     */
    @TableField("index_type")
    private String indexType;

    /**
     * 语言
     */
    @TableField("index_lang")
    private String indexLang;

    @TableField("is_hidden")
    private String hidden;

    private String attr1;

    private String attr2;

    private String attr3;

    private String attr4;

    @TableField(exist = false)
    private Date currDate = new Date();

    @TableField("record_date")
    private Date recordDate;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    /**
     * reportIndex是否被锁住，0代表没有被锁住，1代表被锁住
     */
    @TableField("edit_status")
    private int editStatus;

    /**
     * 逻辑删除 0表示未删除 1表示删除
     */
    @TableLogic(value = "0", delval = "1")
    @TableField("del_flag")
    private String delFlag;
}
