package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.writer.ZhongdianbuweicanshuWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 重点部位参数
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ZhongdianbuweicanshuExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ZhongdianbuweicanshuWriter zhongdianbuweicanshuWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return zhongdianbuweicanshuWriter;
    }
}
