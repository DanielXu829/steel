package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.LugangWenduDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 炉缸温度 月报表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/11 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LugangWenduMonthJob extends AbstractExportJob {

    @Autowired
    private LugangWenduDayExecute lugangWenduDayExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_lugangwendu_month;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return lugangWenduDayExecute;
    }
}
