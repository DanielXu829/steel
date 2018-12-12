package com.cisdi.steel.module.job;

import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import com.cisdi.steel.module.report.service.ReportIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
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
}
