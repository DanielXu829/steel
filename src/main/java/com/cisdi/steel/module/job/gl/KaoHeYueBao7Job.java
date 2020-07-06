package com.cisdi.steel.module.job.gl;


import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.KaoHeYueBaoExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 7高炉考核月报表 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/07/06 </P>
 *
 * @version 1.0
 */
@Component
public class KaoHeYueBao7Job extends AbstractExportJob {

    @Autowired
    private KaoHeYueBaoExecute kaoHeYueBaoExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_kaoheyuebao7;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return kaoHeYueBaoExecute;
    }
}
