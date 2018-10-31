package com.cisdi.steel.module.quartz.config;

import com.zaxxer.hikari.HikariDataSource;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    @Autowired
    private MyJobFactory myJobFactory;

    @Autowired
    private HikariDataSource dataSource;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();  
        schedulerFactoryBean.setJobFactory(myJobFactory);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setStartupDelay(20);
        return schedulerFactoryBean;
    }  

    @Bean
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();  
    }  

} 
