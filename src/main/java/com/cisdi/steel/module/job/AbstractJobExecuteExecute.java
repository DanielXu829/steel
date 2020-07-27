package com.cisdi.steel.module.job;

import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.enums.EditStatusEnum;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.enums.LanguageEnum;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import com.cisdi.steel.module.report.service.ReportIndexService;
import com.cisdi.steel.module.report.util.FileNameHandlerUtil;
import com.cisdi.steel.module.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    /**
     * 初始化操作
     */
    public void initConfig() {

    }

    /**
     * 获取当前的数据处理
     * `
     *
     * @return 结果 不能为null
     */
    public abstract IExcelReadWriter getCurrentExcelWriter();

    @Override
    public void execute(JobExecuteInfo jobExecuteInfo) {
        // 真正的执行的方法
        this.executeDetail(jobExecuteInfo);
    }

    /**
     * 默认的执行
     * 1、初始化数据
     * 2、参数检查
     * 3、遍历模板
     * 4、获取数据
     * 5、处理数据
     * 6、生成文件
     * 7、插入索引
     *
     * @param jobEnum        任务表示
     * @param jobExecuteEnum 状态
     * @param dateQuery      时间段
     */
    protected void executeDetail(JobExecuteInfo jobExecuteInfo) {
        // 1
        initConfig();
        // 2
        this.checkParameter(jobExecuteInfo);
        // 3
        List<ReportCategoryTemplate> templates = getTemplateInfo(jobExecuteInfo.getJobEnum(), jobExecuteInfo.getSequence());
        for (ReportCategoryTemplate template : templates) {

            // 如果最新的报表被锁住，将不执行
            boolean isLocked = this.isLocked(template);
            if (!isLocked) {
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
                            .setRecordDate(dateQuery.getRecordDate())
                            .setId(jobExecuteInfo.getIndexId());

                    this.replaceTemplatePath(reportIndex, template);
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

    /**
     * 替换模板文件使用 生成后的文件 作为模板
     */
    protected void replaceTemplatePath(ReportIndex reportIndex, ReportCategoryTemplate template) {
        String templatePath = reportIndexService.existTemplate(reportIndex);
        if (StringUtils.isNotBlank(templatePath) && !"1".equals(template.getAttr1())) {
            // 修改为生成后文件名称
            template.setTemplatePath(templatePath);
        }

    }

    /**
     * 验证必要参数是否存在
     *
     * @param jobEnum        任务标识
     * @param jobExecuteEnum 任务状态
     */
    protected final void checkParameter(JobExecuteInfo jobExecuteInfo) {
        Objects.requireNonNull(jobExecuteInfo, "参数错误" + jobExecuteInfo);
        Objects.requireNonNull(jobExecuteInfo.getJobEnum(), "任务标识不能为空");
        Objects.requireNonNull(getCurrentExcelWriter(), "没有对应的数据读取和写入策略");
    }

    /**
     * 获取需要生成的模板信息
     *
     * @param jobEnum 任务标识
     * @return 模板信息
     */
    protected List<ReportCategoryTemplate> getTemplateInfo(JobEnum jobEnum, String sequence) {
        // 1、获取需要生成的语言
        String lang = sysConfigService.selectActionByCode(Constants.LANGUAGE_CODE);
        // 2、获取需要生成的模板信息
        List<ReportCategoryTemplate> templates = templateService.selectTemplateInfo(jobEnum.getCode(), lang, sequence);
        if (Objects.isNull(templates) || templates.isEmpty()) {
            this.handlerException(jobEnum.getName() + "-->数据库中没有对应的模板" + jobEnum.getCode());
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
    protected String handlerFileName(String templateName, String templatePath, String code, ReportTemplateTypeEnum templateTypeEnum, DateQuery dateQuery) {
        // 模板的扩展名 如xlsx
        String fileExtension = FileUtil.getTypePart(templatePath);

        // yyyy-MM-dd_HH
        String datePart = FileNameHandlerUtil.handlerName(templateTypeEnum, dateQuery);

        //自动配煤报表 单独特殊处理到分钟
        if (StringUtils.isNotBlank(code)) {
            if ((JobEnum.jh_zidongpeimei.getCode().equals(code)
                    || JobEnum.jh_ck12zidongpeimeinew.getCode().equals(code)
                    || JobEnum.jh_ck45zidongpeimei.getCode().equals(code)
                    || JobEnum.gl_peiliaodan.getCode().equals(code)
                    || JobEnum.gl_peiliaodan7.getCode().equals(code)
                    || JobEnum.gl_peiliaodan6.getCode().equals(code))) {
                // yyyy-MM-dd_HH_mm
                datePart = DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd_HH_mm");
            } else if (JobEnum.sj_liushaogycanshu.getCode().equals(code) || JobEnum.sj_shengchan4.getCode().equals(code)) {
                datePart = DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd_HH");
            }
        }
        return templateName + "_" + datePart + "." + fileExtension;
    }

    /**
     * 获取生成文件存储的路径信息
     *
     * @param template 模板信息
     * @return 结果
     */
    protected ExcelPathInfo getPathInfoByTemplate(ReportCategoryTemplate template, DateQuery dateQuery) {
        // 类型
        ReportTemplateTypeEnum templateTypeEnum = ReportTemplateTypeEnum.getType(template.getTemplateType());
        // 文件名称
        String fileName = handlerFileName(template.getTemplateName(), template.getTemplatePath(), template.getReportCategoryCode(), templateTypeEnum, dateQuery);
        String langName = LanguageEnum.getByLang(template.getTemplateLang()).getName();
        // 文件保存路径
        String saveFilePath = getSaveFilePath(template.getSequence(), templateTypeEnum, fileName, langName);
        return new ExcelPathInfo(fileName, saveFilePath);
    }

    /**
     * 多语言版本
     * 路径 + 语言 + 序号 + 类型 + 文件名
     *
     * @param fileName 文件名
     * @return 文件保存路径
     */
    protected String getSaveFilePath(String sequence,
                                     ReportTemplateTypeEnum templateTypeEnum,
                                     String fileName,
                                     String langName) {
        String partName = templateTypeEnum.getName();
        String resultPath = jobProperties.getFilePath() +
                File.separator + langName +
                File.separator + sequence +
                File.separator + partName;
        File saveFile = new File(resultPath);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        return resultPath + File.separator + fileName;
    }

    /**
     * 创建文件
     *
     * @param workbook      文件
     * @param excelPathInfo 文件存储数据
     * @throws IOException 异常
     */
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
            }else{
//                sheet.setForceFormulaRecalculation(true);
                int rowNum = sheet.getLastRowNum();
                for(int r=0;r<=rowNum;r++){
                    updateFormula(workbook,sheet,r);
                }
            }
        }
        workbook.setForceFormulaRecalculation(true);
        workbook.write(fos);
        fos.close();
    }

    /**
     * 更新公式
     * @param workbook
     * @param sheet
     * @param row
     */
    private static void updateFormula(Workbook workbook,Sheet sheet,int rowNum){
        Row row=sheet.getRow(rowNum);
        if(null == row){
            return;
        }
        Cell cell=null;
        org.apache.poi.ss.usermodel.FormulaEvaluator eval=null;
        if(workbook instanceof HSSFWorkbook)
            eval=new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
        else if(workbook instanceof XSSFWorkbook)
            eval=new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
        if(null == eval){
            return;
        }
        for(int i=row.getFirstCellNum();i<row.getLastCellNum();i++){
            cell=row.getCell(i);
            if(null == cell){
                continue;
            }
            if(cell.getCellType()==Cell.CELL_TYPE_FORMULA)
                eval.evaluateFormulaCell(cell);
        }
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

    /**
     * 生成当前时间前几天，后几天的数据
     *
     * @param jobExecuteInfo 执行信息
     * @param dayIndex       当前 前/后 n 天，
     */
    protected void executeDateParam(JobExecuteInfo jobExecuteInfo, int dayIndex) {
        if (Objects.isNull(jobExecuteInfo.getDateQuery())) {
            Date date = new Date();
            DateQuery dateQuery = new DateQuery(date, date, date);
            jobExecuteInfo.setDateQuery(dateQuery);
        }

        DateQuery dateQuery = jobExecuteInfo.getDateQuery();
        dateQuery.setRecordDate(DateUtil.addDays(DateUtil.getDateEndTime59(dateQuery.getRecordDate()), dayIndex));
        jobExecuteInfo.setDateQuery(dateQuery);
        // 真正的执行的方法
        this.executeDetail(jobExecuteInfo);
    }

    /**
     * 模板通用清空操作
     *
     * @param dateQuery      时间参数
     * @param excelPathInfo  文件输出信息
     * @param writerExcelDTO 模板信息
     * @param tempPath       空模板地址
     */
    public void clearTemp(DateQuery dateQuery, ExcelPathInfo excelPathInfo, WriterExcelDTO writerExcelDTO, String tempPath) {
        DateQuery query = new DateQuery(dateQuery.getStartTime(), dateQuery.getEndTime(), dateQuery.getRecordDate());
        DateQuery oldDateQuery = DateQueryUtil.handlerDelay(query, writerExcelDTO.getTemplate().getBuildDelay(), writerExcelDTO.getTemplate().getBuildDelayUnit(), false);
        //模板清空此操作
        String formatDateTime = DateUtil.getFormatDateTime(oldDateQuery.getRecordDate(), DateUtil.yyyyMMddFormat);
        String formatDateTime1 = DateUtil.getFormatDateTime(new Date(), DateUtil.yyyyMMddFormat);

        String srcUrl = excelPathInfo.getSaveFilePath();
        if (!formatDateTime.equals(formatDateTime1)) {
            srcUrl = tempPath;
        }
        FileUtils.deleteFile(writerExcelDTO.getTemplate().getTemplatePath());
        FileUtils.copyFile(srcUrl, writerExcelDTO.getTemplate().getTemplatePath());
    }

    /**
     *
     * @param reportTemplate
     * @return isLocked 是否锁住
     */
    private boolean isLocked(ReportCategoryTemplate reportTemplate) {
        // 默认没有被锁住
        boolean isLocked = false;

        if (reportTemplate != null) {
            String reportCategoryCode = reportTemplate.getReportCategoryCode();
            String sequence = reportTemplate.getSequence();
            Date lastLockedCreateTime = null;
            Date lastUnLockedCreateTime = null;

            EditStatusEnum editStatusEnum= EditStatusEnum.Locked;
            int editStatus =  editStatusEnum.getEditStatus();
            // 查询被锁住的最新报表
            ReportIndex reportIndexLocked = reportIndexService.getReportIndexInfo(reportCategoryCode, sequence, editStatus);
            if (reportIndexLocked != null) {
                lastLockedCreateTime = reportIndexLocked.getCreateTime();
            }

            editStatusEnum= EditStatusEnum.Release;
            editStatus =  editStatusEnum.getEditStatus();
            // 查询所有没有被锁住的最新报表
            ReportIndex reportIndexUnLocked = reportIndexService.getReportIndexInfo(reportCategoryCode, sequence, editStatus);
            if (reportIndexUnLocked != null) {
                lastUnLockedCreateTime = reportIndexUnLocked.getCreateTime();
            }
            // 根据最新被锁住的报表创建时间与最新没有被锁住的报表创建时间做对比，判断最新报表是否被锁住
            if (lastLockedCreateTime != null) {
                if (lastUnLockedCreateTime != null) {
                    isLocked = lastLockedCreateTime.after(lastUnLockedCreateTime);
                }
            }
        }

        return isLocked;
    }
}
