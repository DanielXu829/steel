package com.cisdi.steel.module.job.a6;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a6.execute.MeiqichuchenbfExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 6BF煤气除尘日报
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class Meiqichuchen7bfJob extends AbstractExportJob {

    @Autowired
    private MeiqichuchenbfExecute meiqichuchenbfExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.hb_meiqichuchen7bf;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return meiqichuchenbfExecute;
    }
}
