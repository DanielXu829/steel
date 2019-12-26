package com.cisdi.steel.module.job.gl;


import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.KaoHeYueBaoExecute;
import com.cisdi.steel.module.job.gl.execute.LuLiaoXiaoHaoExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8高炉考核月报表 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
public class KaoHeYueBaoJob extends AbstractExportJob {

    @Autowired
    private KaoHeYueBaoExecute kaoHeYueBaoExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_kaoheyuebao;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return kaoHeYueBaoExecute;
    }
}
