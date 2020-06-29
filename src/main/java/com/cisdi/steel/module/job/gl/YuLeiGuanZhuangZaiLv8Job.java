package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.BianLiaoJiLuExecute;
import com.cisdi.steel.module.job.gl.execute.YuLeiGuanZhuangZaiLvExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class YuLeiGuanZhuangZaiLv8Job extends AbstractExportJob {

    @Autowired
    private YuLeiGuanZhuangZaiLvExecute execute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_yuleiguanzhuang;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return execute;
    }
}
