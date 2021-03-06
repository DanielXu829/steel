package com.cisdi.steel.module.job.a3;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a3.execute.GycanshuExecute;
import com.cisdi.steel.module.job.a3.execute.GycanshuTotalExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 4小时发布-五烧主要工艺参数及实物质量情况日报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GycanshuTotalJob extends AbstractExportJob {

    @Autowired
    private GycanshuTotalExecute gycanshuTotalExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_gycanshutotal;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return gycanshuTotalExecute;
    }
}
