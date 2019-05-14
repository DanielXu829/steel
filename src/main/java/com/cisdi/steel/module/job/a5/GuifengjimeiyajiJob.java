package com.cisdi.steel.module.job.a5;

import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a5.execute.GuifengjimeiyajiExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 柜区风机煤压机时间统计表
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GuifengjimeiyajiJob extends AbstractBaseCommonExportJob5 {

    @Autowired
    private GuifengjimeiyajiExecute guifengjimeiyajiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_guifengjimeiyaji;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return guifengjimeiyajiExecute;
    }
}
