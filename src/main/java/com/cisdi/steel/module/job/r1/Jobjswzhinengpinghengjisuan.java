package com.cisdi.steel.module.job.r1;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r1.data.JswzhinengpinghengjisuanData;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * <p>Description:  JSW质能平衡计算报表       </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/25 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class Jobjswzhinengpinghengjisuan extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(JswzhinengpinghengjisuanData.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.jswzhinengpinghengjisuan;
    }
}
