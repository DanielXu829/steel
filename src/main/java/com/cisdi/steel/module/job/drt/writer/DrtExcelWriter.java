package com.cisdi.steel.module.job.drt.writer;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.drt.dto.DrtWriterDTO;
import com.cisdi.steel.module.job.drt.dto.HandleDataDTO;
import com.cisdi.steel.module.job.drt.writer.strategy.query.HandleQueryDataStrategy;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.MetadataDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.dto.ReportTemplateConfigDTO;
import com.cisdi.steel.module.report.dto.ReportTemplateSheetDTO;
import com.cisdi.steel.module.report.entity.*;
import com.cisdi.steel.module.report.enums.SequenceEnum;
import com.cisdi.steel.module.report.enums.TimeDivideEnum;
import com.cisdi.steel.module.report.enums.TimeTypeEnum;
import com.cisdi.steel.module.report.util.ReportConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 动态报表 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/09/04</P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class DrtExcelWriter extends DrtAbstractWriter implements IDrtWriter<Workbook> {

    @Override
    public Workbook drtWriter(DrtWriterDTO drtWriterDTO) {
        ReportCategoryTemplate template = drtWriterDTO.getTemplate();
        Date recordDate = drtWriterDTO.getDateQuery().getRecordDate();
        Workbook workbook = getWorkbook(template.getTemplatePath());
        String version = null;
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
            throw e;
        }
        ReportTemplateConfigDTO reportTemplateConfigDTO =
                reportTemplateConfigService.getDTOById(template.getTemplateConfigId());
        ReportTemplateConfig reportTemplateConfig = reportTemplateConfigDTO.getReportTemplateConfig();
        List<ReportTemplateSheetDTO> reportTemplateSheetDTOs = reportTemplateConfigDTO.getReportTemplateSheetDTOs();
        // 对每个sheet进行数据写入
        for (ReportTemplateSheetDTO reportTemplateSheetDTO : reportTemplateSheetDTOs) {
            ReportTemplateSheet reportTemplateSheet = reportTemplateSheetDTO.getReportTemplateSheet();
            List<ReportTemplateTags> sheetTagList = reportTemplateSheetDTO.getReportTemplateTagsList();
            sheetTagList.sort(Comparator.comparing(ReportTemplateTags::getSequence)); // 根据sequence排序
            String sheetName = reportTemplateSheet.getSheetTitle();
            String tagsSheetName =  ReportConstants.TAG_SHEET_NAME_PREFIX + sheetName;
            Sheet tagSheet = workbook.getSheet(tagsSheetName);
            List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(tagSheet); // 获取tagSheet首行的tagName
            if (tagNames.size() != sheetTagList.size()) {
                throw new RuntimeException("动态报表tagSheet和配置中的tag点数量不一致");
            }
            List<TargetManagement> targetManagements = targetManagementMapper.selectTargetManagementsByTargetNames(tagNames);
            List<String> oldTagFormulas = targetManagements.stream().map(TargetManagement::getTargetFormula)
                    .collect(Collectors.toList());
            List<String> newTagFormulas = joinSuffix(oldTagFormulas, sheetTagList); // 拼接tag点前缀和后缀
            SequenceEnum sequenceEnum = SequenceEnum.getSequenceEnumByCode(template.getSequence());
            HandleQueryDataStrategy handleStrategy =
                    handleQueryDataStrategyContext.getHandleQueryDataStrategy(sequenceEnum.getSequenceCode());
            List<DateQuery> dateQuerys = handleStrategy.getDateQueries(recordDate, reportTemplateSheet); // 获取查询策略
            if (CollectionUtils.isEmpty(dateQuerys)) {
                log.error("生成查询策略失败！");
                throw new RuntimeException("生成查询策略失败！");
            }
            HandleDataDTO handleDataDTO = HandleDataDTO.builder()
                    .drtWriterDTO(drtWriterDTO)
                    .workbook(workbook)
                    .version(version)
                    .newTagFormulas(newTagFormulas)
                    .dateQuerys(dateQuerys)
                    .reportTemplateSheet(reportTemplateSheet)
                    .handleStrategy(handleStrategy)
                    .build();
            handleData(handleDataDTO);
        }

        // 构建元数据
        this.buildMetadata(workbook, drtWriterDTO);
        return workbook;
    }

    /**
     * 通过DateQueries来构建时间，并写入时间列
     * @param mainSheet
     * @param reportTemplateConfig
     * @param dateQuerys
     */
    protected void writeTimeColumn(Sheet mainSheet, ReportTemplateSheet reportTemplateSheet, List<DateQuery> dateQuerys) {
        Cell timeTitleCell = PoiCustomUtil.getCellByValue(mainSheet, ReportConstants.TIME);
        int rowIndex = timeTitleCell.getRowIndex();
        int columnIndex = timeTitleCell.getColumnIndex();
        String timeType = reportTemplateSheet.getTimeType();
        TimeTypeEnum timeTypeEnum = TimeTypeEnum.getEnumByCode(timeType);
        Integer timeDivideType = reportTemplateSheet.getTimeDivideType();
        TimeDivideEnum timeDivideEnum = TimeDivideEnum.getEnumByCode(timeDivideType);
        for (int i = 0; i < dateQuerys.size(); i++) {
            DateQuery dateQuery = dateQuerys.get(i);
            Row timeRow = ExcelWriterUtil.getRowOrCreate(mainSheet, rowIndex + i + 1);
            Cell timeCell = ExcelWriterUtil.getCellOrCreate(timeRow, columnIndex);
            SimpleDateFormat timeFormat = new SimpleDateFormat(DateUtil.hhmmFormat);
            String dateString = StringUtils.EMPTY;
            if (TimeTypeEnum.TIME_RANGE.equals(timeTypeEnum)) {
                switch (timeDivideEnum) {
                    case HOUR:
                        timeFormat = new SimpleDateFormat(DateUtil.hhmmFormat);
                        break;
                    case DAY:
                        timeFormat = new SimpleDateFormat(DateUtil.ddChineseFormat);
                        break;
                    case MONTH:
                        timeFormat = new SimpleDateFormat(DateUtil.MMChineseFormat);
                        break;
                }
            } else {
                switch (timeDivideEnum) {
                    case HOUR:
                        timeFormat = new SimpleDateFormat(DateUtil.yyyyMMddHHChineseFormat);
                        break;
                    case DAY:
                        timeFormat = new SimpleDateFormat(DateUtil.yyyyMMddChineseFormat);
                        break;
                    case MONTH:
                        timeFormat = new SimpleDateFormat(DateUtil.yyyyMM);
                        break;
                }
            }
            // 格式化时间显示
            dateString = timeFormat.format(dateQuery.getStartTime());
            PoiCustomUtil.setCellValue(timeCell, dateString);
        }
    }

    /**
     * 处理数据写入
     * @param handleDataDTO
     */
    protected void handleData(HandleDataDTO handleDataDTO) {
        Workbook workbook = handleDataDTO.getWorkbook();
        List<DateQuery> dateQuerys = handleDataDTO.getDateQuerys();
        ReportTemplateSheet reportTemplateSheet = handleDataDTO.getReportTemplateSheet();
        String sheetName = reportTemplateSheet.getSheetTitle();
        String tagSheetName = ReportConstants.TAG_SHEET_NAME_PREFIX + sheetName;
        Sheet reportSheet = workbook.getSheet(sheetName);
        Sheet tagSheet = workbook.getSheet(tagSheetName);
        writeTimeColumn(reportSheet, reportTemplateSheet, dateQuerys); // 写入时间列
        // 写入每行数据
        for (int rowNum = 0; rowNum < dateQuerys.size(); rowNum++) {
            List<CellData> cellDataList = handleEachRowData(handleDataDTO, dateQuerys.get(rowNum), rowNum + 1);
            ExcelWriterUtil.setCellValue(tagSheet, cellDataList);
        }
    }

    /**
     * 处理每行数据
     * @param handleDataDTO
     * @param handleStrategy
     * @param dateQuery
     * @param rowIndex
     * @return
     */
    private List<CellData> handleEachRowData(HandleDataDTO handleDataDTO, DateQuery dateQuery, int rowIndex) {
        String version = handleDataDTO.getVersion();
        HandleQueryDataStrategy handleStrategy = handleDataDTO.getHandleStrategy();
        List<String> tagFormulas = handleDataDTO.getNewTagFormulas();
        List<CellData> resultList = new ArrayList<>();
        Map<String, LinkedHashMap<Long, Double>> tagValueMaps
                = handleStrategy.getTagValueMaps(dateQuery, version, tagFormulas);
        if (Objects.isNull(tagValueMaps)) {
            return resultList;
        }
        for (int columnIndex = 0; columnIndex < tagFormulas.size(); columnIndex++) {
            String tagFormula = tagFormulas.get(columnIndex);
            if (StringUtils.isBlank(tagFormula)) {
                continue;
            }
            Map<Long, Double> tagValueMap = tagValueMaps.get(tagFormula);
            if (Objects.isNull(tagValueMap)) {
                continue;
            }
            // 按照时间顺序从老到新排序
            List<Long> clockList = tagValueMap.keySet().stream().sorted().collect(Collectors.toList());
            Date queryStartTime = dateQuery.getStartTime();
            Date queryEndTime = dateQuery.getEndTime();

            if (tagFormula.endsWith("_evt")) {
                //如果是evt结尾的, 取时间范围内最大值
                Double maxVal = tagValueMap.values().stream().max(Comparator.comparing(Double::doubleValue)).orElse(null);
                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, maxVal);
            } else {
                // 默认取开始时间点
                if (clockList.contains(queryStartTime.getTime())) {
                    Double queryEndValue = tagValueMap.get(queryStartTime.getTime());
                    ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, queryEndValue);
                } else {
                    // 取第一个值
                    for (int j = 0; j < clockList.size(); j++) {
                        Long tempTime = clockList.get(j);
                        Date date = new Date(tempTime);
                        if (DateUtil.isDayBetweenAnotherTwoDays(date, queryStartTime, queryEndTime)) {
                            Double val = tagValueMap.get(tempTime);
                            ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, val);
                            break;
                        }
                    }
                }
            }
        }

        return resultList;
    }

    /**
     * 构建元数据
     * 默认取_metadata
     *
     * @param workbook 当前文件
     * @param drtWriterDTO 数据
     */
    public static void buildMetadata(Workbook workbook, DrtWriterDTO drtWriterDTO) {
        if (Objects.nonNull(workbook)) {
            Sheet sheet = workbook.getSheet("_metadata");
            MetadataDTO metadataDTO = new MetadataDTO(drtWriterDTO);
            if (Objects.nonNull(sheet)) {
                // buildMetadata(sheet, metadataDTO);
                PoiCustomUtil.writeAllMetadata(sheet, metadataDTO);
            } else {
                sheet = workbook.createSheet("_metadata");
                PoiCustomUtil.writeAllMetadata(sheet, metadataDTO);
            }
        }
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
}
