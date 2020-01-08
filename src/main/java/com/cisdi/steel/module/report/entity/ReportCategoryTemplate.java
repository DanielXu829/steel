package com.cisdi.steel.module.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.cisdi.steel.module.quartz.entity.QuartzEntity;
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
     * 周期
     */
    @TableField("build")
    private Integer build;
    /**
     * 单位
     */
    @TableField("build_unit")
    private String buildUnit;

    /**
     * 延迟时间
     */
    @TableField("build_delay")
    private Integer buildDelay;
    /**
     * 延迟单位
     */
    @TableField("build_delay_unit")
    private String buildDelayUnit;

    /**
     * cron表达式
     */
    @TableField("cron")
    private String cron;

    /**
     * 备注
     */
    private String remark;

    private String attr1;

    private String attr2;

    private String attr3;

    private String attr4;

    private String attr5;

    /**
     * 任务相关信息
     */
    @TableField(exist = false)
    private QuartzEntity quartzEntity;

    /**
     * 任务描述
     */
    @TableField(exist = false)
    private String description;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    /**
     * 修改reportIndex时，
     * 关闭reportIndex的时间，
     * 与下次job定时执行时间相差超过字段所对应的时间戳，
     * 就自动触发一次job
     */
    @TableField("makeup_interval")
    private int makeupInterval;

    /**
     * 0代表选择配置cron，1代表手动输入cron
     */
    @TableField("cron_setting_method")
    private int cronSettingMethod;

    /**
     * 用于回显cron表达式的配置项
     */
    @TableField("cron_json_string")
    private String cronJsonString;

    /**
     * 用于生成reportCategory
     */
    @TableField(exist = false)
    private Long categoryParentId;

    /**
     * reportCategory表中name字段
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 是否是由动态报表生成，1代表是
     */
    @TableField("is_dynamic_report")
    private String isDynamicReport;

    /**
     * 动态报表配置表的id
     */
    @TableField("template_config_id")
    private Long templateConfigId;
}
