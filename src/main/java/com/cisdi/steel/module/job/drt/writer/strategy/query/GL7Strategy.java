package com.cisdi.steel.module.job.drt.writer.strategy.query;

import com.cisdi.steel.module.report.enums.SequenceEnum;
import org.springframework.stereotype.Component;

@Component
public class GL7Strategy extends GLStrategy {
    @Override
    public String getKey() {
        return SequenceEnum.GL7.getSequenceCode();
    }
}
