package com.cisdi.steel.module.job.a2.execute;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.LianjiaoWDWriter;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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
public class LianjiaoWDExecute extends AbstractJobExecuteExecute {

    @Autowired
    private LianjiaoWDWriter lianjiaoWDWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return lianjiaoWDWriter;
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
            try {
                ExcelPathInfo excelPathInfo = this.getPathInfoByTemplate(template);
                // 参数缺一不可
                WriterExcelDTO writerExcelDTO = WriterExcelDTO.builder()
                        .startTime(new Date())
                        .jobEnum(jobExecuteInfo.getJobEnum())
                        .jobExecuteEnum(jobExecuteInfo.getJobExecuteEnum())
                        .dateQuery(jobExecuteInfo.getDateQuery())
                        .template(template)
                        .excelPathInfo(excelPathInfo)
                        .build();
                // 4、5填充数据
                Workbook workbook = getCurrentExcelWriter().writerExcelExecute(writerExcelDTO);
                // 6、生成文件
                this.createFile(workbook, excelPathInfo,writerExcelDTO);

                // 7、插入索引
                ReportIndex reportIndex = new ReportIndex();
                reportIndex.setSequence(template.getSequence())
                        .setReportCategoryCode(template.getReportCategoryCode())
                        .setName(excelPathInfo.getFileName())
                        .setPath(excelPathInfo.getSaveFilePath())
                        .setIndexLang(LanguageEnum.getByLang(template.getTemplateLang()).getName())
                        .setIndexType(ReportTemplateTypeEnum.getType(template.getTemplateType()).getCode());
                reportIndexService.insertReportRecord(reportIndex);
            } catch (Exception e) {
                log.error(jobExecuteInfo.getJobEnum().getName() + "-->生成模板失败" + e.getMessage());
            }
        }
    }

    protected void createFile(Workbook workbook, ExcelPathInfo excelPathInfo,WriterExcelDTO writerExcelDTO) throws IOException {
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

        if(DateUtil.isLastDayOfMonth(new Date())){
            for (int i=5;i<numberOfSheets;i++){
                workbook.removeSheetAt(i);
            }
            fos.close();
            workbook.close();
        }else {
            fos.close();
            workbook.close();
            FileUtils.deleteFile(writerExcelDTO.getTemplate().getTemplatePath());
            FileUtils.copyFile(excelPathInfo.getSaveFilePath(),writerExcelDTO.getTemplate().getTemplatePath());
        }
    }
}
