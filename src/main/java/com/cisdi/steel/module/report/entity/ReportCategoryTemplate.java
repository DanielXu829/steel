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
 * <p>Description: 分类模板配置 实体类 </p>
 * <P>Date: 2018-10-23 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReportCategoryTemplate extends Model<ReportCategoryTemplate> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板的编码
     */
    private String reportCategoryCode;

    /**
     * 序号
     */
    private String sequence;

    /**
     * 模板的名称
     */
    private String templateName;

    /**
     * 模板所在路径
     */
    private String templatePath;

    /**
     * 模板类型名称
     */
    private String templateType;

    /**
     * 模板语言
     */
    private String templateLang;
    /**
     * 是否禁止 1 禁止 0 不禁止
     */
    private String forbid;

    /**
     * 文件生成的目录
     */
    @TableField("excel_path")
    private String excelPath;


    /**
     * 备注
     */
    private String remark;

    private String attr1;

    private String attr2;

    private String attr3;

    private String attr4;

    private String attr5;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
