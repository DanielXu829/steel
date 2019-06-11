package com.cisdi.steel.module.job.a3;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a3.execute.GycanshuExecute;
import com.cisdi.steel.module.job.a3.execute.ShaojieNengyuanxiaohaoExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 烧结能源消耗
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShaojieNengyuanxiaohaoJob extends AbstractExportJob {

    @Autowired
    private ShaojieNengyuanxiaohaoExecute nengyuanxiaohaoExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_nengyuanxiaohao_month;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return nengyuanxiaohaoExecute;
    }
}
