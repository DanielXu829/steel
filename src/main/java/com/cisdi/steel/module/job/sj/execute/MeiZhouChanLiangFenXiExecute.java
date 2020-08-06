package com.cisdi.steel.module.job.sj.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.sj.writer.MeiZhouChanLiangFenXiWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeiZhouChanLiangFenXiExecute extends AbstractJobExecuteExecute {
    @Autowired
    private MeiZhouChanLiangFenXiWriter meiZhouLiangChanFenXiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return meiZhouLiangChanFenXiWriter;
    }
}
