package com.cisdi.steel.module.job.drt.execute;

import com.cisdi.steel.module.job.drt.writer.DrtExcelWriter;
import com.cisdi.steel.module.job.drt.writer.IDrtWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DrtExcelExecute extends DrtAbstractExecute{

    @Autowired
    private DrtExcelWriter drtExcelWriter;

    @Override
    public IDrtWriter getDrtWriter() {
        return drtExcelWriter;
    }
}
