package com.cisdi.steel.module.job.drt;

import com.cisdi.steel.module.job.drt.dto.DrtJobExecuteInfo;
import com.cisdi.steel.module.job.drt.execute.DrtExcelExecute;
import com.cisdi.steel.module.job.drt.execute.DrtWordExecute;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.enums.TemplateTypeEnum;
import com.cisdi.steel.module.report.mapper.ReportCategoryTemplateMapper;
import com.cisdi.steel.module.report.mapper.ReportTemplateConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class DynamicReportJob implements Job {

    @Autowired
    private ReportCategoryTemplateMapper reportCategoryTemplateMapper;

    @Autowired
    private ReportTemplateConfigMapper reportTemplateConfigMapper;

    @Autowired
    private DrtExcelExecute dynamicReportExecute;

    @Autowired
    private DrtWordExecute drtWordExecute;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
        String reportCategoryCode = (String) mergedJobDataMap.get("report_category_code");
        Long templateId = (Long) mergedJobDataMap.get("report_category_template_id");
        if (Objects.isNull(templateId)) {
            log.error("动态报表job信息的模板id不存在");
            return;
        }
        DrtJobExecuteInfo drtJobExecuteInfo = new DrtJobExecuteInfo()
                .setReportCategoryCode(reportCategoryCode)
                .setReportCategoryTemplateId(templateId);
        // 找到对应的执行器（word或者excel）
        ReportCategoryTemplate template = reportCategoryTemplateMapper.selectById(templateId);
        if (Objects.isNull(template)) {
            log.error(String.format("id为%s的动态报表模板不存在" + templateId));
            return;
        }
        ReportTemplateConfig reportTemplateConfig
                = reportTemplateConfigMapper.selectById(template.getTemplateConfigId());
        if (Objects.isNull(reportTemplateConfig)) {
            log.error(String.format("id为%s的模板配置项不存在" + template.getTemplateConfigId()));
            return;
        }
        TemplateTypeEnum templateTypeEnum = TemplateTypeEnum.getByCode(reportTemplateConfig.getTemplateType());
        switch (templateTypeEnum) {
            case WORD:
                drtWordExecute.execute(drtJobExecuteInfo);
                break;
            default:
                dynamicReportExecute.execute(drtJobExecuteInfo);
                break;
        }
    }
}
