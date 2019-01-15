package com.cisdi.steel.module.job.a2.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.FensuixiduWriter;
import com.cisdi.steel.module.job.a2.writer.ZhibiaoguankongWriter;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@Slf4j
public class ZhibiaoguankongExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ZhibiaoguankongWriter zhibiaoguankongWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return zhibiaoguankongWriter;
    }

    @Override
    protected void executeDetail(JobExecuteInfo jobExecuteInfo) {
        // 1
        initConfig();
        // 2
        this.checkParameter(jobExecuteInfo);
        // 3
        List<ReportCategoryTemplate> templates = getTemplateInfo(jobExecuteInfo.getJobEnum());
        for (ReportCategoryTemplate template : templates) {
            Date date = new Date();
            DateQuery dateQuery = new DateQuery(date, date, date);
            try {
                if (Objects.nonNull(jobExecuteInfo.getDateQuery())) {
                    dateQuery = jobExecuteInfo.getDateQuery();
                }
                if (Objects.isNull(dateQuery.getDelay()) || dateQuery.getDelay()) {
                    // 处理延迟问题
                    dateQuery = DateQueryUtil.handlerDelay(dateQuery, template.getBuildDelay(), template.getBuildDelayUnit());
                }

                ExcelPathInfo excelPathInfo = this.getPathInfoByTemplate(template, dateQuery);
                // 参数缺一不可
                WriterExcelDTO writerExcelDTO = WriterExcelDTO.builder()
                        .startTime(new Date())
                        .jobEnum(jobExecuteInfo.getJobEnum())
                        .jobExecuteEnum(jobExecuteInfo.getJobExecuteEnum())
                        .dateQuery(dateQuery)
                        .template(template)
                        .excelPathInfo(excelPathInfo)
                        .build();

                ReportIndex reportIndex = new ReportIndex();
                reportIndex.setSequence(template.getSequence())
                        .setReportCategoryCode(template.getReportCategoryCode())
                        .setName(excelPathInfo.getFileName())
                        .setPath(excelPathInfo.getSaveFilePath())
                        .setIndexLang(LanguageEnum.getByLang(template.getTemplateLang()).getName())
                        .setIndexType(ReportTemplateTypeEnum.getType(template.getTemplateType()).getCode())
                        .setCurrDate(dateQuery.getRecordDate())
                        .setRecordDate(dateQuery.getRecordDate());

                // 4、5填充数据
                Workbook workbook = getCurrentExcelWriter().writerExcelExecute(writerExcelDTO);
                // 6、生成文件
                this.createFile(workbook, excelPathInfo, writerExcelDTO, dateQuery);

                // 7、插入索引
                reportIndexService.insertReportRecord(reportIndex);
            } catch (Exception e) {
                log.error(jobExecuteInfo.getJobEnum().getName() + "-->生成模板失败" + e.getMessage(), e);
            }
        }
    }
}
