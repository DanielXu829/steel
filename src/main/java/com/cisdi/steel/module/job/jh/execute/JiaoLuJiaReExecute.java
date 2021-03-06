package com.cisdi.steel.module.job.jh.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.jh.writer.JiaoLuJiaReWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 焦炉加热处理器 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/07 </P>
 *
 * @version 1.0
 */
@Component
public class JiaoLuJiaReExecute extends AbstractJobExecuteExecute {

    @Autowired
    private JiaoLuJiaReWriter jiaoLuJiaReWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return jiaoLuJiaReWriter;
    }
}
