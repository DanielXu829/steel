package com.cisdi.steel.module.job.gl;


import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.CaoZuoGuanLiRiJiExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 7高炉操作管理日记 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/07/06 </P>
 *
 * @version 1.0
 */
@Component
public class CaoZuoGuanLiRiJi7Job extends AbstractExportJob {

    @Autowired
    private CaoZuoGuanLiRiJiExecute caoZuoGuanLiRiJiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_caozuoguanliriji7;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return caoZuoGuanLiRiJiExecute;
    }
}
