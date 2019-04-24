package com.cisdi.steel.module.job.a3;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a3.execute.HuanbaoJiankongExecute;
import com.cisdi.steel.module.job.a3.execute.TuoliuExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 烧结公辅环保设施运行情况及在线监测数据发布
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class HuanbaoJiankongJob extends AbstractExportJob {

    @Autowired
    private HuanbaoJiankongExecute huanbaoJiankongExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_huanbaojiankong_day;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return huanbaoJiankongExecute;
    }
}
