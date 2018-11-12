package com.cisdi.steel.module.job.a4;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a4.execute.ShaixiafentongjiDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 筛下粉统计
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShaixiafentongjiDayJob extends AbstractExportJob {

    private final ShaixiafentongjiDayExecute shaixiafentongjiDayExecute;

    @Autowired
    public ShaixiafentongjiDayJob(ShaixiafentongjiDayExecute shaixiafentongjiDayExecute) {
        this.shaixiafentongjiDayExecute = shaixiafentongjiDayExecute;
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.ygl_shaixiafentongji_day;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return shaixiafentongjiDayExecute;
    }
}
