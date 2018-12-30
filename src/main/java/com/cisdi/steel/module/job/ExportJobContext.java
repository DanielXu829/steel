package com.cisdi.steel.module.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

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
                    JobExecuteInfo jobExecuteInfo = JobExecuteInfo.builder()
                            .jobEnum(abstractExportJob.getCurrentJob())
                            .jobExecuteEnum(JobExecuteEnum.manual)
                            .dateQuery(new DateQuery(reportIndex.getCreateTime(), reportIndex.getCreateTime(), reportIndex.getCreateTime()))
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
            for(ReportIndex index:reportIndex){
                if (Objects.nonNull(index)) {
                    AbstractExportJob abstractExportJob = apiJob.get(index.getReportCategoryCode());
                    if (Objects.nonNull(abstractExportJob)) {
                        JobExecuteInfo jobExecuteInfo = JobExecuteInfo.builder()
                                .jobEnum(abstractExportJob.getCurrentJob())
                                .jobExecuteEnum(JobExecuteEnum.manual)
                                .dateQuery(new DateQuery(index.getCreateTime(), index.getCreateTime(), index.getCreateTime()))
                                .build();
                        abstractExportJob.getCurrentJobExecute().execute(jobExecuteInfo);
                    }
                }
            }

        } catch (Exception e) {
            log.error("重新生成指定生成的报表时报错：" + e.getMessage());
        }

    }
}
