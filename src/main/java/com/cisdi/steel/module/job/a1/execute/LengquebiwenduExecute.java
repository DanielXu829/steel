package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.a1.readwriter.BaseReadWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:   高炉冷却壁温度日报表      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LengquebiwenduExecute extends AbstractJobExecuteExecute {

    @Autowired
    private BaseReadWriter baseReadWriter;

    @Override
    public void initConfig() {
        this.excelWriter = baseReadWriter;
    }
}
