package com.cisdi.steel.module.job.a3.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a3.writer.GycanshuWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 五、六号烧结机主要工艺参数及实物质量情况
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GycanshuExecute extends AbstractJobExecuteExecute {

    @Autowired
    private GycanshuWriter gycanshuWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return gycanshuWriter;
    }
}
