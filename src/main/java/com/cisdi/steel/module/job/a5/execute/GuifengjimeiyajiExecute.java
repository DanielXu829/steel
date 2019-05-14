package com.cisdi.steel.module.job.a5.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a5.writer.GuifengjimeiyajiWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 柜区风机煤压机时间统计表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GuifengjimeiyajiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private GuifengjimeiyajiWriter guifengjimeiyajiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return guifengjimeiyajiWriter;
    }
}
