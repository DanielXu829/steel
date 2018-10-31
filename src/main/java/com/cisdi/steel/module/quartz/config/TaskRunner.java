package com.cisdi.steel.module.quartz.config;

import com.cisdi.steel.module.quartz.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * 目的是为了看到一个测试的效果
 * <p>Description:  项目运行后执行的任务</p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author leaf
 * @version 1.0
 * @since 2018/7/9
 */
//@Component
@Slf4j
public class TaskRunner implements ApplicationRunner {

    @Autowired
    private JobService jobService;

    @Autowired
    private Scheduler scheduler;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void run(ApplicationArguments var) throws Exception {
//    	Long count = jobService.listQuartzEntity(null);
//    	if(count==0){
//    		log.info("初始化测试任务");
//    		QuartzEntity quartz = new QuartzEntity();
//    		quartz.setJobName("test01");
//    		quartz.setJobGroup("test");
//    		quartz.setDescription("测试任务");
//    		quartz.setJobClassName("com.itstyle.quartz.job.ChickenJob");
//    		quartz.setCronExpression("0/20 * * * * ?");
//   	        Class cls = Class.forName(quartz.getJobClassName()) ;
//   	        cls.newInstance();
//   	        //构建job信息
//   	        JobDetail job = JobBuilder.newJob(cls).withIdentity(quartz.getJobName(),
//   	        		quartz.getJobGroup())
//   	        		.withDescription(quartz.getDescription()).build();
//   	        //添加JobDataMap数据
//   	        job.getJobDataMap().put("itstyle", "科帮网欢迎你");
//   	        job.getJobDataMap().put("blog", "https://blog.52itstyle.com");
//		   	job.getJobDataMap().put("data", new String[]{"张三","李四"});
//   	        // 触发时间点
//   	        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression());
//   	        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger"+quartz.getJobName(), quartz.getJobGroup())
//   	                .startNow().withSchedule(cronScheduleBuilder).build();
//   	        //交由Scheduler安排触发
//   	        scheduler.scheduleJob(job, trigger);
//    	}
    }

}