package com.cisdi.steel.module.job.a2;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a2.execute.GuanjianzhibiaoExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * CDQ操作运行报表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GuanjianzhibiaoJob extends AbstractExportJob {

    @Autowired
    private GuanjianzhibiaoExecute guanjianzhibiaoExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.jh_jlguanjianzhibiao;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return guanjianzhibiaoExecute;
    }
}
