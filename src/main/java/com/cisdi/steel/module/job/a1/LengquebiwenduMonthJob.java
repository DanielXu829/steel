package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.LengquebiwenduDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 高炉冷却壁温度月报表
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LengquebiwenduMonthJob extends AbstractExportJob {

    @Autowired
    private LengquebiwenduDayExecute lengquebiwenduExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_lengquebiwendu_month;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return lengquebiwenduExecute;
    }
}
