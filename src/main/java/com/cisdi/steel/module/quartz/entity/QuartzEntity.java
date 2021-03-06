package com.cisdi.steel.module.quartz.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: 定时器实体类 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author leaf
 * @version 1.0
 * @since 2018/7/9
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuartzEntity {

    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务分组
     */
    private String jobGroup="所有";
    /**
     * 任务描述
     */
    private String description;
    /**
     * 执行类
     */
    private String jobClassName;
    /**
     * cron表达式
     */
    private String cronExpression;
    /**
     * 触发器名称
     */
    private String triggerName;
    /**
     * 触发器状态
     */
    private String triggerState;

    /**
     * 下一次触发时间
     */
    private Long nextFireTime;

    /**
     * 上一次触发时间
     */
    private Long prevFireTime;

    /**
     * 任务名称 用于修改
     */
    private String oldJobName;
    /**
     * 任务分组 用于修改
     */
    private String oldJobGroup="所有";

    /**
     * 任务执行编码
     */
    private String jobCode;

    /**
     * 模板id
     */
    private Long id;

    /**
     * 周期
     */
    private Integer build;
    /**
     * 单位
     */
    private String buildUnit;

    /**
     * 延迟时间
     */
    private Integer buildDelay;
    /**
     * 延迟单位
     */
    private String buildDelayUnit;

    /**
     * 修改reportIndex时，
     * 关闭reportIndex的时间，
     * 与下次job定时执行时间相差超过字段所对应的时间戳，
     * 就自动触发一次job
     */
    private int makeupInterval;

    /**
     * 0代表选择配置cron，1代表手动输入cron
     */
    private int cronSettingMethod;

    /**
     * 用于回显cron表达式的配置项
     */
    private String cronJsonString;
}
