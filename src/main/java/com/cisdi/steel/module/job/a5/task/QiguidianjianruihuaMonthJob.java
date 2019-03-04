package com.cisdi.steel.module.job.a5.task;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a5.execute.QiguidianjianExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class QiguidianjianruihuaMonthJob extends AbstractNJExportJob {
    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_qiguidianjianruihua_month;
    }
//
//    @Autowired
//    private QiguidianjianExecute qiguidianjianExecute;
//
//    @Override
//    public IJobExecute getCurrentJobExecute() {
//        return qiguidianjianExecute;
//    }
//
//    /**
//     * 0 2 0 1 * ? 每月第一天过两分
//     * 0 1 0 * * ? 每天0点执行
//     */
//    @Override
//    @Scheduled(cron = "0 1 0 * * ?")
//    public void task() {
//        execute(null);
//    }
}
