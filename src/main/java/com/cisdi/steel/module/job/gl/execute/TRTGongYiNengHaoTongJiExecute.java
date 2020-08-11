package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.BianLiaoJiLuWriter;
import com.cisdi.steel.module.job.gl.writer.TRTGongYiNengHaoTongJiWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TRTGongYiNengHaoTongJiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private TRTGongYiNengHaoTongJiWriter trtGongYiNengHaoTongJiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return trtGongYiNengHaoTongJiWriter;
    }
}
