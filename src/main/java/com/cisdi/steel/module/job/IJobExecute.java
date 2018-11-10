package com.cisdi.steel.module.job;

import com.cisdi.steel.module.job.dto.JobExecuteInfo;

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
     * @param jobExecuteInfo 执行信息
     * @throws Exception 执行错误
     */
    void execute(JobExecuteInfo jobExecuteInfo) throws Exception;
}
