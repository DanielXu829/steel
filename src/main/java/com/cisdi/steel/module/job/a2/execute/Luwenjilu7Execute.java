package com.cisdi.steel.module.job.a2.execute;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.Luwenjilu7Writer;
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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 炼焦
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
public class Luwenjilu7Execute extends AbstractJobExecuteExecute {

    @Autowired
    private Luwenjilu7Writer luwenjilu7Writer;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return luwenjilu7Writer;
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

    @Override
    public void createFile(Workbook workbook, ExcelPathInfo excelPathInfo, WriterExcelDTO writerExcelDTO, DateQuery dateQuery) throws IOException {
        // 隐藏 下划线的sheet  强制计算
        FileOutputStream fos = new FileOutputStream(excelPathInfo.getSaveFilePath());
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            // 以下划线开头的全部隐藏掉
            if (sheetName.startsWith("_")) {
                if (sheet.isSelected()) {
                    sheet.setSelected(false);
                }
                workbook.setSheetHidden(i, Workbook.SHEET_STATE_HIDDEN);
            }

        }
        workbook.setForceFormulaRecalculation(true);
        workbook.write(fos);
        fos.close();
        workbook.close();
        String formatDateTime = DateUtil.getFormatDateTime(dateQuery.getRecordDate(), DateUtil.yyyyMMFormat);
        String formatDateTime1 = DateUtil.getFormatDateTime(new Date(), DateUtil.yyyyMMFormat);
        if (!formatDateTime1.equals(formatDateTime)) {
            FileUtils.deleteFile(writerExcelDTO.getTemplate().getTemplatePath());
            //FileUtils.copyFile("D:\\template\\焦化\\CK67-炼焦-7#炉温记录报表（日）copy.xlsx",writerExcelDTO.getTemplate().getTemplatePath());
            FileUtils.copyFile("/u01/templates/焦化/CK67-炼焦-7#炉温记录报表（日）copy.xlsx", writerExcelDTO.getTemplate().getTemplatePath());
        } else {
            FileUtils.deleteFile(writerExcelDTO.getTemplate().getTemplatePath());
            FileUtils.copyFile(excelPathInfo.getSaveFilePath(), writerExcelDTO.getTemplate().getTemplatePath());
        }
    }

}
