package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.writer.BF6gongyicanshuWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 工艺参数
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class BF6gongyicanshuExecute extends AbstractJobExecuteExecute {


    @Autowired
    private BF6gongyicanshuWriter bf6gongyicanshuWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return bf6gongyicanshuWriter;
    }
}
