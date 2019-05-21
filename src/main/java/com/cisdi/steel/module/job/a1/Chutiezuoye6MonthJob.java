package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.ChutiezonglanExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 6高炉出铁作业月报表
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/8 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class Chutiezuoye6MonthJob extends AbstractExportJob {


    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_chutiezuoye6_month;
    }

    @Autowired
    private ChutiezonglanExecute chutiezonglanExecute;

    @Override
    public IJobExecute getCurrentJobExecute() {
        return chutiezonglanExecute;
    }


}
