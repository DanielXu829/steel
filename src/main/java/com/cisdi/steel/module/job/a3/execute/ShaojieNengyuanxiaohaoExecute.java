package com.cisdi.steel.module.job.a3.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a3.writer.ShaojieNengyuanxiaohaoWriter;
import com.cisdi.steel.module.job.a3.writer.ShaojieWuzhibangongWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 烧结能源消耗
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShaojieNengyuanxiaohaoExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ShaojieNengyuanxiaohaoWriter nengyuanxiaohaoWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return nengyuanxiaohaoWriter;
    }
}
