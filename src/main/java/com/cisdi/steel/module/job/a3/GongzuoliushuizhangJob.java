package com.cisdi.steel.module.job.a3;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a3.execute.JiejiExecute;
import com.cisdi.steel.module.job.a3.execute.ShaojieWuzhibangongExecute;
import com.cisdi.steel.module.job.a3.writer.ShaojieWuzhibangongWriter;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 烧结无纸办公通用执行类-工作流水账
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GongzuoliushuizhangJob extends AbstractExportJob {

    @Autowired
    private ShaojieWuzhibangongExecute wuzhibangongExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_gongzuoliushuizhang;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return wuzhibangongExecute;
    }
}
