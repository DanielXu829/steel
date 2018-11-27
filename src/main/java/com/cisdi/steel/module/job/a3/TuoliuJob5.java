package com.cisdi.steel.module.job.a3;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a3.execute.TuoliuExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 5#脱硫系统运行日报 作业
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class TuoliuJob5 extends AbstractExportJob {

    @Autowired
    private TuoliuExecute tuoliuExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_tuoliu5;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return tuoliuExecute;
    }
}
