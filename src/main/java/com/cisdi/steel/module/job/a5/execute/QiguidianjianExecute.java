package com.cisdi.steel.module.job.a5.execute;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 气柜点检表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@Slf4j
public class QiguidianjianExecute extends AbstractJobExecuteExecute {
    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return null;
    }

    @Override
    protected void executeDetail(JobExecuteInfo jobExecuteInfo) {
        List<ReportCategoryTemplate> templates = getTemplateInfo(jobExecuteInfo.getJobEnum());
        for (ReportCategoryTemplate template : templates) {
            DateQuery dateQuery = getDateQuery(jobExecuteInfo);
            try {
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

                ReportIndex templatePath = reportIndexService.existTemplate1(reportIndex);
                if (Objects.nonNull(templatePath) && StringUtils.isNotBlank(templatePath.getPath())) {
                    if (writerExcelDTO.getJobEnum().getCode().equals(JobEnum.nj_qiguidianjianruihua_month.getCode())) {
                        // 修改为生成后文件名称
                        String dateTime = DateUtil.getFormatDateTime(writerExcelDTO.getDateQuery().getRecordDate(), "yyyy-MM");
                        String dateTime1 = DateUtil.getFormatDateTime(templatePath.getRecordDate(), "yyyy-MM");
                        if (!dateTime.equals(dateTime1)) {
                            template.setTemplatePath(templatePath.getPath());
                            Workbook workbook = dealTemp(template);

                            PoiCustomUtil.buildMetadata(workbook, writerExcelDTO);
                            // 6、生成文件
                            this.createFile(workbook, excelPathInfo, writerExcelDTO, dateQuery);

                            // 7、插入索引
                            reportIndexService.insertReportRecord(reportIndex);
                        }

                    }
                } else {
                    Workbook workbook = getWorkbook(template.getTemplatePath());

                    PoiCustomUtil.buildMetadata(workbook, writerExcelDTO);
                    // 6、生成文件
                    this.createFile(workbook, excelPathInfo, writerExcelDTO, dateQuery);

                    // 7、插入索引
                    reportIndexService.insertReportRecord(reportIndex);
                }
            } catch (Exception e) {
                log.error(jobExecuteInfo.getJobEnum().getName() + "-->生成模板失败" + e.getMessage(), e);
            }
        }
    }

    private Workbook dealTemp(ReportCategoryTemplate template) {
        Workbook workbook = getWorkbook(template.getTemplatePath());

        int index = workbook.getSheetIndex("每班加油记录本");
        workbook.removeSheetAt(index);
        int temp = workbook.getSheetIndex("_temp");
        workbook.setSheetName(temp, "每班加油记录本");
        workbook.setSheetHidden(temp, false);

        Sheet sheet = workbook.cloneSheet(temp);
        workbook.setSheetName(workbook.getSheetIndex(sheet), "_temp");
        workbook.setSheetHidden(workbook.getSheetIndex(sheet), true);


        return workbook;
    }

    /**
     * 查询条件
     * 一定存在 recordDate数据
     *
     * @return 结果
     */
    protected final DateQuery getDateQuery(JobExecuteInfo excelDTO) {
        DateQuery dateQuery = excelDTO.getDateQuery();
        if (Objects.isNull(dateQuery)) {
            // 默认取当前时间
            Date date = new Date();
            dateQuery = new DateQuery(date, date, date);
        }
        return dateQuery;
    }


    /**
     * 获取操作的文件
     *
     * @param templatePath 模板路径
     * @return 文件
     */
    protected final Workbook getWorkbook(String templatePath) {
        try {
            return WorkbookFactory.create(POICacheManager.getFile(templatePath));
        } catch (IOException | InvalidFormatException e) {
            throw new NullPointerException("模板路径不存在" + templatePath);
        }
    }

    /**
     * 获取处理后的文件名
     *
     * @param templateName 生成的模板名称
     * @param templatePath 模板的路径
     * @return 文件名
     */
    @Override
    protected String handlerFileName(String templateName, String templatePath, String code, ReportTemplateTypeEnum templateTypeEnum, DateQuery dateQuery) {
        // 模板的扩展名 如xlsx
        String fileExtension = FileUtil.getTypePart(templatePath);

        // yyyy-MM-dd_HH
        String datePart = DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd");
        return templateName + "_" + datePart + "." + fileExtension;
    }
}
