package com.cisdi.steel.module.job.a5;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a5.execute.TwokongDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 三空压站运行记录表
 */
@Data
public class ThreekongJob extends AbstractExportJob {

    @Autowired
    private TwokongDayExecute execute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_threekong;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return execute;
    }
}
