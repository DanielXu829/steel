package com.cisdi.steel.module.job.a4;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a4.execute.ZhongjiaowaipaiMonthExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 中焦外排记录
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ZhongjiaowaipaiMonthJob extends AbstractExportJob {

    @Autowired
    private ZhongjiaowaipaiMonthExecute zhongjiaowaipaiMonthExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.ygl_zhongjiaowaipai_month;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return zhongjiaowaipaiMonthExecute;
    }
}
