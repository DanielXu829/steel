package com.cisdi.steel.module.job.gl;


import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.JiShuJingJiExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 7高炉技术经济月报表 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/07/06</P>
 *
 * @version 1.0
 */
@Component
public class JiShuJingJi7Job extends AbstractExportJob {

    @Autowired
    private JiShuJingJiExecute jiShuJingJiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_jishujingji7;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return jiShuJingJiExecute;
    }
}
