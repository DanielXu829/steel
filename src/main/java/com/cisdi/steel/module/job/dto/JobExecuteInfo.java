package com.cisdi.steel.module.job.dto;

import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务执行需要的信息
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@Builder(toBuilder = true)
public class JobExecuteInfo implements Serializable {

    /**
     * 执行的工作
     * 必须存在
     */
    private JobEnum jobEnum;

    /**
     * 执行的状态
     * 如果值为null 表示自动
     */
    private JobExecuteEnum jobExecuteEnum;


    /**
     * 查询的时间
     * 如果为null 表示 查询当前时间
     */
    private DateQuery dateQuery;

    /**
     * 重新生成时报表ID
     */
    private Long indexId;
}
