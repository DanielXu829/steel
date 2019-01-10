package com.cisdi.steel.module.job.a5.task;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a5.execute.QiguidianjianExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 气柜点检表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class QiguidianjianJob extends AbstractExportJob {

    @Autowired
    private QiguidianjianExecute qiguidianjianExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_qiguidianjian;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return qiguidianjianExecute;
    }

    /**
     * 每天过一分钟执行
     */
    @Scheduled(cron = "0 1 0 1/1 * ?")
    public void task() {
        execute(null);
    }
}
