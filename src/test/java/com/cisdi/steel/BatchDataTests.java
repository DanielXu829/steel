package com.cisdi.steel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.report.entity.ReportCategory;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import com.cisdi.steel.module.report.service.ReportCategoryService;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import com.cisdi.steel.module.report.service.ReportIndexService;
import com.cisdi.steel.module.sys.entity.SysConfig;
import com.cisdi.steel.module.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Slf4j
public class BatchDataTests extends SteelApplicationTests {

    @Autowired
    private SysConfigService sysConfigService;


    @Autowired
    private ReportCategoryService reportCategoryService;

    @Autowired
    private ReportCategoryTemplateService reportCategoryTemplateService;


    @Autowired
    private Scheduler scheduler;

    private static final String jobGroup = "所有";


    @Autowired
    private ReportIndexService reportIndexService;

    @Test
    public void test11() {
        LambdaQueryWrapper<ReportIndex> wrapper = new QueryWrapper<ReportIndex>().lambda();
        wrapper.eq(true, ReportIndex::getHidden, "1");
        List<ReportIndex> list = reportIndexService.list(wrapper);
        for (ReportIndex reportIndex : list) {
//            System.out.println(reportIndex.getPath());
            BaseId baseId = new BaseId();
            baseId.setId(reportIndex.getId());
            reportIndexService.deleteRecord(baseId);
            FileUtils.delFile(reportIndex.getPath());
        }
    }


    /**
     * 批量插入 所有job
     * 名称 编码 对应类的class  插入到sysConfig表中
     */
    @Test
    public void test1() {
        List<SysConfig> all = getAll();
        sysConfigService.saveBatch(all);
    }


    /**
     * 开启所有任务
     * 0 0/10 * * * ? 每10分钟
     * 0 0 0/4 * * ?  每4个小时
     * 0 5 0/1 * * ?  每1小时5分钟
     * 0 10,59 * * * ? 每10,59分钟
     */
    @Test
    public void test2() {
        List<SysConfig> all = getAll();
        for (SysConfig sysConfig : all) {
            createTask(sysConfig.getCode(), jobGroup, "0 10,59 * * * ?", "");
        }
    }

    /**
     * 移除所有任务
     */
    @Test
    public void test3() {
        List<SysConfig> all = getAll();
        for (SysConfig sysConfig : all) {
            JobKey jobKey = JobKey.jobKey(sysConfig.getCode(), jobGroup);
            try {
                scheduler.deleteJob(jobKey);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 生成所有子类 更新目录
     */
    @Test
    public void test4() {
        List<ReportCategoryTemplate> list = reportCategoryTemplateService.list(null);
        list.forEach(item -> {
            if (StringUtils.isNotBlank(item.getTemplatePath())) {
                LambdaQueryWrapper<ReportCategory> wrapper =
                        new QueryWrapper<ReportCategory>().lambda();
                wrapper.eq(ReportCategory::getCode, item.getReportCategoryCode());
                int count = reportCategoryService.count(wrapper);
                if (count == 0) {
                    ReportCategory reportCategory = new ReportCategory();
                    reportCategory.setCode(item.getReportCategoryCode());
                    reportCategory.setLeafNode("1");
                    Long parentId = getParentId(item.getReportCategoryCode());
                    reportCategory.setParentId(parentId);
                    reportCategory.setName(item.getTemplateName());
                    reportCategoryService.save(reportCategory);
                }

            }

        });

    }

    private Long getParentId(String code) {
        if (code.startsWith("gl_")) {
            // 6高炉
            return 1L;
        } else if (code.startsWith("jh_")) {
            // 焦化
            return 2L;
        } else if (code.startsWith("sj_")) {
            // 烧结
            return 3L;
        } else if (code.startsWith("ygl_")) {
            // 原供料
            return 4L;
        } else if (code.startsWith("nj_")) {
            // 能介
            return 5L;
        }
        return 1L;
    }

    /**
     * 获取所有的类
     */
    private List<SysConfig> getAll() {
        Map<String, Job> beansOfType = ApplicationContextHolder.getApplicationContext().getBeansOfType(Job.class);
        List<SysConfig> sysConfigs = new ArrayList<>();
        beansOfType.forEach((k, v) -> {
            String name = v.getClass().getPackage().getName();
            // 查找a开头的包里面的类
            if (name.startsWith("com.cisdi.steel.module.job.a")) {
                AbstractExportJob abstractExportJob = (AbstractExportJob) v;
                SysConfig t = new SysConfig();
                t.setCode(abstractExportJob.getCurrentJob().getCode());
                t.setName(abstractExportJob.getCurrentJob().getName());
                t.setAction(v.getClass().getName());
                sysConfigs.add(t);
            }
        });
        return sysConfigs;
    }

    // 创建一个任务
    private void createTask(String jobName, String jobGroup, String cronExpression, String desc) {
        try {
            //通过任务编码获取执行类
            String action = sysConfigService.selectActionByCode(jobName);
//            Class cls = Class.forName(quartz.getJobClassName());
            Class cls = Class.forName(action);
            cls.newInstance();
            //构建job信息
            JobDetail job = JobBuilder.newJob(cls).withIdentity(jobName,
                    jobGroup)
                    .withDescription(desc)
                    .build();
            // 触发时间点
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger" + jobName, jobGroup)
                    .startNow().withSchedule(cronScheduleBuilder).build();
            //交由Scheduler安排触发
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            log.error("新建任务报错", e);
        }
    }
}
