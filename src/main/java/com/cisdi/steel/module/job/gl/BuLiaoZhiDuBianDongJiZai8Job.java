package com.cisdi.steel.module.job.gl;


import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.BuLiaoZhiDuBianDongJiZaiExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8#高炉布料制度变动记载日报表 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
public class BuLiaoZhiDuBianDongJiZai8Job extends AbstractExportJob {

    @Autowired
    private BuLiaoZhiDuBianDongJiZaiExecute buLiaoZhiDuBianDongJiLuExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_buliaozhidubiandongjizai8;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return buLiaoZhiDuBianDongJiLuExecute;
    }
}
