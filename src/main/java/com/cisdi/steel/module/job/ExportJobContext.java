package com.cisdi.steel.module.job;

import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class ExportJobContext {

    private final Map<String, AbstractExportJob> apiJob = new ConcurrentHashMap<>();

    @Autowired
    private ReportIndexMapper reportIndexMapper;

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

    @Autowired
    private ReportIndexMapper reportIndexMapper;

    public void execute(Long indexId) throws Exception {
        ReportIndex reportIndex = reportIndexMapper.selectById(indexId);
        if (Objects.nonNull(reportIndex)) {
            AbstractExportJob abstractExportJob = apiJob.get(reportIndex.getReportCategoryCode());
            if (Objects.nonNull(abstractExportJob)) {
                JobExecuteInfo jobExecuteInfo = JobExecuteInfo.builder()
                        .jobEnum(abstractExportJob.getCurrentJob())
                        .jobExecuteEnum(JobExecuteEnum.manual)
                        .dateQuery(new DateQuery(reportIndex.getCreateTime()))
                        .build();
                abstractExportJob.getCurrentJobExecute().execute(jobExecuteInfo);
            }
        }
    }
}
