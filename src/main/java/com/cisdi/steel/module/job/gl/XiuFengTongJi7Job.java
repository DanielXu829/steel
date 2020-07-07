package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.ChuTieXiaoLvExecute;
import com.cisdi.steel.module.job.gl.execute.XiuFengTongJiExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8高炉休风统计 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/07/07 </P>
 *
 * @version 1.0
 */
@Component
public class XiuFengTongJi7Job extends AbstractExportJob {

    @Autowired
    private XiuFengTongJiExecute execute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_xiufengtongji7;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return execute;
    }
}
