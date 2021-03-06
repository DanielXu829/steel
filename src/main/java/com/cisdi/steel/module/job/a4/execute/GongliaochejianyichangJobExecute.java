package com.cisdi.steel.module.job.a4.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a4.writer.GongliaochejianyichangJobWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GongliaochejianyichangJobExecute extends AbstractJobExecuteExecute {

    @Autowired
    private GongliaochejianyichangJobWriter gongliaochejianyichangJobWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return gongliaochejianyichangJobWriter;
    }
}
