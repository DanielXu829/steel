package com.cisdi.steel.module.job.a3;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a3.execute.JiejiExecute;
import com.cisdi.steel.module.job.a3.execute.RongjiExecute;
import com.cisdi.steel.module.job.a3.writer.RongjiWriter;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 熔剂燃料质量管控
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class RongjiJob5 extends AbstractExportJob {

    @Autowired
    private RongjiExecute rongjiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_rongji;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return rongjiExecute;
    }
}
