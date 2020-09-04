package com.cisdi.steel.module.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Description: 报表动态模板配置 实体类 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReportTemplateConfig extends Model<ReportTemplateConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板的名称
     */
    private String templateName;

    /**
     * 模板类型： 0：excel 1： word
     */
    private Integer templateType;

    /**
     * 是否为单sheet 1为单sheet， 0为多sheet
     */
    private Integer isSingleSheet;

    /**
     * 模板生成所在临时路径
     */
    private String templatePath;

    /**
     * 工序名称
     */
    private String sequenceCode;

    private Date createdTime;

    private Date updatedTime;


    /**
     * 动态报表配置信息json字符串
     */
    private String templateConfigJsonString;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
