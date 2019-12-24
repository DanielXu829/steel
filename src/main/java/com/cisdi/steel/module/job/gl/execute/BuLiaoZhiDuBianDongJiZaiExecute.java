package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.BuLiaoZhiDuBianDongJiZaiWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8高炉布料制度变动记载日报表 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
public class BuLiaoZhiDuBianDongJiZaiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private BuLiaoZhiDuBianDongJiZaiWriter buLiaoZhiDuBianDongJiLuWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return buLiaoZhiDuBianDongJiLuWriter;
    }
}
