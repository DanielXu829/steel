package com.cisdi.steel.module.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.cisdi.steel.module.report.enums.TimeDivideEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
    private Integer timeDivideType;

    private TimeDivideEnum timeDivideEnum;

    /**
     * 开始时间
     */
    private String startTimeslot;

    /**
     * 结束时间
     */
    private String endTimeslot;

    /**
     * 时间间隔
     */
    private String timeslotInterval;

    /**
     * 是否添加平均值
     */
    private String isAddAvg;

    /**
     * 平均值计算方式
     */
    private Integer avgDivideType;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
