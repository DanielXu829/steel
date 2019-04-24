package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.XiaoHaoDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消耗日报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/11 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class XiaoHaoDayJob extends AbstractExportJob {

    @Autowired
    private XiaoHaoDayExecute xiaoHaoDayExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_xiaohao_day;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return xiaoHaoDayExecute;
    }
}
