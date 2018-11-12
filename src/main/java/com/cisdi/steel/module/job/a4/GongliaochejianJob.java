package com.cisdi.steel.module.job.a4;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a4.execute.GongliaochejianExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 供料车间集控中心交接班记录
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GongliaochejianJob extends AbstractExportJob {

    private final GongliaochejianExecute gongliaochejianExecute;

    @Autowired
    public GongliaochejianJob(GongliaochejianExecute gongliaochejianExecute) {
        this.gongliaochejianExecute = gongliaochejianExecute;
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_chejianjikongzhongxinjioajieban;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return gongliaochejianExecute;
    }
}
