package com.cisdi.steel.module.report.task;

import com.cisdi.steel.module.report.mapper.ReportTemporaryFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;

// @Component
@EnableScheduling
public class ClearTemporaryFile {
    @Autowired
    private ReportTemporaryFileMapper reportTemporaryFileMapper;

    // @Scheduled(cron = "0/10 * * * * ?")
    public void deleteTemporaryFile() {

    }
}
