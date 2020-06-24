package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.ShaoJieKuangLiHuaExecute;
import com.cisdi.steel.module.job.gl.execute.YueBaoHuiZongExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8高炉月报汇总 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/06/24 </P>
 *
 * @version 1.0
 */
@Component
public class YueBaoHuiZong8Job extends AbstractExportJob {

    @Autowired
    YueBaoHuiZongExecute yueBaoHuiZongExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_yuebaohuizong;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return yueBaoHuiZongExecute;
    }
}
