package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.ExcelReadWriter;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.writer.XiaoHaoDayWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消耗日报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/11 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class XiaoHaoDayExecute extends AbstractJobExecuteExecute {

    @Autowired
    private XiaoHaoDayWriter xiaoHaoDayWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return xiaoHaoDayWriter;
    }
}
