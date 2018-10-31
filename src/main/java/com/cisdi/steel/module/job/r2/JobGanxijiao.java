package com.cisdi.steel.module.job.r2;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r2.data.GanxijiaoData;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 干熄焦报表设计        </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/27 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JobGanxijiao extends AbstractJob {
    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(GanxijiaoData.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.jh_ganxijiao;
    }
}
