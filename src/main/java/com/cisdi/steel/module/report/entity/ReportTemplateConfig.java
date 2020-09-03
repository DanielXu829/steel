package com.cisdi.steel.module.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.cisdi.steel.module.report.enums.TimeDivideEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

    /**
     * 时间划分方式
     */
    // TODO 删除
    @TableField(exist = false)
    private Integer timeDivideType;

    /**
     * 时间分类  "1"-表示时间范围；"2"-表示“最近多少小时/天/月”。
     */
    // TODO 删除
    @TableField(exist = false)
    private String timeType;

    /**
     * 开始时间  对应 timeType="1"
     */
    // TODO 删除
    @TableField(exist = false)
    private String startTimeslot;

    /**
     * 结束时间 对应 timeType="1"
     */
    // TODO 删除
    @TableField(exist = false)
    private String endTimeslot;


    /**
     * 时间间隔
     */
    // TODO 删除
    @TableField(exist = false)
    private String timeslotInterval;

    /**
     * 最近多少小时/天/月  对应 timeType = "2"
     */
    // TODO 删除
    @TableField(exist = false)
    private Integer lastTimeslot;

    /**
     * 是否添加平均值
     */
    // TODO 删除
    @TableField(exist = false)
    private String isAddAvg;

    /**
     * 平均值计算方式
     */
    // TODO 删除
    @TableField(exist = false)
    private Integer avgDivideType;

    /**
     * 动态报表配置信息json字符串
     */
    private String templateConfigJsonString;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
