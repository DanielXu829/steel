package com.cisdi.steel.module.quartz.query;

import com.cisdi.steel.common.base.vo.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>Description: 查询参数 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author leaf
 * @version 1.0
 * @since 2018/7/9
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuartzEntityQuery extends PageQuery {

    /**
     * 查询的任务名称
     */
    private String jobName;

    /**
     * 任务分组
     */
    private String jobGroup;
}
