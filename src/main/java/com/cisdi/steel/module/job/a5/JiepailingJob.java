package com.cisdi.steel.module.job.a5;

import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a5.execute.AcsExecute;
import com.cisdi.steel.module.job.a5.execute.DianLiExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 界牌岭
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JiepailingJob extends AbstractBaseCommonExportJob5 {
    @Autowired
    private DianLiExecute dianLiExecute;

    @Override
    public IJobExecute getCurrentJobExecute() {
        return dianLiExecute;
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_jiepailing_day;
    }
}
