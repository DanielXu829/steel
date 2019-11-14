package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.LuKuangXiaoShiWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 小时炉况执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/13 </P>
 *
 * @version 1.0
 */
@Component
public class LuKuangXiaoShiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private LuKuangXiaoShiWriter luKuangXiaoShiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return luKuangXiaoShiWriter;
    }
}
