package com.cisdi.steel.module.report.service.impl;

import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.report.dto.ReportTemplateConfigDTO;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.enums.SequenceEnum;
import com.cisdi.steel.module.report.mapper.ReportTemplateConfigMapper;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import com.cisdi.steel.module.report.service.ReportTemplateConfigService;
import com.cisdi.steel.module.report.service.ReportTemplateTagsService;
import com.cisdi.steel.module.report.service.TargetManagementService;
import com.cisdi.steel.module.report.util.ExcelStyleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.java2d.pipe.Region;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 报表动态模板配置 服务实现类 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@Service
@Slf4j
public class ReportTemplateConfigServiceImpl extends BaseServiceImpl<ReportTemplateConfigMapper, ReportTemplateConfig> implements ReportTemplateConfigService {

    //点位数据占位公式
    private static final String formula = "IF(cell%=\"\",\"\",cell%)";
    private static final String[] letterArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static final String firstSheetName = "报表";
    public static final int REPORT_TITLE_ROW_INDEX = 1; // 标题行
    public static final int TARGET_NAME_BEGIN_ROW = 2; // 顶层节点行
    private static final int firstDataRowIndex = 4;//从开始填充点位的行开始，下标从0开始
    private static final int firstDataColumnIndex = 2;//从开始填充点位的列开始，下标从0开始，并且排除时间列
    private static final int heightInPoints = 18;//普通行高度
    private static final int heightInPointsHeader = 25;//参数表头行高度
    private static final int heightInPointsTitle = 45;//大标题行高度
    private static final int cellWidth = 14 * 256;//普通列宽度
    private static final String avarageFormula = "IFERROR(AVERAGE(%s:%s), \"\")";
    //默认小数点位
    private static final int defaultScale = 2;
    private Map<Long, TargetManagement> allTargetManagements;
    // 根节点的parentId
    public static final Long TOP_PARENT_ID = 0L;

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private ReportTemplateTagsService reportTemplateTagsService;

    @Autowired
    private TargetManagementService targetManagementService;

    @Autowired
    private ReportTemplateConfigMapper reportTemplateConfigMapper;

    @Autowired
    private TargetManagementMapper targetManagementMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateDTO(ReportTemplateConfigDTO templateConfigDTO) {

        //生成临时模板文件。
        ReportTemplateConfig reportTemplateConfig = templateConfigDTO.getReportTemplateConfig();
        if (reportTemplateConfig.getId() != null && reportTemplateConfig.getId() > 0){
            this.updateRecord(reportTemplateConfig);
        } else {
            this.insertRecord(reportTemplateConfig);
        }

        long configId = reportTemplateConfig.getId();
        List<ReportTemplateTags> reportTemplateTags = templateConfigDTO.getReportTemplateTags();
        //清空参数列表后再插入
        reportTemplateTagsService.deleteByConfigId(configId);
        reportTemplateTags.stream().forEach(tag -> {
            tag.setTemplateConfigId(configId);
            reportTemplateTagsService.insertRecord(tag);
        });
        log.info("保存模板配置成功，ID: " + reportTemplateConfig.getId());

        //生成临时模板文件。
        String templateFilePath = this.generateTemplate(templateConfigDTO);
        log.info("成功生成模板文件，文件路径：" + templateFilePath);

        //修改templatePath
        reportTemplateConfig.setTemplatePath(templateFilePath);
        this.updateRecord(reportTemplateConfig);

        return true;
    }

    @Override
    public ReportTemplateConfigDTO getDTOById(Long id) {
        ReportTemplateConfig reportTemplateConfig = reportTemplateConfigMapper.selectById(id);
        if (reportTemplateConfig != null) {
            ReportTemplateConfigDTO configDTO = new ReportTemplateConfigDTO();
            configDTO.setReportTemplateConfig(reportTemplateConfig);

            //查询tags
            List<ReportTemplateTags> reportTemplateTags = reportTemplateTagsService.selectByConfigId(reportTemplateConfig.getId());
            configDTO.setReportTemplateTags(reportTemplateTags);

            return configDTO;
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult deleteRecord(BaseId record) {
        reportTemplateTagsService.deleteByConfigId(record.getId());
        reportTemplateConfigMapper.deleteById(record.getId());

        return ApiUtil.success("删除成功");
    }

    public String generateTemplate(ReportTemplateConfigDTO templateConfigDTO) {
        //通过templateConfig获取所有配置项。
        try {
            List<ReportTemplateTags> reportTemplateTagsList = templateConfigDTO.getReportTemplateTags();
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

                allTargetManagements = targetManagementMapper.selectAllTargetManagement();

                // 原始代码
                List<Long> targetIds = reportTemplateTagsList.stream().map(ReportTemplateTags::getTargetId).collect(Collectors.toList());
                //通过配置获取所有tag management.
                Collection<TargetManagement> targetManagements = targetManagementService.listByIds(targetIds);

                //构建target map。
                LinkedHashMap<ReportTemplateTags, TargetManagement> tagsMap = new LinkedHashMap<ReportTemplateTags, TargetManagement>();
                for (int i = 0; i < reportTemplateTagsList.size(); i++) {
                    ReportTemplateTags reportTemplateTags = reportTemplateTagsList.get(i);
                    TargetManagement targetManagement = targetManagements.stream().filter(target -> target.getId().equals(reportTemplateTags.getTargetId())).collect(Collectors.toList()).get(0);
                    tagsMap.put(reportTemplateTags, targetManagement);
                }

                String generatedExcelFilePath = generateReportTemplateExcel(templateConfigDTO, tagsMap, topTypeToTagsMap);
                log.debug("生成报表临时模板文件: " + generatedExcelFilePath);
                return generatedExcelFilePath;
            }
        } catch (Exception e) {
            log.error("根据报表配置生成模板文件失败", e);
        }

        return null;
    }

    /**
     * 获取子节点和父节点之间的层级数
     * @param targetId
     * @param parentId
     * @return
     */
    private Integer getHierarchyBetweenTag(Long targetId, Long parentId) {
        Integer number = 1;
        // 没有父节点 返回一层
        if (Objects.isNull(parentId)) {
            return number;
        }

        // 同一个点 返回一层
        if (targetId == parentId) {
            return number;
        }

        Long tmpTargetId = targetId;
        TargetManagement targetManagement = allTargetManagements.get(tmpTargetId);
        while (targetManagement.getParentId() != TOP_PARENT_ID) {
            tmpTargetId = targetManagement.getParentId();
            number++;
            if (tmpTargetId.equals(parentId)) {
                return number;
            } else {
                targetManagement = allTargetManagements.get(tmpTargetId);
            }
        }
        // targetId和parentId之间没有父子关系
        return null;
    }

    /**
     * 创建主sheet(第一个sheet)
     * @param workbook
     * @param templateConfigDTO
     * @param tagsMap
     * @param topTypeToTagsMap
     * @param tagsSheetName
     */
    private void createReportMainSheet(Workbook workbook, ReportTemplateConfigDTO templateConfigDTO,
                                       LinkedHashMap<ReportTemplateTags, TargetManagement> tagsMap,
                                       LinkedHashMap<Object, List<ReportTemplateTags>> topTypeToTagsMap,
                                       String tagsSheetName) {
        List<ReportTemplateTags> reportTemplateTagsList = templateConfigDTO.getReportTemplateTags();
        ReportTemplateConfig reportTemplateConfig = templateConfigDTO.getReportTemplateConfig();
        // 获取最大层级
        Integer maxHierarchy = reportTemplateTagsList.stream().map(e -> getHierarchyBetweenTag(e.getTargetId(), e.getTopParentId()))
                .max(Integer::compareTo).orElse(0);
        int tagsMapSize = tagsMap.keySet().size();
        //创建第一个sheet
        Sheet firstSheet = workbook.createSheet(firstSheetName);
        //设置标题及样式
        Row secondTitleRow = ExcelWriterUtil.getRowOrCreate(firstSheet, REPORT_TITLE_ROW_INDEX);
        PoiCustomUtil.addMergedRegion(firstSheet, 1, 1, 1, tagsMapSize + 1);
        for (int j = 1; j <= tagsMapSize + 1; j++) {
            Cell cell = ExcelWriterUtil.getCellOrCreate(secondTitleRow, j);
            cell.setCellStyle(ExcelStyleUtil.getHeaderTitleStyle(workbook));
        }
        Cell titleCell = ExcelWriterUtil.getCellOrCreate(secondTitleRow, 1);
        titleCell.setCellValue(reportTemplateConfig.getTemplateName());
        secondTitleRow.setHeightInPoints(heightInPointsTitle);//设置行高
        CellStyle headerTitleStyle = ExcelStyleUtil.getHeaderTitleStyle(workbook);
        titleCell.setCellStyle(headerTitleStyle);

        int lastRowOfTagNames = REPORT_TITLE_ROW_INDEX + maxHierarchy; // 子节点tag点行号
        Row lastTagsNameRow = ExcelWriterUtil.getRowOrCreate(firstSheet, lastRowOfTagNames);
        // 项目
        Row tagsNameRow = ExcelWriterUtil.getRowOrCreate(firstSheet, TARGET_NAME_BEGIN_ROW);
        PoiMergeCellUtil.addMergedRegion(firstSheet, TARGET_NAME_BEGIN_ROW, lastRowOfTagNames,1, 1);
        Cell tagsNameRowFirstCell = ExcelWriterUtil.getCellOrCreate(tagsNameRow, firstDataColumnIndex - 1);
        tagsNameRowFirstCell.setCellValue("项目");
        tagsNameRowFirstCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
        tagsNameRow.setHeightInPoints(heightInPointsHeader);
        // 时间
        Row unitRow = ExcelWriterUtil.getRowOrCreate(firstSheet, lastRowOfTagNames + 1);
        Cell unitRowFirstCell = ExcelWriterUtil.getCellOrCreate(unitRow, firstDataColumnIndex - 1);
        unitRowFirstCell.setCellValue("时间");
        unitRowFirstCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
        unitRow.setHeightInPoints(heightInPointsHeader);
        firstSheet.setColumnWidth(firstDataColumnIndex - 1, cellWidth);

        // 填充表头,添加具体的tags name和unit
        int topParentColumnIndex = firstDataColumnIndex;
        for (Map.Entry<Object, List<ReportTemplateTags>> entry : topTypeToTagsMap.entrySet()) {
            Object key = entry.getKey();
            // 当前topParent下的子节点
            List<ReportTemplateTags> tagsList = entry.getValue();
            // 没有topParent的点
            if (key instanceof ReportTemplateTags) {
                // 上下合并空行
                PoiMergeCellUtil.addMergedRegion(firstSheet, TARGET_NAME_BEGIN_ROW, lastRowOfTagNames, topParentColumnIndex, topParentColumnIndex);
                Cell tagCell = ExcelWriterUtil.getCellOrCreate(tagsNameRow, topParentColumnIndex);
                ReportTemplateTags reportTemplateTags = tagsList.get(0);
                TargetManagement targetManagement = allTargetManagements.get(reportTemplateTags.getTargetId());
                tagCell.setCellValue(targetManagement.getWrittenName());
                tagCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                topParentColumnIndex++;
            } else {
                // 多级表头
                Long topParentId = Long.valueOf(String.valueOf(key));
                int sonTagSizeOfCurrentTopParent = tagsList.size();
                // 横向合并一级表头
                Cell topParentCell = ExcelWriterUtil.getCellOrCreate(tagsNameRow, topParentColumnIndex);
                topParentCell.setCellValue(allTargetManagements.get(topParentId).getWrittenName());
                topParentCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                PoiMergeCellUtil.addMergedRegion(firstSheet, TARGET_NAME_BEGIN_ROW, TARGET_NAME_BEGIN_ROW, topParentColumnIndex, topParentColumnIndex + sonTagSizeOfCurrentTopParent - 1);

                for (int tagIndex = 0; tagIndex < sonTagSizeOfCurrentTopParent; tagIndex++) {
                    ReportTemplateTags reportTemplateTags = tagsList.get(tagIndex);
                    Long targetId = reportTemplateTags.getTargetId();

                    int eachSonTagColumn = tagIndex + topParentColumnIndex; // 子节点的 column index
                    Integer hierarchyBetweenTag = getHierarchyBetweenTag(targetId, topParentId); // 子节点到topParent的层级
                    int numbersNeedToMerge = maxHierarchy - hierarchyBetweenTag; //  1代表需要上下合并两个单元格, 依次类推

                    // 大于2代表有顶节点和子节点之间有中间的层级, 写入中间层级分类
                    if (hierarchyBetweenTag > 2) {
                        TargetManagement targetManagement = allTargetManagements.get(targetId);
                        TargetManagement parentTargetManagement = targetManagement;
                        for (Integer i = 0; i < hierarchyBetweenTag - 2; i++) {
                            parentTargetManagement = allTargetManagements.get(parentTargetManagement.getParentId());
                            Row tagsRow = ExcelWriterUtil.getRowOrCreate(firstSheet, lastRowOfTagNames - i - 1 - numbersNeedToMerge);
                            Cell tagCell = ExcelWriterUtil.getCellOrCreate(tagsRow, eachSonTagColumn);
                            tagCell.setCellValue(parentTargetManagement.getWrittenName());
                            tagCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                        }
                    }

                    Cell lastTagCell;
                    if (numbersNeedToMerge > 0) {
                        // 上下合并空余行数和子节点单元格
                        PoiMergeCellUtil.addMergedRegion(firstSheet, lastRowOfTagNames - numbersNeedToMerge, lastRowOfTagNames, eachSonTagColumn, eachSonTagColumn);
                        Row tagOfMergeBeginRow = ExcelWriterUtil.getRowOrCreate(firstSheet, lastRowOfTagNames - numbersNeedToMerge);
                        lastTagCell = ExcelWriterUtil.getCellOrCreate(tagOfMergeBeginRow, eachSonTagColumn);
                    } else {
                        // 写入最后子节点数据
                        lastTagCell = ExcelWriterUtil.getCellOrCreate(lastTagsNameRow, eachSonTagColumn);
                    }
                    lastTagCell.setCellValue(allTargetManagements.get(targetId).getWrittenName());
                    lastTagCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                    // 写入单位
                    Cell unitCell = ExcelWriterUtil.getCellOrCreate(unitRow, eachSonTagColumn);
                    unitCell.setCellValue(tagsMap.get(reportTemplateTags).getUnit());
                    unitCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
                }

                // 横向合并连续且相同名称的单元格
                for (int rowIndex = TARGET_NAME_BEGIN_ROW + 1; rowIndex < lastRowOfTagNames; rowIndex++) {
                    List<Cell> cellList = new ArrayList<>();
                    Row rowToMergeSameCell = ExcelWriterUtil.getRowOrCreate(firstSheet, rowIndex);
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
                            PoiMergeCellUtil.addMergedRegion(firstSheet, sameCellRowIndex, sameCellRowIndex, sameCellColumnIndex, sameCellColumnIndex + cellSameTime - 1);
                        }
                    }
                }

                // 下一个topParentIndex
                topParentColumnIndex += sonTagSizeOfCurrentTopParent;
            }
        }

        // 在第一个sheet中填充点位信息
        // 计算填充需填充点位的行数。以及对应的最大行数
        int interval = Integer.valueOf(reportTemplateConfig.getTimeslotInterval());
        Integer startTimeSlot = Integer.valueOf(reportTemplateConfig.getStartTimeslot());
        Integer endTimeSlot = Integer.valueOf(reportTemplateConfig.getEndTimeslot());
        int maxRow = (endTimeSlot - startTimeSlot)/interval;
        if (startTimeSlot >= endTimeSlot) {
            //处理焦化报表时间跨天的情况
            maxRow = (endTimeSlot + (24 - startTimeSlot)) / interval;
        }

        int firstDataRowIndex = lastRowOfTagNames + 2;
        for (int i = 0; i <= maxRow; i++) {
            Row dataRow = ExcelWriterUtil.getRowOrCreate(firstSheet, firstDataRowIndex + i);
            Cell timeCell = ExcelWriterUtil.getCellOrCreate(dataRow, firstDataColumnIndex - 1);
            //添加时间信息
            int timeSlot = i * interval + startTimeSlot;
            String timeStr = timeSlot + ":00";
            //处理焦化报表时间跨天的情况
            if (timeSlot > 24) {
                timeStr = timeSlot - 24 + ":00";
            }
            timeCell.setCellValue(timeStr);
            timeCell.setCellStyle(ExcelStyleUtil.getCellStyle(workbook));//设置样式
            // 循环点位列表
            int j = 0;
            for (ReportTemplateTags reportTemplateTags : tagsMap.keySet()) {
                String columnLetter = letterArray[j];
                Cell cell = ExcelWriterUtil.getCellOrCreate(dataRow, firstDataColumnIndex + j);
                cell.setCellFormula(formula.replaceAll("cell%", tagsSheetName + "!" + columnLetter + (i + 2)));
                cell.setCellType(CellType.FORMULA);
                // 设置单元格样式及小数点位
                TargetManagement targetManagement = tagsMap.get(reportTemplateTags);
                Integer scaleObj = targetManagement.getScale();
                int scale = scaleObj != null ? scaleObj.intValue() : defaultScale;
                CellStyle cellStyle = ExcelStyleUtil.getCellStyle(workbook, scale);
                //设置样式
                cell.setCellStyle(cellStyle);

                j++;
            }
        }

        //添加汇总值到最后
        if ("1".equals(reportTemplateConfig.getIsAddAvg())) {
            Row summaryRow = ExcelWriterUtil.getRowOrCreate(firstSheet, firstSheet.getLastRowNum() + 1);
            Cell averageCell = ExcelWriterUtil.getCellOrCreate(summaryRow, 1);
            averageCell.setCellValue("平均值");
            averageCell.setCellStyle(ExcelStyleUtil.getCellStyle(workbook));

            for (int j = 0; j < tagsMapSize; j++) {
                Cell cell = ExcelWriterUtil.getCellOrCreate(summaryRow, firstDataColumnIndex + j);
                String columnLetter = letterArray[firstDataColumnIndex + j];
                String avgBegin = columnLetter + (firstDataRowIndex + 1);
                String avgEnd = columnLetter + (firstDataRowIndex + maxRow);
                String formula = String.format(avarageFormula, avgBegin, avgEnd);
                cell.setCellFormula(formula);
                cell.setCellType(CellType.FORMULA);
                // 设置平均值单元格样式和小数点位
                CellStyle cellStyle = ExcelStyleUtil.getCellStyle(workbook, defaultScale);
                cell.setCellStyle(cellStyle);
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


    private String generateReportTemplateExcel(ReportTemplateConfigDTO templateConfigDTO, LinkedHashMap<ReportTemplateTags, TargetManagement> tagsMap, LinkedHashMap<Object, List<ReportTemplateTags>> topTypeToTagsMap) throws Exception {
        ReportTemplateConfig reportTemplateConfig = templateConfigDTO.getReportTemplateConfig();
        Workbook workbook = new XSSFWorkbook();
        String tagsSheetName =  "_tags_day_hour" + reportTemplateConfig.getTimeslotInterval();
        //创建报表主sheet。
        // createReportSheet(workbook, reportTemplateConfig, tagsMap, tagsSheetName);
        createReportMainSheet(workbook, templateConfigDTO, tagsMap, topTypeToTagsMap, tagsSheetName);

        // 创建tags sheet并且填充点位信息
        createTagsSheet(workbook, tagsMap, tagsSheetName);

        // 创建“dictionary”sheet并填充版本信息
        createDictionarySheet(workbook, reportTemplateConfig.getSequenceCode());

        String tempPath = jobProperties.getTempPath();
        String excelFileName = new StringBuilder().append(tempPath).append(File.separator)
                .append(reportTemplateConfig.getTemplateName()).append("_").append(System.currentTimeMillis()).append(".xlsx").toString();
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
     * 使用原生poi创建excel
     * @param workbook
     * @param reportTemplateConfig
     * @param tagsMap
     * @param tagsSheetName
     */
    private void createReportSheet(Workbook workbook, ReportTemplateConfig reportTemplateConfig, LinkedHashMap<ReportTemplateTags, TargetManagement> tagsMap, String tagsSheetName) {
        int tagsMapSize = tagsMap.keySet().size();
        //创建第一个sheet
        Sheet firstSheet = workbook.createSheet(firstSheetName);
        //添加空白行
        Row firstBlankRow = ExcelWriterUtil.getRowOrCreate(firstSheet, 0);
        //设置标题及样式
        Row secondTitleRow = ExcelWriterUtil.getRowOrCreate(firstSheet, firstDataRowIndex - 3);
        PoiMergeCellUtil.addMergedRegion(firstSheet, 1,1,1, tagsMapSize + 1);
        for (int j = 1; j <= tagsMapSize + 1; j++) {
            Cell cell = ExcelWriterUtil.getCellOrCreate(secondTitleRow, j);
            cell.setCellStyle(ExcelStyleUtil.getHeaderTitleStyle(workbook));
        }
        Cell titleCell = ExcelWriterUtil.getCellOrCreate(secondTitleRow, 1);
        titleCell.setCellValue(reportTemplateConfig.getTemplateName());
        secondTitleRow.setHeightInPoints(heightInPointsTitle);//设置行高
        CellStyle headerTitleStyle = ExcelStyleUtil.getHeaderTitleStyle(workbook);
        titleCell.setCellStyle(headerTitleStyle);

        Row tagsNameRow = ExcelWriterUtil.getRowOrCreate(firstSheet, firstDataRowIndex - 2);
        Cell tagsNameRowFirstCell = ExcelWriterUtil.getCellOrCreate(tagsNameRow, firstDataColumnIndex - 1);
        tagsNameRowFirstCell.setCellValue("项目");
        tagsNameRowFirstCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
        tagsNameRow.setHeightInPoints(heightInPointsHeader);

        Row unitRow = ExcelWriterUtil.getRowOrCreate(firstSheet, firstDataRowIndex - 1);
        Cell unitRowFirstCell = ExcelWriterUtil.getCellOrCreate(unitRow, firstDataColumnIndex - 1);
        unitRowFirstCell.setCellValue("时间");
        unitRowFirstCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
        unitRow.setHeightInPoints(heightInPointsHeader);
        firstSheet.setColumnWidth(firstDataColumnIndex - 1, cellWidth);

        // 填充表头,添加具体的tags name和unit
        int tagsIndex = firstDataColumnIndex;
        for (ReportTemplateTags reportTemplateTags : tagsMap.keySet()) {
            Cell tagsNameCell = ExcelWriterUtil.getCellOrCreate(tagsNameRow, tagsIndex);
            tagsNameCell.setCellValue(tagsMap.get(reportTemplateTags).getWrittenName());
            tagsNameCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
            Cell unitCell = ExcelWriterUtil.getCellOrCreate(unitRow, tagsIndex);
            unitCell.setCellValue(tagsMap.get(reportTemplateTags).getUnit());
            unitCell.setCellStyle(ExcelStyleUtil.getHeaderStyle(workbook));
            tagsIndex++;
        }

        // 在第一个sheet中填充点位信息
        // 计算填充需填充点位的行数。以及对应的最大行数
        int interval = Integer.valueOf(reportTemplateConfig.getTimeslotInterval());
        Integer startTimeSlot = Integer.valueOf(reportTemplateConfig.getStartTimeslot());
        Integer endTimeSlot = Integer.valueOf(reportTemplateConfig.getEndTimeslot());
        int maxRow = (endTimeSlot - startTimeSlot)/interval;
        if (startTimeSlot >= endTimeSlot) {
            //处理焦化报表时间跨天的情况
            maxRow = (endTimeSlot + (24 - startTimeSlot)) / interval;
        }

        for (int i = 0; i <= maxRow; i++) {
            Row dataRow = ExcelWriterUtil.getRowOrCreate(firstSheet, firstDataRowIndex + i);
            Cell timeCell = ExcelWriterUtil.getCellOrCreate(dataRow, firstDataColumnIndex - 1);
            //添加时间信息
            int timeSlot = i * interval + startTimeSlot;
            String timeStr = timeSlot + ":00";
            //处理焦化报表时间跨天的情况
            if (timeSlot > 24) {
                timeStr = timeSlot - 24 + ":00";
            }
            timeCell.setCellValue(timeStr);
            timeCell.setCellStyle(ExcelStyleUtil.getCellStyle(workbook));//设置样式
            // 循环点位列表
            int j = 0;
            for (ReportTemplateTags reportTemplateTags : tagsMap.keySet()) {
                String columnLetter = letterArray[j];
                Cell cell = ExcelWriterUtil.getCellOrCreate(dataRow, firstDataColumnIndex + j);
                cell.setCellFormula(formula.replaceAll("cell%", tagsSheetName + "!" + columnLetter + (i + 2)));
                cell.setCellType(CellType.FORMULA);
                // 设置单元格样式及小数点位
                TargetManagement targetManagement = tagsMap.get(reportTemplateTags);
                Integer scaleObj = targetManagement.getScale();
                int scale = scaleObj != null ? scaleObj.intValue() : defaultScale;
                CellStyle cellStyle = ExcelStyleUtil.getCellStyle(workbook, scale);
                //设置样式
                cell.setCellStyle(cellStyle);

                j++;
            }
        }

        //添加汇总值到最后
        if ("1".equals(reportTemplateConfig.getIsAddAvg())) {
            Row summaryRow = ExcelWriterUtil.getRowOrCreate(firstSheet, firstSheet.getLastRowNum() + 1);
            Cell averageCell = ExcelWriterUtil.getCellOrCreate(summaryRow, 1);
            averageCell.setCellValue("平均值");
            averageCell.setCellStyle(ExcelStyleUtil.getCellStyle(workbook));

            for (int j = 0; j < tagsMapSize; j++) {
                Cell cell = ExcelWriterUtil.getCellOrCreate(summaryRow, firstDataColumnIndex + j);
                String columnLetter = letterArray[firstDataColumnIndex + j];
                String avgBegin = columnLetter + (firstDataRowIndex + 1);
                String avgEnd = columnLetter + (firstDataRowIndex + maxRow);
                String formula = String.format(avarageFormula, avgBegin, avgEnd);
                cell.setCellFormula(formula);
                cell.setCellType(CellType.FORMULA);
                // 设置平均值单元格样式和小数点位
                CellStyle cellStyle = ExcelStyleUtil.getCellStyle(workbook, defaultScale);
                cell.setCellStyle(cellStyle);
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
     * 创建tags sheet并且填充点位信息
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
        Sheet dictionarySheet = workbook.createSheet("_dictionary");
        Row dictionarySheetFirstRow = ExcelWriterUtil.getRowOrCreate(dictionarySheet,0);
        Cell dictionarySheetFirstRowCell1 = ExcelWriterUtil.getCellOrCreate(dictionarySheetFirstRow, 0);
        dictionarySheetFirstRowCell1.setCellValue("version");

        Cell dictionarySheetFirstRowCell2 = ExcelWriterUtil.getCellOrCreate(dictionarySheetFirstRow, 1);

        dictionarySheetFirstRowCell2.setCellValue(SequenceEnum.getVersion(sequenceCode));
    }

}
