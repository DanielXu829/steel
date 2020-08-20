package com.cisdi.steel.module.job.sj.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.sj.writer.ShengChanFenXiZhouBaoWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShengChanFenXiZhouBaoExecute extends AbstractJobExecuteExecute {
    @Autowired
    private ShengChanFenXiZhouBaoWriter shengChanFenXiZhouBaoWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return shengChanFenXiZhouBaoWriter;
    }
}
