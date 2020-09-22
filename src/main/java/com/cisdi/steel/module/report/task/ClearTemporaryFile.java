package com.cisdi.steel.module.report.task;

import com.cisdi.steel.module.report.service.ReportTemporaryFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClearTemporaryFile {
    @Autowired
    private ReportTemporaryFileService reportTemporaryFileService;

    /**
     * 每天凌晨2点清除临时文件(excel、word和image)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteTemporaryFile() {
        reportTemporaryFileService.deleteAllTemporaryFile();
    }
}
