package com.cisdi.steel.module.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.common.exception.LeafException;
import com.cisdi.steel.module.job.drt.dto.DrtJobExecuteInfo;
import com.cisdi.steel.module.job.drt.execute.DrtExcelExecute;
import com.cisdi.steel.module.job.drt.execute.DrtWordExecute;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.enums.TemplateTypeEnum;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import com.cisdi.steel.module.report.mapper.ReportTemplateConfigMapper;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/12/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@Slf4j
public class ExportJobContext {

    private final Map<String, AbstractExportJob> apiJob = new ConcurrentHashMap<>();

    @Autowired
    private ReportIndexMapper reportIndexMapper;

    @Autowired
    private ReportCategoryTemplateService reportCategoryTemplateService;

    @Autowired
    private ReportTemplateConfigMapper reportTemplateConfigMapper;

    @Autowired
    private DrtExcelExecute dynamicReportExecute;

    @Autowired
    private DrtWordExecute drtWordExecute;

    @Autowired
    public ExportJobContext(Map<String, AbstractExportJob> apiJobMap) {
        this.apiJob.clear();
        apiJobMap.forEach((k, v) -> this.apiJob.put(v.getCurrentJob().getCode(), v));
    }

    public String execute(String code) {
        AbstractExportJob abstractExportJob = apiJob.get(code);
        if (Objects.nonNull(abstractExportJob)) {
            abstractExportJob.execute(null);
            ReportIndex reportIndex = reportIndexMapper.queryLastOne(code);
            if (Objects.nonNull(reportIndex)) {
                return reportIndex.getPath();
            }
        }
        return null;
    }

    /**
     * 重新生成指定生成的报表
     *
     * @param indexId 报表ID
     * @throws Exception
     */
    public void executeByIndexId(Long indexId) {
        try {
            ReportIndex reportIndex = reportIndexMapper.selectById(indexId);
            if (Objects.nonNull(reportIndex)) {
                AbstractExportJob abstractExportJob = apiJob.get(reportIndex.getReportCategoryCode());
                if (Objects.nonNull(abstractExportJob)) {
                    DateQuery dateQuery = new DateQuery(reportIndex.getRecordDate(), reportIndex.getRecordDate(), reportIndex.getRecordDate());
                    dateQuery.setDelay(false);
                    JobExecuteInfo jobExecuteInfo = JobExecuteInfo.builder()
                            .jobEnum(abstractExportJob.getCurrentJob())
                            .jobExecuteEnum(JobExecuteEnum.manual)
                            .dateQuery(dateQuery)
                            .indexId(indexId)
                            .sequence(reportIndex.getSequence())
                            .build();
                    abstractExportJob.getCurrentJobExecute().execute(jobExecuteInfo);
                }
            }
        } catch (Exception e) {
            log.error("重新生成指定生成的报表时报错：" + e.getMessage());
        }

    }

    /**
     * 重新生成动态报表
     * @param
     */
    public void executeDynamicReport(ReportIndex reportIndex, ReportCategoryTemplate template) {
        DateQuery dateQuery = new DateQuery(reportIndex.getRecordDate(), reportIndex.getRecordDate(), reportIndex.getRecordDate());
        dateQuery.setDelay(false);
        DrtJobExecuteInfo drtJobExecuteInfo = new DrtJobExecuteInfo()
                .setReportCategoryCode(reportIndex.getReportCategoryCode())
                .setReportCategoryTemplateId(template.getId())
                .setJobExecuteEnum(JobExecuteEnum.manual)
                .setDateQuery(dateQuery)
                .setIndexId(reportIndex.getId())
                .setSequence(reportIndex.getSequence());
        ReportTemplateConfig reportTemplateConfig
                = reportTemplateConfigMapper.selectById(template.getTemplateConfigId());
        if (Objects.isNull(reportTemplateConfig)) {
            LeafException.castException(String.format("id为%s的模板配置项不存在" + template.getTemplateConfigId()));
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


    /**
     * 生成指定日期的报表
     *
     * @param indexId    id
     * @param reportDate 报表时间
     */
    public void executeByIndexId(Long indexId, Date reportDate) {
        try {
            ReportIndex reportIndex = reportIndexMapper.selectById(indexId);
            if (Objects.nonNull(reportIndex)) {
                AbstractExportJob abstractExportJob = apiJob.get(reportIndex.getReportCategoryCode());
                if (Objects.nonNull(abstractExportJob)) {
                    DateQuery dateQuery = new DateQuery(reportDate, reportDate, reportDate);
                    dateQuery.setDelay(false);
                    JobExecuteInfo jobExecuteInfo = JobExecuteInfo.builder()
                            .jobEnum(abstractExportJob.getCurrentJob())
                            .jobExecuteEnum(JobExecuteEnum.manual)
                            .dateQuery(dateQuery)
                            .build();
                    abstractExportJob.getCurrentJobExecute().execute(jobExecuteInfo);
                }
            }
        } catch (Exception e) {
            log.error("重新生成指定生成的报表时报错：" + e.getMessage());
        }

    }

    public void executeByIndexIds(Long indexId) {
        try {
            LambdaQueryWrapper<ReportIndex> wrapper = new QueryWrapper<ReportIndex>().lambda();
            List<ReportIndex> reportIndex = reportIndexMapper.selectList(wrapper);
            for (ReportIndex index : reportIndex) {
                if (Objects.nonNull(index)) {
                    AbstractExportJob abstractExportJob = apiJob.get(index.getReportCategoryCode());
                    if (Objects.nonNull(abstractExportJob)) {
                        JobExecuteInfo jobExecuteInfo = JobExecuteInfo.builder()
                                .jobEnum(abstractExportJob.getCurrentJob())
                                .jobExecuteEnum(JobExecuteEnum.manual)
                                .dateQuery(new DateQuery(index.getRecordDate(), index.getRecordDate(), index.getRecordDate()))
                                .build();
                        abstractExportJob.getCurrentJobExecute().execute(jobExecuteInfo);
                    }
                } else {

                }
            }

        } catch (Exception e) {
            log.error("重新生成指定生成的报表时报错：" + e.getMessage());
        }

    }
}
