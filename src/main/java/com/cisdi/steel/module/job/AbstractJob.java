package com.cisdi.steel.module.job;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.module.data.DataCatalog;
import com.cisdi.steel.module.data.IDataHandler;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import com.cisdi.steel.module.report.service.ReportIndexService;
import com.cisdi.steel.module.report.util.FileNameHandlerUtil;
import com.cisdi.steel.module.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * <p>Description:  执行定时器的抽象类  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Slf4j
@DisallowConcurrentExecution
public abstract class AbstractJob implements Job, Serializable {


    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private ReportCategoryTemplateService templateService;

    @Autowired
    private ReportIndexService reportIndexService;

    @Autowired
    private SysConfigService sysConfigService;

    protected IDataHandler dataHandler;

    /**
     * 当前语言 默认无
     */
    private LanguageEnum languageEnum = LanguageEnum.none;

    /**
     * 记录开始时间
     */
    private Date startTime;

    /**
     * 数据获取完成后的时间
     */
    private Date dataTime;

    /**
     * 初始化一些数据
     */
    protected abstract void init();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        startTime = new Date();
        // 初始化数据
        init();
        log.debug(getJobName() + "执行");
        try {
            // 1、获取需要生成的语言
            String lang = sysConfigService.selectActionByCode(Constants.LANGUAGE_CODE);
            LanguageEnum languageEnum = LanguageEnum.getByLang(lang);
            setLanguageEnum(languageEnum);

            // 2、获取需要生成的模板信息
            List<ReportCategoryTemplate> templates = getTemplateInfo(lang);
            if (Objects.isNull(templates) || templates.isEmpty()) {
                handlerException(getJobName() + "-->数据库中没有对应的模板");
            }
            // 3、获取需要生成的数据
            if (Objects.isNull(dataHandler)) {
                handlerException(getJobName() + "-->数据注入失败");
            }
            for(ReportCategoryTemplate template:templates){
                String templatePath = template.getTemplatePath();
            }
            Map<DataCatalog, Map<String, Object>> excelData = dataHandler.getExcelData();
            dataTime = new Date();
            // 4、生成报表文件,并存储 存入数据库
            Set<DataCatalog> dataCatalogs = excelData.keySet();
            for (DataCatalog dataCatalog : dataCatalogs) {
                Map<String, Object> templateData = excelData.get(dataCatalog);
                if (Objects.isNull(templateData)) {
                    // 不存在数据
                    continue;
                }

                handlerTemplateGenerate(dataCatalog, templates, templateData);

            }
        } catch (Exception e) {
            log.error(getJobName() + "生成失败", e);
        }
    }

    /**
     * 处理 模板生成
     *
     * @param dataCatalog  目录信息
     * @param templates    所有模板
     * @param templateData 填充数据
     */
    public void handlerTemplateGenerate(DataCatalog dataCatalog, List<ReportCategoryTemplate> templates, Map<String, Object> templateData) {
        for (ReportCategoryTemplate template : templates) {
            String sequenceName = dataCatalog.getSequenceName();
            // 相同才生成 不同 不生成
            if (!sequenceName.equals(template.getSequence())) {
                continue;
            }
            FileOutputStream fos = null;
            Workbook workbook;
            try {
                TemplateExportParams params = new TemplateExportParams(template.getTemplatePath());
                params.setScanAllsheet(true);

                ReportTemplateTypeEnum templateTypeEnum = ReportTemplateTypeEnum.getType(template.getTemplateType());
                // 文件名称
                String fileName = handlerFileName(template.getTemplateName(), template.getTemplatePath(), templateTypeEnum);
                // 文件保存路径
                String saveFilePath = getSaveFilePath(dataCatalog, templateTypeEnum, fileName);

                handlerMapData(templateData, fileName, saveFilePath, template, templateTypeEnum);

                workbook = ExcelExportUtil.exportExcel(params, templateData);
                workbook.setForceFormulaRecalculation(true);
                // 强制计算
                fos = new FileOutputStream(saveFilePath);
                workbook.write(fos);

                // 添加数据
                reportIndexService.insertReportRecord(getJobName(), saveFilePath, dataCatalog.getSequenceName(),templateTypeEnum.getCode(),getLanguageEnum().getLang());
            } catch (IOException e) {
                log.error(getJobName() + template.getTemplateName() + "生成错误：" + e.getMessage(), e);
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理模板的元数据
     *
     * @param map                    数据
     * @param fileName               文件名
     * @param saveFilePath           保存路径
     * @param template               模板信息
     * @param reportTemplateTypeEnum 模板类型
     */
    private void handlerMapData(Map<String, Object> map, String fileName, String saveFilePath, ReportCategoryTemplate template, ReportTemplateTypeEnum reportTemplateTypeEnum) {
        map.put("date", new Date());
        map.put("fileName", fileName);
        map.put("fileType", reportTemplateTypeEnum.getCode());
        map.put("language", getLanguageEnum().getLang());
        map.put("templatePath", template.getTemplatePath());
        map.put("templateName", template.getTemplateName());
        map.put("buildType", "Automatically");
        map.put("buildInitTime", startTime);
        map.put("buildDataTime", dataTime);
        map.put("buildEndTime", new Date());
        map.put("excelFile", saveFilePath);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        map.put("year", cal.get(Calendar.YEAR));
        map.put("month",cal.get(Calendar.MONTH));
        map.put("days",cal.get(Calendar.DATE));
    }

    /**
     * 获取需要生成的模板信息
     *
     * @param lang 语言
     * @return 模板
     */
    protected List<ReportCategoryTemplate> getTemplateInfo(String lang) {
        return templateService.selectTemplateInfo(getCurrentJob().getCode(), lang);
    }

    /**
     * 多语言版本
     *
     * @param dataCatalog 文件保存的目录
     * @param fileName    文件名
     * @return 文件保存路径
     */
    public String getSaveFilePath(DataCatalog dataCatalog, ReportTemplateTypeEnum templateTypeEnum, String fileName) {
        String partName = templateTypeEnum.getName();
        String langName = getLanguageEnum().getName();
        String parentPath = jobProperties.getFilePath() + File.separator + langName + File.separator + dataCatalog.getFileCatalog() + File.separator + partName;
        File saveFile = new File(parentPath);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        return parentPath + File.separator + fileName;
    }

    /**
     * 获取处理后的文件名
     *
     * @param templateName 生成的模板名称
     * @param templatePath 模板的路径
     * @return 文件名
     */
    public String handlerFileName(String templateName, String templatePath, ReportTemplateTypeEnum templateTypeEnum) {
        // 模板的扩展名 如.xlsx
        String fileExtension = FileUtil.getTypePart(templatePath);
        // yyyy-MM-dd_HH
        String datePart = handlerDateNamePart(templateTypeEnum);
        return templateName + datePart + "." + fileExtension;
    }

    /**
     * 获取 拼接的时间
     *
     * @param templateTypeEnum 模板类型
     * @return 文件的的时间部分
     */
    public String handlerDateNamePart(ReportTemplateTypeEnum templateTypeEnum) {
        return FileNameHandlerUtil.handlerName(templateTypeEnum);
    }


    /**
     * 出现错误
     *
     * @param msg 说明
     * @throws JobExecutionException 异常
     */
    private void handlerException(String msg) throws JobExecutionException {
        log.error(msg);
        throw new JobExecutionException(msg);
    }

    /**
     * 获取当前的 工作类型
     *
     * @return 获取当前的类型 不能为null
     */
    public abstract JobEnum getCurrentJob();

    /**
     * 名称
     *
     * @return 工作的名称
     */
    private String getJobName() {
        JobEnum job = getCurrentJob();
        if (Objects.isNull(job)) {
            return "";
        }
        return job.getName();
    }


    public LanguageEnum getLanguageEnum() {
        return languageEnum;
    }

    public void setLanguageEnum(LanguageEnum languageEnum) {
        this.languageEnum = languageEnum;
    }
}
