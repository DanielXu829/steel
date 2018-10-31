package com.cisdi.steel.module.test.task;

import com.cisdi.steel.module.test.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DemoTask {
    @Autowired
    private DemoService demoService;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void synGenOfficeTast() {
        try {
            demoService.genOfficeFile();
            log.info("----------------自动生成报表---------------------------");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
