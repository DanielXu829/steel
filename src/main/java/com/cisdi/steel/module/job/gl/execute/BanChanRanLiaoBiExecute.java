package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.BanChanRanLiaoBiWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8高炉班产燃料比执行处理类 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/07/09 </P>
 *
 * @version 1.0
 */
@Component
public class BanChanRanLiaoBiExecute  extends AbstractJobExecuteExecute {

    @Autowired
    private BanChanRanLiaoBiWriter banChanRanLiaoBiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return banChanRanLiaoBiWriter;
    }
}