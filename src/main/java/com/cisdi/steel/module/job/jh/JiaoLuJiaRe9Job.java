package com.cisdi.steel.module.job.jh;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.jh.execute.JiaoLuJiaReExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 9#焦炉加热制度报表 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/07 </P>
 *
 * @version 1.0
 */
@Component
public class JiaoLuJiaRe9Job extends AbstractExportJob {

    @Autowired
    private JiaoLuJiaReExecute jiaoLuJiaReExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.jh_jiaolujiare9;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return jiaoLuJiaReExecute;
    }
}
