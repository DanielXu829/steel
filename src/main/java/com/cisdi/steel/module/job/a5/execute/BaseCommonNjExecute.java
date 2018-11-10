package com.cisdi.steel.module.job.a5.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a5.writer.BaseNjWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通用默认执行器
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class BaseCommonNjExecute extends AbstractJobExecuteExecute {

    @Autowired
    private BaseNjWriter baseNjWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return baseNjWriter;
    }
}
