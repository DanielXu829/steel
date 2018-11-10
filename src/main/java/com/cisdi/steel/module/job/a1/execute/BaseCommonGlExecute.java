package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.readwriter.BaseGlReadWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基本用的 执行器
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class BaseCommonGlExecute extends AbstractJobExecuteExecute {

    @Autowired
    private BaseGlReadWriter baseGlReadWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return baseGlReadWriter;
    }
}
