package com.cisdi.steel.module.job.a5.task;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a5.execute.QiguidianjianExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
public abstract class AbstractNJExportJob extends AbstractExportJob {

    @Autowired
    private QiguidianjianExecute qiguidianjianExecute;

    @Override
    public IJobExecute getCurrentJobExecute() {
        return qiguidianjianExecute;
    }

    /**
     * 0 0 0/3 * * ? 每3小时生成
     * 0 0/1 * * * ? 每分钟生成
     * 0 1 0 * * ? 每天0点执行
     */
//    @Scheduled(cron = "0 1 0 * * ?")
    public void task() {
        execute(null);
    }
}
