package com.cisdi.steel.module.job;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;

/**
 * 任务执行的接口 最终执行
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface IJobExecute {
    /**
     * 执行器
     *
     * @param jobEnum        任务标识
     * @param jobExecuteEnum 任务执行类型
     * @param dateQuery      条件
     * @throws Exception 执行错误
     */
    void execute(JobEnum jobEnum, JobExecuteEnum jobExecuteEnum, DateQuery dateQuery) throws Exception;
}
