package com.cisdi.steel.module.quartz.query;

import lombok.Data;

/**
 * <p>Description: cron查询参数 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author leaf
 * @version 1.0
 * @since 2018/7/10
 */
@Data
public class CronQuery {

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 次数
     */
    private Integer count;
}
