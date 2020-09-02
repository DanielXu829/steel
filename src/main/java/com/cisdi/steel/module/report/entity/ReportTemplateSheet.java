package com.cisdi.steel.module.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReportTemplateSheet extends Model<ReportTemplateSheet> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 对应report_tempalte_config中的id
     */
    private Long templateConfigId;

    /**
     * sheet的标题
     */
    private String sheetTitle;

    /**
     * sheet的名称
     */
    private String sheetName;

    /**
     * 时间划分方式
     */
    private Integer timeDivideType;

    /**
     * 时间分类  "1"-表示时间范围；"2"-表示“最近多少小时/天/月”。
     */
    private String timeType;

    /**
     * 开始时间  对应 timeType="1"
     */
    private String startTimeslot;

    /**
     * 结束时间 对应 timeType="1"
     */
    private String endTimeslot;

    /**
     * 时间间隔
     */
    private String timeslotInterval;

    /**
     * 最近多少小时/天/月  对应 timeType = "2"
     */
    private Integer lastTimeslot;

    /**
     * 是否添加平均值
     */
    private String isAddAvg;

    /**
     * 平均值计算方式
     */
    private Integer avgDivideType;


    /**
     * sheet在excel中的顺序(如果是word, 则代表文本和趋势分析的顺序)
     */
    private Integer sequence;
}
