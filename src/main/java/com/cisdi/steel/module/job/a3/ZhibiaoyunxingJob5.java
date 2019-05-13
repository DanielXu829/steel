package com.cisdi.steel.module.job.a3;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a3.execute.JiejiExecute;
import com.cisdi.steel.module.job.a3.execute.RongjiExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 指标运行记录日报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ZhibiaoyunxingJob5 extends AbstractExportJob {

    @Autowired
    private RongjiExecute rongjiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_zhibiaoyunxing_day;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return rongjiExecute;
    }
}
