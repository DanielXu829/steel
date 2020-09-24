package com.cisdi.steel.module.report.service.impl;

import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.exception.LeafException;
import com.cisdi.steel.common.poi.ExportWordUtil;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.report.dto.ReportTemplateConfigDTO;
import com.cisdi.steel.module.report.dto.ReportTemplateSheetDTO;
import com.cisdi.steel.module.report.dto.WordTitleConfigDTO;
import com.cisdi.steel.module.report.entity.*;
import com.cisdi.steel.module.report.enums.*;
import com.cisdi.steel.module.report.mapper.ReportTemplateConfigMapper;
import com.cisdi.steel.module.report.mapper.ReportTemporaryFileMapper;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import com.cisdi.steel.module.report.service.ReportTemplateConfigService;
import com.cisdi.steel.module.report.service.ReportTemplateSheetService;
import com.cisdi.steel.module.report.service.ReportTemplateTagsService;
import com.cisdi.steel.module.report.service.TargetManagementService;
import com.cisdi.steel.module.report.util.ExcelStyleUtil;
import com.cisdi.steel.module.report.util.ReportConstants;
import com.cisdi.steel.module.report.util.TargetManagementUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 报表动态模板配置 服务实现类 </p>
 * <P>Date: 2020-09-02 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@Service
@Slf4j
public class ReportTemplateConfigServiceImpl extends BaseServiceImpl<ReportTemplateConfigMapper
        , ReportTemplateConfig> implements ReportTemplateConfigService {

    //点位数据占位公式
    private static final String formula = "IF(cell%=\"\",\"\",cell%)";
    private static final String[] letterArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static final int REPORT_TITLE_ROW_INDEX = 1; // 标题行
    public static final int TARGET_NAME_BEGIN_ROW = 2; // 顶层节点行
    private static final int firstDataColumnIndex = 2;//从开始填充点位的列开始，下标从0开始，并且排除时间列
    private static final int heightInPoints = 18;//普通行高度
    private static final int heightInPointsHeader = 25;//参数表头行高度
    private static final int heightInPointsTitle = 45;//大标题行高度
    private static final int cellWidth = 14 * 256;//普通列宽度
    private static final int timeCellWidth = 18 * 256;//普通列宽度
    private static final String AVERAGE_FORMULA = "IFERROR(AVERAGE(%s:%s), \"\")";
    //默认小数点位
    private static final int defaultScale = 2;
    private Map<Long, TargetManagement> allTargetManagements;

    @Autowired
    private JobProperties jobProperties;
    @Autowired
    private ReportTemplateSheetService reportTemplateSheetService;
    @Autowired
    private ReportTemplateTagsService reportTemplateTagsService;
    @Autowired
    private TargetManagementService targetManagementService;
    @Autowired
    private ReportTemplateConfigMapper reportTemplateConfigMapper;
    @Autowired
    private TargetManagementMapper targetManagementMapper;
    @Autowired
    private ReportTemporaryFileMapper reportTemporaryFileMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateDTO(ReportTemplateConfigDTO templateConfigDTO) {
        // 生成临时模板文件。
        ReportTemplateConfig reportTemplateConfig = templateConfigDTO.getReportTemplateConfig();
        if (StringUtils.isBlank(reportTemplateConfig.getTemplateName())) {
            // 如果templateName为空，代表是excel，默认取第一个sheet的title
            reportTemplateConfig.setTemplateName(templateConfigDTO.getReportTemplateSheetDTOs()
                    .get(0).getReportTemplateSheet().getSheetTitle());
        }
        // 保存配置信息json串
        reportTemplateConfig.setTemplateConfigJsonString(JSON.toJSONString(templateConfigDTO));
        // 如果id存在则更新，不存在则新增
        if (reportTemplateConfig.getId() != null && reportTemplateConfig.getId() > 0) {
            reportTemplateConfig.setUpdatedTime(new Date());
            this.updateRecord(reportTemplateConfig);
        } else {
            reportTemplateConfig.setCreatedTime(new Date());
            reportTemplateConfig.setUpdatedTime(new Date());
            this.insertRecord(reportTemplateConfig);
        }

        long configId = reportTemplateConfig.getId();
        // 清空每个sheet的tag
        List<ReportTemplateSheet> reportTemplateSheets =
                reportTemplateSheetService.selectByConfigId(configId);
        List<Long> templateSheetIds = reportTemplateSheets.stream().map(ReportTemplateSheet::getId)
                .collect(Collectors.toList());
        templateSheetIds.forEach(id -> reportTemplateTagsService.deleteBySheetId(id));
        // 清空sheet列表
        reportTemplateSheetService.deleteByConfigId(configId);
        // 组装sheet数据
        List<ReportTemplateSheetDTO> reportTemplateSheetDTOs = templateConfigDTO.getReportTemplateSheetDTOs();
        List<ReportTemplateSheet> reportTemplateSheetList = new ArrayList<>();
        reportTemplateSheetDTOs.forEach(sheetDto -> {
            ReportTemplateSheet templateSheet = sheetDto.getReportTemplateSheet();
            templateSheet.setTemplateConfigId(configId);
            reportTemplateSheetList.add(templateSheet);
        });
        // 保存sheet
        reportTemplateSheetService.saveBatch(reportTemplateSheetList);

        // 组装tag数据
        for (ReportTemplateSheetDTO reportTemplateSheetDTO : reportTemplateSheetDTOs) {
            ReportTemplateSheet reportTemplateSheet = reportTemplateSheetDTO.getReportTemplateSheet();
            List<ReportTemplateTags> reportTemplateTagsList = reportTemplateSheetDTO.getReportTemplateTagsList();
            reportTemplateTagsList.forEach(e -> e.setTemplateSheetId(reportTemplateSheet.getId()));
            reportTemplateTagsService.saveBatch(reportTemplateTagsList);
        }
        // 生成临时模板文件
        String templateFilePath = this.generateTemplate(templateConfigDTO);
        log.info("保存模板配置成功，ID: " + reportTemplateConfig.getId());
        log.info("成功生成模板文件，文件路径：" + templateFilePath);
        // 修改templatePath
        reportTemplateConfig.setTemplatePath(templateFilePath);
        this.updateRecord(reportTemplateConfig);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String generateTemporaryFile(ReportTemplateConfigDTO templateConfigDTO) {
        ReportTemplateConfig reportTemplateConfig = templateConfigDTO.getReportTemplateConfig();
        String templateName = reportTemplateConfig.getTemplateName();
        if (StringUtils.isBlank(templateName)) {
            templateName = templateConfigDTO.getReportTemplateSheetDTOs()
                    .get(0).getReportTemplateSheet().getSheetTitle();
            // 如果templateName为空，代表是excel，默认取第一个sheet的title
            reportTemplateConfig.setTemplateName(templateName);
        }

        String templateFilePath = this.generateTemplate(templateConfigDTO);
        reportTemporaryFileMapper.insertOne(new ReportTemporaryFile().setFilePath(templateFilePath).setFileType(0));
        return templateFilePath;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateExcelImage(ReportTemplateConfigDTO templateConfigDTO) {
        ReportTemplateConfig reportTemplateConfig = templateConfigDTO.getReportTemplateConfig();
        String templateName = reportTemplateConfig.getTemplateName();
        if (StringUtils.isBlank(templateName)) {
            templateName = templateConfigDTO.getReportTemplateSheetDTOs()
                    .get(0).getReportTemplateSheet().getSheetTitle();
            // 如果templateName为空，代表是excel，默认取第一个sheet的title
            reportTemplateConfig.setTemplateName(templateName);
        }

        String templateFilePath = this.generateTemplate(templateConfigDTO);
        String tempImagePath = jobProperties.getTempImagePath();
        String imageFilePath = new StringBuilder()
                .append(tempImagePath)
                .append(File.separator)
                .append(templateName)
                .append("_")
                .append(System.currentTimeMillis())
                .append(".png")
                .toString();
        com.spire.xls.Workbook wb = new com.spire.xls.Workbook();
        File file = new File(templateFilePath);
        try ( InputStream fileInputStream1 = new FileInputStream(file);){
            wb.loadFromStream(fileInputStream1);
            com.spire.xls.Worksheet worksheet = wb.getWorksheets().get(0);
            worksheet.saveToImage(imageFilePath);
        } catch(Exception e) {
            log.error("生成excel临时预览图片失败", e);
            throw new LeafException("生成excel临时预览图片失败");
        }

        log.info("成功生成excel临时文件，文件路径：" + templateFilePath);
        log.info("成功生成excel临时预览图片，文件路径：" + imageFilePath);
        reportTemporaryFileMapper.insertOne(new ReportTemporaryFile().setFilePath(templateFilePath).setFileType(0));
        reportTemporaryFileMapper.insertOne(new ReportTemporaryFile().setFilePath(imageFilePath).setFileType(1));

        File imageFile = new File(imageFilePath);
        try (
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ) {
            BufferedImage image = ImageIO.read(fileInputStream);
            if (image != null) {
                ImageIO.write(image, "png", byteArrayOutputStream);
            }
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encodeBuffer(byteArrayOutputStream.toByteArray()).trim();
            base64 = base64.replaceAll("\n", "").replaceAll("\r", "");
            return "data:image/jpg;base64," + base64;
        } catch (IOException e) {
            log.error("生成excel临时预览图片失败", e);
            throw new LeafException("生成excel临时预览图片失败");
        }
    }

    @Override
    public ReportTemplateConfigDTO getDTOById(Long id) {
        ReportTemplateConfig reportTemplateConfig = reportTemplateConfigMapper.selectById(id);
        if (reportTemplateConfig != null) {
            ReportTemplateConfigDTO reportTemplateConfigDTO = new ReportTemplateConfigDTO();
            reportTemplateConfigDTO.setReportTemplateConfig(reportTemplateConfig);
            List<ReportTemplateSheet> reportTemplateSheets =
                    reportTemplateSheetService.selectByConfigId(reportTemplateConfig.getId());
            List<ReportTemplateSheetDTO> reportTemplateSheetDTOs = new ArrayList<>();
            for (ReportTemplateSheet reportTemplateSheet : reportTemplateSheets) {
                List<ReportTemplateTags> reportTemplateTags =
                        reportTemplateTagsService.selectBySheetId(reportTemplateSheet.getId());
                ReportTemplateSheetDTO reportTemplateSheetDTO = new ReportTemplateSheetDTO();
                reportTemplateSheetDTO.setReportTemplateSheet(reportTemplateSheet);
                reportTemplateSheetDTO.setReportTemplateTagsList(reportTemplateTags);
                reportTemplateSheetDTOs.add(reportTemplateSheetDTO);
            }
            reportTemplateConfigDTO.setReportTemplateSheetDTOs(reportTemplateSheetDTOs);
            return reportTemplateConfigDTO;
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    // TODO 批量删除
    public ApiResult deleteRecord(BaseId record) {
        Long configId = record.getId();
        List<ReportTemplateSheet> reportTemplateSheets
                = reportTemplateSheetService.selectByConfigId(configId);
        for (ReportTemplateSheet reportTemplateSheet : reportTemplateSheets) {
            reportTemplateTagsService.deleteBySheetId(reportTemplateSheet.getId());
        }
        reportTemplateSheetService.deleteByConfigId(configId);
        reportTemplateConfigMapper.deleteById(configId);
        return ApiUtil.success("删除成功");
    }

    /**
     * 生成模板文件
     * @param templateConfigDTO
     * @return
     */
    public String generateTemplate(ReportTemplateConfigDTO templateConfigDTO) {
        try {
            allTargetManagements = targetManagementMapper.selectAllTargetManagement();
            ReportTemplateConfig reportTemplateConfig = templateConfigDTO.getReportTemplateConfig();
            Integer templateType = reportTemplateConfig.getTemplateType();
            String templateName = reportTemplateConfig.getTemplateName();
            if (StringUtils.isBlank(templateName)) {
                templateName = templateConfigDTO.getReportTemplateSheetDTOs().get(0).getReportTemplateSheet().getSheetTitle();
                reportTemplateConfig.setTemplateName(templateName);
            }
            TemplateTypeEnum templateTypeEnum = TemplateTypeEnum.getByCode(templateType);
            String generatedExcelFilePath;
            switch (templateTypeEnum) {
                case WORD:
                    generatedExcelFilePath = generateWordTemplate(templateConfigDTO);
                    break;
                default:
                    generatedExcelFilePath = generateExcelTemplate(templateConfigDTO);
                    break;
            }
            log.debug("生成报表临时模板文件: " + generatedExcelFilePath);
            return generatedExcelFilePath;
        } catch (Exception e) {
            throw new LeafException("根据报表配置生成临时模板文件失败");
        }
    }

    /**
     * 生成word模板
     * @param templateConfigDTO
     * @return
     * @throws Exception
     */
    private String generateWordTemplate(ReportTemplateConfigDTO templateConfigDTO) throws Exception {
        ReportTemplateConfig reportTemplateConfig = templateConfigDTO.getReportTemplateConfig();
        String templateName = reportTemplateConfig.getTemplateName();
        List<ReportTemplateSheetDTO> reportTemplateSheetDTOs = templateConfigDTO.getReportTemplateSheetDTOs();
        reportTemplateSheetDTOs.sort(Comparator.comparing(e -> e.getReportTemplateSheet().getSequence()));
        WordTitleConfigDTO wordTitleConfigDTO = WordTitleConfigDTO.getDefaultWordTitleConfigDTO();
        XWPFDocument document = new XWPFDocument();
        // 文档标题
        ExportWordUtil.createParagraph(document, templateName, wordTitleConfigDTO);
        // 时间行
        wordTitleConfigDTO.setFontSize(16).setParagraphAlignment(ParagraphAlignment.LEFT);
        ExportWordUtil.createParagraph(document, "时间：{{current_date}}", wordTitleConfigDTO);
        // TODO 页眉未生效
        /**
            CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
            XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);
            CTP ctpHeader = CTP.Factory.newInstance();
            CTR ctrHeader = ctpHeader.addNewR();
            CTText ctHeader = ctrHeader.addNewT();
            String headerText = "ctpHeader";
            ctHeader.setStringValue(headerText);
            XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
            headerParagraph.setAlignment(ParagraphAlignment.RIGHT);
            XWPFParagraph[] parsHeader = new XWPFParagraph[1];
            parsHeader[0] = headerParagraph;
            policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);
        **/
        int sheetIndex = 1;
        for (ReportTemplateSheetDTO reportTemplateSheetDTO : reportTemplateSheetDTOs) {
            ReportTemplateSheet reportTemplateSheet = reportTemplateSheetDTO.getReportTemplateSheet();
            List<ReportTemplateTags> reportTemplateTagsList = reportTemplateSheetDTO.getReportTemplateTagsList();
            reportTemplateTagsList.sort(Comparator.comparing(ReportTemplateTags::getSequence));
            // 创建每个段落
            // TODO 行间距
            String sheetTitle = reportTemplateSheet.getSheetTitle();
            wordTitleConfigDTO.setFontSize(16).setIsBold(true);
            ExportWordUtil.createParagraph(document, sheetTitle, wordTitleConfigDTO);
            wordTitleConfigDTO.setFontSize(12).setIsBold(false);
            // 判断是纯文本还是图片
            WordTypeEnum wordTypeEnum =
                    WordTypeEnum.getByCode(reportTemplateSheet.getWordType());
            switch (wordTypeEnum) {
                case LINE_CHART:
                    List<List<ReportTemplateTags>> listGroups = ListUtils.partition(reportTemplateTagsList, 2);
                    for (int i = 1; i <= listGroups.size(); i++) {
                        ExportWordUtil.createParagraph(document, String.format("{{sheet%s_chart%s}}", sheetIndex, i), wordTitleConfigDTO);
                    }
                    break;
                case BAR_CHART:
                    ExportWordUtil.createParagraph(document, String.format("{{sheet%s_chart%s}}", sheetIndex, 1), wordTitleConfigDTO);
                    break;
                default:
                    StringJoiner joiner = new StringJoiner("、", "今日", "。");
                    int tagIndex = 1;
                    for (ReportTemplateTags reportTemplateTags : reportTemplateTagsList) {
                        Long targetId = reportTemplateTags.getTargetId();
                        TargetManagement targetManagement = allTargetManagements.get(targetId);
                        StringBuilder singleTagText = new StringBuilder();
                        StringBuilder singleTargetText = singleTagText.append(targetManagement.getWrittenName())
                                .append(String.format("{{sheet%s_tag%s}}", sheetIndex, tagIndex))
                                .append(targetManagement.getUnit())
                                .append("、").append("较昨日")
                                .append(String.format("{{sheet%s_compare%s}}", sheetIndex, tagIndex))
                                .append(String.format("{{sheet%s_difference%s}}", sheetIndex, tagIndex))
                                .append(targetManagement.getUnit());
                        joiner.add(singleTargetText);
                        tagIndex++;
                    }
                    ExportWordUtil.createParagraph(document, joiner.toString(), wordTitleConfigDTO);
                    break;
            }
            sheetIndex++;
        }

        String tempPath = jobProperties.getTempPath();
        String wordFileName = new StringBuilder()
                .append(tempPath)
                .append(File.separator)
                .append(templateName)
                .append("_")
                .append(System.currentTimeMillis())
                .append(TemplateTypeEnum.WORD.getEndSuffix())
                .toString();
        File localFile = new File(wordFileName);
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(wordFileName);
        document.write(fos);
        fos.close();

        return wordFileName;
    }


    /**
     * 生成excel模板
     * @param templateConfigDTO
     * @return
     * @throws Exception
     */
    private String generateExcelTemplate(ReportTemplateConfigDTO templateConfigDTO) throws Exception {
        ReportTemplateConfig reportTemplateConfig = templateConfigDTO.getReportTemplateConfig();
        String templateName = reportTemplateConfig.getTemplateName();
        Workbook workbook = new XSSFWorkbook();
        List<ReportTemplateSheetDTO> reportTemplateSheetDTOs = templateConfigDTO.getReportTemplateSheetDTOs();
        reportTemplateSheetDTOs.sort(Comparator.comparing(e -> e.getReportTemplateSheet().getSequence()));
        for (ReportTemplateSheetDTO reportTemplateSheetDTO : reportTemplateSheetDTOs) {
            ReportTemplateSheet reportTemplateSheet = reportTemplateSheetDTO.getReportTemplateSheet();
            List<ReportTemplateTags> reportTemplateTagsList = reportTemplateSheetDTO.getReportTemplateTagsList();
            //创建每个report sheet
            String sheetTitle = reportTemplateSheet.getSheetTitle();
            String tagsSheetName = ReportConstants.TAG_SHEET_NAME_PREFIX + sheetTitle;
            LinkedHashMap<Object, List<ReportTemplateTags>> topTypeToTagsMap = getTopTypeToTagsMap(reportTemplateTagsList);
            // 原始代码
            List<Long> targetIds = reportTemplateTagsList.stream().map(ReportTemplateTags::getTargetId).collect(Collectors.toList());
            // 通过配置获取所有targetmanagement
            Collection<TargetManagement> targetManagements = targetManagementService.listByIds(targetIds);
            // 构建target map。
            LinkedHashMap<ReportTemplateTags, TargetManagement> tagsMap = new LinkedHashMap<ReportTemplateTags, TargetManagement>();
            for (int i = 0; i < reportTemplateTagsList.size(); i++) {
                ReportTemplateTags reportTemplateTags = reportTemplateTagsList.get(i);
                TargetManagement targetManagement = targetManagements.stream().filter(target -> target.getId().equals(reportTemplateTags.getTargetId())).collect(Collectors.toList()).get(0);
                tagsMap.put(reportTemplateTags, targetManagement);
            }
            createReportSheet(workbook, reportTemplateSheetDTO, tagsMap, topTypeToTagsMap, tagsSheetName);
            // 创建tags sheet并且填充点位信息
            createTagsSheet(workbook, tagsMap, tagsSheetName);
        }

        // 创建dictionary sheet并填充版本信息
        createDictionarySheet(workbook, reportTemplateConfig.getSequenceCode());

        String tempPath = jobProperties.getTempPath();
        String excelFileName = new StringBuilder()
                .append(tempPath)
                .append(File.separator)
                .append(templateName)
                .append("_")
                .append(System.currentTimeMillis())
                .append(TemplateTypeEnum.EXCEL.getEndSuffix())
                .toString();
        File localFile = new File(excelFileName);
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(excelFileName);
        workbook.write(fos);
        fos.close();

        return excelFileName;
    }

    /**
     * key为顶层节点,value为底层节点list
     * @param reportTemplateTagsList
     * @return
     */
    public LinkedHashMap<Object, List<ReportTemplateTags>> getTopTypeToTagsMap(List<ReportTemplateTags> reportTemplateTagsList) {
        //通过templateConfig获取所有配置项。
        if (CollectionUtils.isNotEmpty(reportTemplateTagsList)) {
            // 过滤topParentId为空的tag，然后根据topParentId进行分组
            Map<Long, List<ReportTemplateTags>> topParentIdToReportTemplateTags =
                    reportTemplateTagsList.stream().filter(e -> Objects.nonNull(e.getTopParentId()))
                            .collect(Collectors.groupingBy(ReportTemplateTags::getTopParentId));
            // 顶级分类和底层tag点的map
            LinkedHashMap<Object, List<ReportTemplateTags>> topTypeToTagsMap = new LinkedHashMap<>();
            for (ReportTemplateTags reportTemplateTags : reportTemplateTagsList) {
                Long topParentId = reportTemplateTags.getTopParentId();
                if (Objects.isNull(topParentId)) {
                    List<ReportTemplateTags> tagList = new ArrayList<>();
                    tagList.add(reportTemplateTags);
                    // 没有顶层分类，则key设置为本身
                    topTypeToTagsMap.put(reportTemplateTags, tagList);
                } else {
                    if (!topTypeToTagsMap.containsKey(topParentId)) {
                        topTypeToTagsMap.put(topParentId, topParentIdToReportTemplateTags.get(topParentId));
                    }
                }
            }
            return topTypeToTagsMap;

        }
        return null;
    }

    /**
     * 创建主reportSheet
     * @param workbook
     * @param templateSheetDTO
     * @param tagsMap
     * @param topTypeToTagsMap
     * @param tagsSheetName
     */
    private void createReportSheet(Workbook workbook, ReportTemplateSheetDTO templateSheetDTO,
                                   LinkedHashMap<ReportTemplateTags, TargetManagement> tagsMap,
                                   LinkedHashMap<Object, List<ReportTemplateTags>> topTypeToTagsMap,
                                   String tagsSheetName) {
        List<ReportTemplateTags> reportTemplateTagsList = templateSheetDTO.getReportTemplateTagsList();
        ReportTemplateSheet reportTemplateSheet = templateSheetDTO.getReportTemplateSheet();
        // 获取最大层级
        Integer maxHierarchy = reportTemplateTagsList.stream().map(e -> TargetManagementUtil.getHierarchyBetweenTag(allTargetManagements, e.getTargetId(), e.getTopParentId()))
                .max(Integer::compareTo).orElse(0);
        int tagsMapSize = tagsMap.keySet().size();
        // 创建reportSheet
        Sheet reportSheet = workbook.createSheet(reportTemplateSheet.getSheetTitle());
        // 设置标题及样式
        Row secondTitleRow = ExcelWriterUtil.getRowOrCreate(reportSheet, REPORT_TITLE_ROW_INDEX);
        PoiCustomUtil.addMergedRegion(reportSheet, 1, 1, 1, tagsMapSize + 1);
        for (int j = 1; j <= tagsMapSize + 1; j++) {
            Cell cell = ExcelWriterUtil.getCellOrCreate(secondTitleRow, j);
            cell.setCellStyle(ExcelStyleUtil.getHeaderTitleStyle(workbook));
        }
        Cell titleCell = ExcelWriterUtil.getCellOrCreate(secondTitleRow, 1);
        titleCell.setCellValue(reportTemplateSheet.getSheetTitle());
        secondTitleRow.setHeightInPoints(heightInPointsTitle);//设置行高
        CellStyle headerTitleStyle = ExcelStyleUtil.getHeaderTitleStyle(workbook);
        titleCell.setCellStyle(headerTitleStyle);

        int lastRowOfTagNames = REPORT_TITLE_ROW_INDEX + maxHierarchy; // 子节点tag点行号
        Row lastTagsNameRow = ExcelWriterUtil.getRowOrCreate(reportSheet, lastRowOfTagNames);
        // 项目
        Row tagsNameRow = ExcelWriterUtil.getRowOrCreate(reportSheet, TARGET_NAME_BEGIN_ROW);
        PoiMergeCellUtil.addMergedRegion(reportSheet, TARGET_NAME_BEGIN_ROW, lastRowOfTagNames,1, 1);
        // 设置项目单元格的样式
        for (int i = TARGET_NAME_BEGIN_ROW; i <= lastRowOfTagNames; i++) {
            Row itemRow = ExcelWriterUtil.getRowOrCreate(reportSheet, i);
            Cell itemCell = ExcelWriterUtil.getCellOrCreate(itemRow, firstDataColumnIndex - 1);
            itemCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
        }
        Cell tagsNameRowFirstCell = ExcelWriterUtil.getCellOrCreate(tagsNameRow, firstDataColumnIndex - 1);
        tagsNameRowFirstCell.setCellValue("项目");
        tagsNameRow.setHeightInPoints(heightInPointsHeader);
        // 时间
        Row unitRow = ExcelWriterUtil.getRowOrCreate(reportSheet, lastRowOfTagNames + 1);
        Cell unitRowFirstCell = ExcelWriterUtil.getCellOrCreate(unitRow, firstDataColumnIndex - 1);
        unitRowFirstCell.setCellValue("时间");
        unitRowFirstCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
        unitRow.setHeightInPoints(heightInPointsHeader);
        reportSheet.setColumnWidth(firstDataColumnIndex - 1, timeCellWidth);

        // 填充表头,添加具体的tags name和unit
        int topParentColumnIndex = firstDataColumnIndex;
        for (Map.Entry<Object, List<ReportTemplateTags>> entry : topTypeToTagsMap.entrySet()) {
            Object key = entry.getKey();
            // 当前topParent下的子节点
            List<ReportTemplateTags> tagsList = entry.getValue();
            // 没有topParent的点
            if (key instanceof ReportTemplateTags) {
                // 上下合并空行
                PoiMergeCellUtil.addMergedRegion(reportSheet, TARGET_NAME_BEGIN_ROW, lastRowOfTagNames, topParentColumnIndex, topParentColumnIndex);
                for (int i = TARGET_NAME_BEGIN_ROW; i <= lastRowOfTagNames; i++) {
                    Row itemRow = ExcelWriterUtil.getRowOrCreate(reportSheet, i);
                    Cell itemCell = ExcelWriterUtil.getCellOrCreate(itemRow, topParentColumnIndex);
                    itemCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                }
                Cell tagCell = ExcelWriterUtil.getCellOrCreate(tagsNameRow, topParentColumnIndex);
                ReportTemplateTags reportTemplateTags = tagsList.get(0);
                TargetManagement targetManagement = allTargetManagements.get(reportTemplateTags.getTargetId());
                tagCell.setCellValue(targetManagement.getWrittenName());
                // 写入单位
                Cell unitCell = ExcelWriterUtil.getCellOrCreate(unitRow, topParentColumnIndex);
                unitCell.setCellValue(tagsMap.get(reportTemplateTags).getUnit());
                unitCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                topParentColumnIndex++;
            } else {
                // 多级表头
                Long topParentId = Long.valueOf(String.valueOf(key));
                int sonTagSizeOfCurrentTopParent = tagsList.size();
                // 横向合并一级表头
                PoiMergeCellUtil.addMergedRegion(reportSheet, TARGET_NAME_BEGIN_ROW, TARGET_NAME_BEGIN_ROW, topParentColumnIndex, topParentColumnIndex + sonTagSizeOfCurrentTopParent - 1);
                Cell topParentCell = ExcelWriterUtil.getCellOrCreate(tagsNameRow, topParentColumnIndex);
                for (int i = topParentColumnIndex; i <= topParentColumnIndex + sonTagSizeOfCurrentTopParent - 1; i++) {
                    Row itemRow = ExcelWriterUtil.getRowOrCreate(reportSheet, TARGET_NAME_BEGIN_ROW);
                    Cell itemCell = ExcelWriterUtil.getCellOrCreate(itemRow, i);
                    itemCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                }
                topParentCell.setCellValue(allTargetManagements.get(topParentId).getWrittenName());

                for (int tagIndex = 0; tagIndex < sonTagSizeOfCurrentTopParent; tagIndex++) {
                    ReportTemplateTags reportTemplateTags = tagsList.get(tagIndex);
                    Long targetId = reportTemplateTags.getTargetId();

                    int eachSonTagColumn = tagIndex + topParentColumnIndex; // 子节点的 column index
                    Integer hierarchyBetweenTag = TargetManagementUtil.getHierarchyBetweenTag(allTargetManagements, targetId, topParentId); // 子节点到topParent的层级
                    int numbersNeedToMerge = maxHierarchy - hierarchyBetweenTag; //  1代表需要上下合并两个单元格, 依次类推

                    // 大于2代表有顶节点和子节点之间有中间的层级, 写入中间层级分类
                    if (hierarchyBetweenTag > 2) {
                        TargetManagement targetManagement = allTargetManagements.get(targetId);
                        TargetManagement parentTargetManagement = targetManagement;
                        for (Integer i = 0; i < hierarchyBetweenTag - 2; i++) {
                            parentTargetManagement = allTargetManagements.get(parentTargetManagement.getParentId());
                            Row tagsRow = ExcelWriterUtil.getRowOrCreate(reportSheet, lastRowOfTagNames - i - 1 - numbersNeedToMerge);
                            Cell tagCell = ExcelWriterUtil.getCellOrCreate(tagsRow, eachSonTagColumn);
                            tagCell.setCellValue(parentTargetManagement.getWrittenName());
                            tagCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                        }
                    }

                    Cell lastTagCell;
                    if (numbersNeedToMerge > 0) {
                        // 上下合并空余行数和子节点单元格
                        PoiMergeCellUtil.addMergedRegion(reportSheet, lastRowOfTagNames - numbersNeedToMerge, lastRowOfTagNames, eachSonTagColumn, eachSonTagColumn);
                        for (int i = lastRowOfTagNames - numbersNeedToMerge; i <= lastRowOfTagNames; i++) {
                            Row itemRow = ExcelWriterUtil.getRowOrCreate(reportSheet, i);
                            Cell itemCell = ExcelWriterUtil.getCellOrCreate(itemRow, eachSonTagColumn);
                            itemCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                        }
                        Row tagOfMergeBeginRow = ExcelWriterUtil.getRowOrCreate(reportSheet, lastRowOfTagNames - numbersNeedToMerge);
                        lastTagCell = ExcelWriterUtil.getCellOrCreate(tagOfMergeBeginRow, eachSonTagColumn);
                    } else {
                        // 写入最后子节点数据
                        lastTagCell = ExcelWriterUtil.getCellOrCreate(lastTagsNameRow, eachSonTagColumn);
                        lastTagCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                    }
                    lastTagCell.setCellValue(allTargetManagements.get(targetId).getWrittenName());
                    // 写入单位
                    Cell unitCell = ExcelWriterUtil.getCellOrCreate(unitRow, eachSonTagColumn);
                    unitCell.setCellValue(tagsMap.get(reportTemplateTags).getUnit());
                    unitCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                }

                // 横向合并连续且相同名称的单元格
                for (int rowIndex = TARGET_NAME_BEGIN_ROW + 1; rowIndex < lastRowOfTagNames; rowIndex++) {
                    List<Cell> cellList = new ArrayList<>();
                    Row rowToMergeSameCell = ExcelWriterUtil.getRowOrCreate(reportSheet, rowIndex);
                    for (int tagIndex = 0; tagIndex < sonTagSizeOfCurrentTopParent; tagIndex++) {
                        int eachSonTagColumn = tagIndex + topParentColumnIndex;
                        Cell cell = ExcelWriterUtil.getCellOrCreate(rowToMergeSameCell, eachSonTagColumn);
                        cellList.add(cell);
                    }
                    List<Cell> sameCells = new ArrayList<>(); //存放相同的数据项
                    List<Integer> sameTimes = new ArrayList<Integer>(); //存放重复的次数
                    Cell tempCell = cellList.get(0);
                    sameCells.add(tempCell);
                    int count = 0;
                    for (int i = 0; i < cellList.size(); i++) {
                        if (tempCell.getStringCellValue().equals(cellList.get(i).getStringCellValue())) {
                            count++;
                        } else {
                            sameCells.add(cellList.get(i));
                            sameTimes.add(count); // 上一轮相同的个数
                            tempCell = cellList.get(i);
                            count = 1;
                        }
                        if (i == cellList.size() - 1) {
                            sameTimes.add(count);
                        }
                    }
                    for (int i = 0; i < sameCells.size(); i++) {
                        Cell cell = sameCells.get(i);
                        Integer cellSameTime = sameTimes.get(i);
                        int sameCellRowIndex = cell.getRowIndex();
                        int sameCellColumnIndex = cell.getColumnIndex();
                        if (cellSameTime > 1) {
                            PoiMergeCellUtil.addMergedRegion(reportSheet, sameCellRowIndex, sameCellRowIndex, sameCellColumnIndex, sameCellColumnIndex + cellSameTime - 1);
                        }
                    }
                }

                // 下一个topParentIndex
                topParentColumnIndex += sonTagSizeOfCurrentTopParent;
            }
        }
        // 写入时间列数据 写入tag点引用公式 平均值公式
        setTimeAndFormula(reportSheet, workbook, reportTemplateSheet, tagsMap, tagsSheetName, lastRowOfTagNames);
    }

    private void setTimeAndFormula(Sheet firstSheet, Workbook workbook, ReportTemplateSheet reportTemplateSheet, LinkedHashMap<ReportTemplateTags, TargetManagement> tagsMap,
                                   String tagsSheetName, int lastRowOfTagNames) {
        // 生成时间
        // 获取时间范围类型
        String timeUnit = ":00";
        int interval = Integer.valueOf(reportTemplateSheet.getTimeslotInterval()); // 时间间隔
        Integer startTimeSlot = null, endTimeSlot, maxRow;
        TimeDivideEnum timeDivideEnum = TimeDivideEnum.getEnumByCode(reportTemplateSheet.getTimeDivideType());
        TimeTypeEnum timeTypeEnum = TimeTypeEnum.getEnumByCode(reportTemplateSheet.getTimeType());
        if (timeTypeEnum.equals(TimeTypeEnum.TIME_RANGE)) {
            startTimeSlot = Integer.valueOf(reportTemplateSheet.getStartTimeslot());
            endTimeSlot = Integer.valueOf(reportTemplateSheet.getEndTimeslot());
            maxRow = (endTimeSlot - startTimeSlot) / interval + 1;
            switch (timeDivideEnum) {
                case HOUR:
                    if (startTimeSlot >= endTimeSlot) {
                        //处理焦化报表时间跨天的情况
                        maxRow = (endTimeSlot + (24 - startTimeSlot)) / interval + 1;
                    }
                    break;
                case DAY:
                    if (startTimeSlot >= endTimeSlot) {
                        // 处理焦化报表时间跨天的情况
                        maxRow = (endTimeSlot + (31 - startTimeSlot)) / interval + 1;
                    }
                    timeUnit = timeDivideEnum.getDivideType();
                    break;
                default:
                    // 月
                    if (startTimeSlot >= endTimeSlot) {
                        // 处理焦化报表时间跨天的情况
                        maxRow = (endTimeSlot + (12 - startTimeSlot)) / interval + 1;
                    }
                    timeUnit = timeDivideEnum.getDivideType();;
                    break;
            }
        } else {
            maxRow = Integer.valueOf(reportTemplateSheet.getLastTimeslot()) / interval;
        }

        LocalDateTime now = LocalDateTime.now();
        int firstDataRowIndex = lastRowOfTagNames + 2;

        DateTimeFormatter df = null;
        LocalDateTime startHours = now.minusHours((maxRow - 1) * interval);
        LocalDateTime startDays = now.minusDays((maxRow - 1) * interval);
        LocalDateTime startMonths = now.minusMonths((maxRow - 1) * interval);
        DecimalFormat decimalFormat = new DecimalFormat();
        for (int i = 0; i < maxRow; i++) {
            Row dataRow = ExcelWriterUtil.getRowOrCreate(firstSheet, firstDataRowIndex + i);
            Cell timeCell = ExcelWriterUtil.getCellOrCreate(dataRow, firstDataColumnIndex - 1);
            //添加时间信息
            // 构造时间信息
            String timeStr = "";
            int timeSlot = 0;
            if (timeTypeEnum.equals(TimeTypeEnum.RECENT_TIME)) {
                switch (timeDivideEnum) {
                    case HOUR:
                        df = DateTimeFormatter.ofPattern(DateUtil.yyyyMMddHHChineseFormat);
                        timeStr = startHours.plusHours(i * interval).format(df);
                        break;
                    case DAY:
                        df = DateTimeFormatter.ofPattern(DateUtil.yyyyMMddChineseFormat);
                        timeStr = startDays.plusDays(i * interval).format(df);
                        break;
                    default:
                        df = DateTimeFormatter.ofPattern(DateUtil.yyyyMM);
                        timeStr = startMonths.plusMonths(i * interval).format(df);
                        break;
                }
            } else {
                decimalFormat.applyPattern("00");
                timeSlot = i * interval + startTimeSlot;
                timeStr = decimalFormat.format(timeSlot) + timeUnit;
                // 处理时间跨天、跨月、跨年的情况
                switch (timeDivideEnum) {
                    case HOUR:
                        if (timeSlot > 24) {
                            timeStr = decimalFormat.format(timeSlot - 24) + timeUnit;
                        }
                        break;
                    // 日期跨月的情况
                    case DAY:
                        if (timeSlot > 31) {
                            timeStr = decimalFormat.format(timeSlot - 31) + timeUnit;
                        }
                        break;
                    // 月份跨年的情况
                    case MONTH:
                        if (timeSlot > 12) {
                            timeStr = decimalFormat.format(timeSlot - 12) + timeUnit;
                        }
                        break;
                }
            }

            timeCell.setCellValue(timeStr);
            timeCell.setCellStyle(ExcelStyleUtil.getCellStyle(workbook));//设置样式
            // 循环点位列表
            int j = 0;
            for (ReportTemplateTags reportTemplateTags : tagsMap.keySet()) {
                String columnLetter = letterArray[j];
                Cell cell = ExcelWriterUtil.getCellOrCreate(dataRow, firstDataColumnIndex + j);
                cell.setCellFormula(formula.replaceAll("cell%", "'" + tagsSheetName + "'!" + columnLetter + (i + 2)));
                cell.setCellType(CellType.FORMULA);
                // 设置单元格样式及小数点位
                Integer decimalScale = reportTemplateTags.getDecimalScale();
                int scale = decimalScale != null ? decimalScale : defaultScale;
                CellStyle cellStyle = ExcelStyleUtil.getCellStyle(workbook, scale);
                //设置样式
                cell.setCellStyle(cellStyle);

                j++;
            }
        }

        //添加汇总值到最后
        if ("1".equals(reportTemplateSheet.getIsAddAvg())) {
            Row summaryRow = ExcelWriterUtil.getRowOrCreate(firstSheet, firstSheet.getLastRowNum() + 1);
            Cell averageCell = ExcelWriterUtil.getCellOrCreate(summaryRow, 1);
            averageCell.setCellValue("平均值");
            averageCell.setCellStyle(ExcelStyleUtil.getCellStyle(workbook));

            int k = 0;
            for (ReportTemplateTags reportTemplateTags : tagsMap.keySet()) {
                Cell cell = ExcelWriterUtil.getCellOrCreate(summaryRow, firstDataColumnIndex + k);
                String columnLetter = letterArray[firstDataColumnIndex + k];
                String avgBegin = columnLetter + (firstDataRowIndex + 1);
                String avgEnd = columnLetter + (firstDataRowIndex + maxRow);
                String formula = String.format(AVERAGE_FORMULA, avgBegin, avgEnd);
                cell.setCellFormula(formula);
                cell.setCellType(CellType.FORMULA);
                // 设置平均值单元格样式和小数点位
                Integer decimalScale = reportTemplateTags.getDecimalScale();
                int scale = decimalScale != null ? decimalScale : defaultScale;
                CellStyle cellStyle = ExcelStyleUtil.getCellStyle(workbook, scale);
                cell.setCellStyle(cellStyle);
                k++;
            }
        }

        // 循环设置所有的行高和列宽, 包含平均值列
        for (int i = 0; i <= maxRow + 1; i++) {
            Row dataRow = ExcelWriterUtil.getRowOrCreate(firstSheet, firstDataRowIndex + i);
            dataRow.setHeightInPoints(heightInPoints);
        }
        int j = 0;
        for (ReportTemplateTags reportTemplateTags : tagsMap.keySet()) {
            firstSheet.setColumnWidth(firstDataColumnIndex + j, cellWidth);
            j++;
        }
    }

    /**
     * 创建tags sheet并且填充点位信息(填充report_template_tags的id)
     * @param workbook
     * @param tagsMap
     * @param tagsSheetName
     */
    private void createTagsSheet(Workbook workbook, LinkedHashMap<ReportTemplateTags, TargetManagement> tagsMap, String tagsSheetName) {
        Sheet secondSheet = workbook.createSheet(tagsSheetName);
        List<CellData> cellDataList = new ArrayList<CellData>();

        int i = 0;
        for (ReportTemplateTags reportTemplateTags : tagsMap.keySet()) {
            String targetName = tagsMap.get(reportTemplateTags).getTargetName();
            ExcelWriterUtil.addCellData(cellDataList, 0, i, targetName != null ? targetName : "");
            i++;
        }

        SheetRowCellData.builder()
                .cellDataList(cellDataList)
                .sheet(secondSheet)
                .workbook(workbook)
                .build().allValueWriteExcel();
    }

    /**
     * 创建“dictionary”sheet并填充版本信息
     * @param workbook
     * @param sequenceCode
     */
    private void createDictionarySheet(Workbook workbook, String sequenceCode) {
        Sheet dictionarySheet = workbook.createSheet(ReportConstants.DICTIONARY_SHEET_NAME);
        Row dictionarySheetFirstRow = ExcelWriterUtil.getRowOrCreate(dictionarySheet,0);
        Cell dictionarySheetFirstRowCell1 = ExcelWriterUtil.getCellOrCreate(dictionarySheetFirstRow, 0);
        dictionarySheetFirstRowCell1.setCellValue(ReportConstants.VERSION);
        Cell dictionarySheetFirstRowCell2 = ExcelWriterUtil.getCellOrCreate(dictionarySheetFirstRow, 1);
        dictionarySheetFirstRowCell2.setCellValue(SequenceEnum.getVersion(sequenceCode));
    }

}
