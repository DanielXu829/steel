package com.cisdi.steel.module.job.drt.execute;

import com.cisdi.steel.module.job.drt.writer.DrtWordWriter;
import com.cisdi.steel.module.job.drt.writer.IDrtWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DrtWordExecute extends DrtAbstractExecute{

    @Autowired
    private DrtWordWriter drtWordWriter;

    @Override
    public IDrtWriter getDrtWriter() {
        return drtWordWriter;
    }

}
