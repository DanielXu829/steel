package com.cisdi.steel.module.job.drt.execute;

import com.cisdi.steel.module.job.drt.writer.DrtExcelWriter;
import com.cisdi.steel.module.job.drt.writer.IDrtWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DrtExcelExecute extends DrtAbstractExecute{

//    @Autowired
//    protected JobProperties jobProperties;
//
//    @Autowired
//    protected ReportIndexService reportIndexService;
//
//    @Autowired
//    protected ReportCategoryTemplateMapper reportCategoryTemplateMapper;

    @Autowired
    private DrtExcelWriter drtExcelWriter;

//    public void execute(DrtJobExecuteInfo drtJobExecuteInfo) {
//        Long templateId = drtJobExecuteInfo.getReportCategoryTemplateId();
//        ReportCategoryTemplate template = reportCategoryTemplateMapper.selectById(templateId);
//
//        boolean isLocked = isLocked(template);
//        if (isLocked) {
//            log.error(drtJobExecuteInfo.getTemplateName()+ "--> 报表正在被在线预览，暂时无法运行");
//            return;
//        }
//        Date date = new Date();
//        DateQuery dateQuery = new DateQuery(date, date, date);
//        try {
//            if (Objects.nonNull(drtJobExecuteInfo.getDateQuery())) {
//                dateQuery = drtJobExecuteInfo.getDateQuery();
//            }
//            if (Objects.isNull(dateQuery.getDelay()) || dateQuery.getDelay()) {
//                // 处理延迟问题
//                dateQuery = DateQueryUtil.handlerDelay(dateQuery, template.getBuildDelay(), template.getBuildDelayUnit());
//            }
//
//            ExcelPathInfo excelPathInfo = getPathInfoByTemplate(template, dateQuery);
//            // 参数缺一不可
//            DrtWriterDTO drtWriterDTO = DrtWriterDTO.builder()
//                    .startTime(new Date())
//                    .jobExecuteEnum(drtJobExecuteInfo.getJobExecuteEnum())
//                    .dateQuery(dateQuery)
//                    .template(template)
//                    .excelPathInfo(excelPathInfo)
//                    .build();
//
//            ReportIndex reportIndex = new ReportIndex();
//            reportIndex.setSequence(template.getSequence())
//                    .setReportCategoryCode(template.getReportCategoryCode())
//                    .setName(excelPathInfo.getFileName())
//                    .setPath(excelPathInfo.getSaveFilePath())
//                    .setIndexLang(LanguageEnum.getByLang(template.getTemplateLang()).getName())
//                    .setIndexType(ReportTemplateTypeEnum.getType(template.getTemplateType()).getCode())
//                    .setCurrDate(dateQuery.getRecordDate())
//                    .setRecordDate(dateQuery.getRecordDate())
//                    .setId(drtJobExecuteInfo.getIndexId());
//
//            this.replaceTemplatePath(reportIndex, template);
//            // 4、5填充数据
//            Workbook workbook = drtExcelWriter.writerExcelExecute(drtWriterDTO);
//            // 6、生成文件
//            this.createFile(workbook, excelPathInfo);
//
//            // 7、插入索引
//            reportIndexService.insertReportRecord(reportIndex);
//        } catch (Exception e) {
//            log.error(drtJobExecuteInfo.getTemplateName()+ "-->生成模板失败" + e.getMessage(), e);
//        }
//    }

    @Override
    public IDrtWriter getDrtWriter() {
        return drtExcelWriter;
    }

//    /**
//     * 获取生成文件存储的路径信息
//     *
//     * @param template 模板信息
//     * @return 结果
//     */
//    protected ExcelPathInfo getPathInfoByTemplate(ReportCategoryTemplate template, DateQuery dateQuery) {
//        // 类型
//        ReportTemplateTypeEnum templateTypeEnum = ReportTemplateTypeEnum.getType(template.getTemplateType());
//        // 文件名称
//        String fileName = handlerFileName(template.getTemplateName(), template.getTemplatePath(), template.getReportCategoryCode(), templateTypeEnum, dateQuery);
//        String langName = LanguageEnum.getByLang(template.getTemplateLang()).getName();
//        // 文件保存路径
//        String saveFilePath = getSaveFilePath(template.getSequence(), templateTypeEnum, fileName, langName);
//        return new ExcelPathInfo(fileName, saveFilePath);
//    }
//
//    /**
//     * 多语言版本
//     * 路径 + 语言 + 序号 + 类型 + 文件名
//     *
//     * @param fileName 文件名
//     * @return 文件保存路径
//     */
//    protected String getSaveFilePath(String sequence,
//                                     ReportTemplateTypeEnum templateTypeEnum,
//                                     String fileName,
//                                     String langName) {
//        String partName = templateTypeEnum.getName();
//        String resultPath = jobProperties.getFilePath() +
//                File.separator + langName +
//                File.separator + sequence +
//                File.separator + partName;
//        File saveFile = new File(resultPath);
//        if (!saveFile.exists()) {
//            saveFile.mkdirs();
//        }
//        return resultPath + File.separator + fileName;
//    }
//    /**
//     * 获取处理后的文件名
//     *
//     * @param templateName 生成的模板名称
//     * @param templatePath 模板的路径
//     * @return 文件名
//     */
//    protected String handlerFileName(String templateName, String templatePath, String code, ReportTemplateTypeEnum templateTypeEnum, DateQuery dateQuery) {
//        // 模板的扩展名 如xlsx
//        String fileExtension = FileUtil.getTypePart(templatePath);
//        // yyyy-MM-dd_HH
//        String datePart = FileNameHandlerUtil.handlerName(templateTypeEnum, dateQuery);
//        return templateName + "_" + datePart + "." + fileExtension;
//    }
//
//    /**
//     * 替换模板文件使用 生成后的文件 作为模板
//     */
//    protected void replaceTemplatePath(ReportIndex reportIndex, ReportCategoryTemplate template) {
//        if ("1".equals(template.getAttr1())) {
//            return;
//        }
//        String templatePath = reportIndexService.existTemplate(reportIndex);
//        if (StringUtils.isNotBlank(templatePath)) {
//            template.setTemplatePath(templatePath);
//        }
//    }
//
//    public void createFile(Workbook workbook, ExcelPathInfo excelPathInfo) throws IOException {
//        // 隐藏 下划线的sheet  强制计算
//        FileOutputStream fos = new FileOutputStream(excelPathInfo.getSaveFilePath());
//        int numberOfSheets = workbook.getNumberOfSheets();
//        for (int i = 0; i < numberOfSheets; i++) {
//            Sheet sheet = workbook.getSheetAt(i);
//            String sheetName = sheet.getSheetName();
//            // 以下划线开头的全部隐藏掉
//            if (sheetName.startsWith("_")) {
//                if (sheet.isSelected()) {
//                    sheet.setSelected(false);
//                }
//                workbook.setSheetHidden(i, Workbook.SHEET_STATE_HIDDEN);
//            }else{
//                // sheet.setForceFormulaRecalculation(true);
//                int rowNum = sheet.getLastRowNum();
//                for(int r=0;r<=rowNum;r++){
//                    updateFormula(workbook,sheet,r);
//                }
//            }
//        }
//        workbook.setForceFormulaRecalculation(true);
//        workbook.write(fos);
//        fos.close();
//    }
//
//    /**
//     * 更新公式
//     * @param workbook
//     * @param sheet
//     * @param
//     */
//    private static void updateFormula(Workbook workbook,Sheet sheet,int rowNum){
//        Row row=sheet.getRow(rowNum);
//        if(null == row){
//            return;
//        }
//        Cell cell=null;
//        org.apache.poi.ss.usermodel.FormulaEvaluator eval=null;
//        if(workbook instanceof HSSFWorkbook)
//            eval=new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
//        else if(workbook instanceof XSSFWorkbook)
//            eval=new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
//        if(null == eval){
//            return;
//        }
//        for(int i=row.getFirstCellNum();i<row.getLastCellNum();i++){
//            cell=row.getCell(i);
//            if(null == cell){
//                continue;
//            }
//            if(cell.getCellType()==Cell.CELL_TYPE_FORMULA)
//                eval.evaluateFormulaCell(cell);
//        }
//    }
//
//    protected boolean isLocked(ReportCategoryTemplate reportTemplate) {
//        // 默认没有被锁住
//        boolean isLocked = false;
//
//        if (reportTemplate != null) {
//            String reportCategoryCode = reportTemplate.getReportCategoryCode();
//            String sequence = reportTemplate.getSequence();
//            Date lastLockedCreateTime = null;
//            Date lastUnLockedCreateTime = null;
//
//            EditStatusEnum editStatusEnum= EditStatusEnum.Locked;
//            int editStatus =  editStatusEnum.getEditStatus();
//            // 查询被锁住的最新报表
//            ReportIndex reportIndexLocked = reportIndexService.getReportIndexInfo(reportCategoryCode, sequence, editStatus);
//            if (reportIndexLocked != null) {
//                lastLockedCreateTime = reportIndexLocked.getCreateTime();
//            }
//
//            editStatusEnum= EditStatusEnum.Release;
//            editStatus =  editStatusEnum.getEditStatus();
//            // 查询所有没有被锁住的最新报表
//            ReportIndex reportIndexUnLocked = reportIndexService.getReportIndexInfo(reportCategoryCode, sequence, editStatus);
//            if (reportIndexUnLocked != null) {
//                lastUnLockedCreateTime = reportIndexUnLocked.getCreateTime();
//            }
//            // 根据最新被锁住的报表创建时间与最新没有被锁住的报表创建时间做对比，判断最新报表是否被锁住
//            if (lastLockedCreateTime != null) {
//                if (lastUnLockedCreateTime != null) {
//                    isLocked = lastLockedCreateTime.after(lastUnLockedCreateTime);
//                }
//            }
//        }
//
//        return isLocked;
//    }
}
