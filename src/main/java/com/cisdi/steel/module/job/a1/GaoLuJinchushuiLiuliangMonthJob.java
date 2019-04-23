package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.GaoLuPenMeiExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 高炉冷却壁进出水量月报
 */
@Component
public class GaoLuJinchushuiLiuliangMonthJob extends AbstractBaseCommonExportJob1 {

    @Autowired
    private GaoLuPenMeiExecute gaoLuPenMeiExecute;

    @Override
    public IJobExecute getCurrentJobExecute() {
        return gaoLuPenMeiExecute;
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_lqbjcs_month;
    }

}
