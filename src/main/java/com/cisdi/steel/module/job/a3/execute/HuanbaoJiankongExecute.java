package com.cisdi.steel.module.job.a3.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a3.writer.HuanbaoJiankongWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 烧结公辅环保设施运行情况及在线监测数据发布
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class HuanbaoJiankongExecute extends AbstractJobExecuteExecute {

    @Autowired
    private HuanbaoJiankongWriter huanbaoJIankongWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return huanbaoJIankongWriter;
    }
}
