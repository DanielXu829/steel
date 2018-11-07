package com.cisdi.steel.module.job;

import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.enums.JobExecuteEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import com.cisdi.steel.module.report.service.ReportIndexService;
import com.cisdi.steel.module.report.util.FileNameHandlerUtil;
import com.cisdi.steel.module.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 通用的执行器
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
@SuppressWarnings("ALL")
@Slf4j
public abstract class AbstractJobExecuteExecute implements IJobExecute {

    @Autowired
    protected SysConfigService sysConfigService;

    @Autowired
    protected HttpUtil httpUtil;

    @Autowired
    protected JobProperties jobProperties;

    @Autowired
    protected ReportCategoryTemplateService templateService;

    @Autowired
    protected ReportIndexService reportIndexService;

    protected IExcelReadWriter excelWriter;

    /**
     * 初始化参数
     */
    public abstract void initConfig();

    @Override
    public void execute(JobEnum jobEnum, JobExecuteEnum jobExecuteEnum, DateQuery dateQuery) {
        this.executeDetail(jobEnum, jobExecuteEnum, dateQuery);
    }

    /**
     * 默认的执行
     * 1、遍历模板
     * 2、获取数据
     * 3、处理数据
     * 4、生成文件
     * 5、插入索引
     *
     * @param jobEnum        任务表示
     * @param jobExecuteEnum 状态
     * @param dateQuery      时间段
     */
    protected void executeDetail(JobEnum jobEnum, JobExecuteEnum jobExecuteEnum, DateQuery dateQuery) {
        this.initConfig();
        this.checkParameter(jobEnum, jobExecuteEnum);
        // 获取模板
        List<ReportCategoryTemplate> templates = getTemplateInfo(jobEnum);
        for (ReportCategoryTemplate template : templates) {
            try {
                ExcelPathInfo excelPathInfo = this.getPathInfoByTemplate(template);
                WriterExcelDTO writerExcelDTO = new WriterExcelDTO();
                writerExcelDTO.setStartTime(new Date())
                        .setJobEnum(jobEnum)
                        .setJobExecuteEnum(jobExecuteEnum)
                        .setTemplate(template)
                        .setExcelPathInfo(excelPathInfo)
                        .setDateQuery(dateQuery);
                // 填充数据
                Workbook workbook = excelWriter.writerExcelExecute(writerExcelDTO);
                // 生成文件
                this.createFile(workbook, excelPathInfo);

                ReportIndex reportIndex = new ReportIndex();
                reportIndex.setSequence(template.getSequence());
                reportIndex.setReportCategoryCode(template.getReportCategoryCode());
                reportIndex.setName(excelPathInfo.getFileName());
                reportIndex.setPath(excelPathInfo.getSaveFilePath());
                reportIndex.setIndexLang(LanguageEnum.getByLang(template.getTemplateLang()).getName());
                reportIndex.setIndexType(ReportTemplateTypeEnum.getType(template.getTemplateType()).getCode());
                reportIndexService.insertReportRecord(reportIndex);
            } catch (Exception e) {
                this.handlerException(jobEnum + "-->生成模板失败");
            }
        }
    }

    /**
     * 验证参数是否正确
     *
     * @param jobEnum        任务标识
     * @param jobExecuteEnum 任务状态
     */
    protected final void checkParameter(JobEnum jobEnum, JobExecuteEnum jobExecuteEnum) {
        Objects.requireNonNull(jobEnum, "任务标识不能为空");
        Objects.requireNonNull(jobExecuteEnum, "任务状态不能为空");
    }

    /**
     * 获取需要生成的模板信息
     *
     * @param jobEnum 任务标识
     * @return 模板信息
     */
    protected List<ReportCategoryTemplate> getTemplateInfo(JobEnum jobEnum) {
        // 1、获取需要生成的语言
        String lang = sysConfigService.selectActionByCode(Constants.LANGUAGE_CODE);
        // 2、获取需要生成的模板信息
        List<ReportCategoryTemplate> templates = templateService.selectTemplateInfo(jobEnum.getCode(), lang);
        if (Objects.isNull(templates) || templates.isEmpty()) {
            this.handlerException(jobEnum.getName() + "-->数据库中没有对应的模板");
        }
        return templates;
    }


    /**
     * 获取处理后的文件名
     *
     * @param templateName 生成的模板名称
     * @param templatePath 模板的路径
     * @return 文件名
     */
    protected String handlerFileName(String templateName, String templatePath, ReportTemplateTypeEnum templateTypeEnum) {
        // 模板的扩展名 如.xlsx
        String fileExtension = FileUtil.getTypePart(templatePath);
        // yyyy-MM-dd_HH
        String datePart = FileNameHandlerUtil.handlerName(templateTypeEnum);
        return templateName + "_" + datePart + "." + fileExtension;
    }

    /**
     * 获取生成文件存储的路径信息
     *
     * @param template 模板信息
     * @return 结果
     */
    protected ExcelPathInfo getPathInfoByTemplate(ReportCategoryTemplate template) {
        // 类型
        ReportTemplateTypeEnum templateTypeEnum = ReportTemplateTypeEnum.getType(template.getTemplateType());
        // 文件名称
        String fileName = handlerFileName(template.getTemplateName(), template.getTemplatePath(), templateTypeEnum);
        String langName = LanguageEnum.getByLang(template.getTemplateLang()).getName();
        // 文件保存路径
        String saveFilePath = getSaveFilePath(templateTypeEnum, fileName, langName);
        return new ExcelPathInfo(fileName, saveFilePath);
    }

    /**
     * 多语言版本
     *
     * @param fileName 文件名
     * @return 文件保存路径
     */
    protected String getSaveFilePath(ReportTemplateTypeEnum templateTypeEnum, String fileName, String langName) {
        String partName = templateTypeEnum.getName();
        String resultPath = jobProperties.getFilePath() +
                File.separator + langName +
                File.separator + partName;
        File saveFile = new File(resultPath);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        return resultPath + File.separator + fileName;
    }

    /**
     * 创建晚饭吗
     *
     * @param workbook      文件
     * @param excelPathInfo 文件存储数据
     * @throws IOException 异常
     */
    protected void createFile(Workbook workbook, ExcelPathInfo excelPathInfo) throws IOException {
        // 隐藏 下划线的sheet  强制计算
        FileOutputStream fos = new FileOutputStream(excelPathInfo.getSaveFilePath());
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            if (sheetName.contains("_")) {
                if (sheet.isSelected()) {
                    sheet.setSelected(false);
                }
                workbook.setSheetHidden(i, Workbook.SHEET_STATE_HIDDEN);
            }
        }
        workbook.setForceFormulaRecalculation(true);
        workbook.write(fos);
        fos.close();
    }

    /**
     * 出现错误
     *
     * @param msg 说明
     */
    protected void handlerException(String msg) {
        log.error(msg);
        throw new NullPointerException(msg);
    }
}
