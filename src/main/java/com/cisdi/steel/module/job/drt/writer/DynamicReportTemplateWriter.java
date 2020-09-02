package com.cisdi.steel.module.job.drt.writer;

import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.drt.dto.HandleDataDTO;
import com.cisdi.steel.module.job.drt.writer.strategy.query.HandleQueryDataStrategy;
import com.cisdi.steel.module.job.drt.writer.strategy.query.HandleQueryDataStrategyContext;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.dto.ReportTemplateConfigDTO;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.enums.*;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import com.cisdi.steel.module.report.service.ReportTemplateConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 动态报表 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2020/1/7 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class DynamicReportTemplateWriter extends AbstractExcelReadWriter {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected TargetManagementMapper targetManagementMapper;

    @Autowired
    private ReportTemplateConfigService reportTemplateConfigService;

    @Autowired
    private HandleQueryDataStrategyContext handleQueryDataStrategyContext;

    /**
     * @param excelDTO 数据
     * @return
     */
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        ReportCategoryTemplate template = excelDTO.getTemplate();
        Date recordDate = excelDTO.getDateQuery().getRecordDate();
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
        // TODO
//        if (reportTemplateConfigDTO == null || reportTemplateConfigDTO.getReportTemplateConfig() == null
//                || reportTemplateConfigDTO.getReportTemplateTags() == null) {
//            log.error("动态报表配置信息为空！");
//            throw new RuntimeException("动态报表配置信息为空");
//        }
        ReportTemplateConfig reportTemplateConfig = reportTemplateConfigDTO.getReportTemplateConfig();
        // TODO
//        List<ReportTemplateTags> reportTemplateTags = reportTemplateConfigDTO.getReportTemplateTags();
        List<ReportTemplateTags> reportTemplateTags = null;

        Sheet sheet = workbook.getSheetAt(1); // tagSheet
        List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(sheet); // 获取tagSheet首行的tagName
        reportTemplateTags = reportTemplateTags.stream().sorted(Comparator.comparing(ReportTemplateTags::getSequence))
                .collect(Collectors.toList()); // 根据sequence排序
        if (tagNames.size() != reportTemplateTags.size()) {
            throw new RuntimeException("动态报表tagSheet和配置中的tag点数量不一致");
        }
        List<TargetManagement> targetManagements = targetManagementMapper.selectTargetManagementsByTargetNames(tagNames);
        List<String> oldTagFormulas = targetManagements.stream().map(TargetManagement::getTargetFormula)
                .collect(Collectors.toList());
        List<String> newTagFormulas = joinSuffix(oldTagFormulas, reportTemplateTags); // 拼接tag点前缀和后缀
        SequenceEnum sequenceEnum = SequenceEnum.getSequenceEnumByCode(template.getSequence());
        HandleQueryDataStrategy handleStrategy =
                handleQueryDataStrategyContext.getHandleQueryDataStrategy(sequenceEnum.getSequenceCode());
        List<DateQuery> dateQuerys = handleStrategy.getDateQueries(recordDate, reportTemplateConfig); // 获取查询策略
        if (CollectionUtils.isEmpty(dateQuerys)) {
            log.error("生产查询策略失败！");
            throw new RuntimeException("生产查询策略失败！");
        }
        HandleDataDTO handleDataDTO = HandleDataDTO.builder()
                .excelDTO(excelDTO)
                .workbook(workbook)
                .version(version)
                .newTagFormulas(newTagFormulas)
                .dateQuerys(dateQuerys)
                .reportTemplateConfig(reportTemplateConfig)
                .handleStrategy(handleStrategy)
                .build();
        handleData(handleDataDTO);

        return workbook;
    }

    /**
     * 拼接tagformula前缀和后缀
     * @param tagNames
     * @param reportTemplateTags
     * @return
     */
    protected List<String> joinSuffix(List<String> tagNames, List<ReportTemplateTags> reportTemplateTags) {
        List<String> tagTimeSuffixCodeList = TagTimeSuffixEnum.getTagTimeSuffixCodeList();
        List<String> tagCalSuffixCodeList = TagCalSuffixEnum.getTagCalSuffixCodeList();
        List<String> newTagFormulas = new ArrayList<>();
        // 截取最后两位原始后缀 拼接配置的后缀
        for (int i = 0; i < tagNames.size(); i++) {
            String tagName = tagNames.get(i);
            ReportTemplateTags reportTemplateTag = reportTemplateTags.get(i);
            for (String calCode : tagCalSuffixCodeList) {
                if (StringUtils.endsWith(tagName, "_" + calCode)) {
                    tagName = tagName.substring(0, tagName.length() - calCode.length() - 1);
                    break;
                }
            }
            for (String timeCode : tagTimeSuffixCodeList) {
                if (StringUtils.endsWith(tagName, "_" + timeCode)) {
                    tagName = tagName.substring(0, tagName.length() - timeCode.length() - 1);
                    break;
                }
            }
            List<String> stringsNeedToJoin = Arrays.asList(tagName, reportTemplateTag.getTagTimeSuffix(),
                    reportTemplateTag.getTagCalSuffix());
            newTagFormulas.add(String.join("_", stringsNeedToJoin));
        }
        return newTagFormulas;
    }

    /**
     * 写入时间列
     * @param mainSheet
     * @param reportTemplateConfig
     * @param dateQuerys
     */
    protected void writeTimeColumn(Sheet mainSheet, ReportTemplateConfig reportTemplateConfig, List<DateQuery> dateQuerys) {
        Cell timeTitleCell = PoiCustomUtil.getCellByValue(mainSheet,"时间");
        int rowIndex = timeTitleCell.getRowIndex();
        int columnIndex = timeTitleCell.getColumnIndex();
        String timeType = reportTemplateConfig.getTimeType();
        TimeTypeEnum timeTypeEnum = TimeTypeEnum.getEnumByCode(timeType);
        Integer timeDivideType = reportTemplateConfig.getTimeDivideType();
        TimeDivideEnum timeDivideEnum = TimeDivideEnum.getEnumByCode(timeDivideType);
        for (int i = 0; i < dateQuerys.size(); i++) {
            DateQuery dateQuery = dateQuerys.get(i);
            Row timeRow = ExcelWriterUtil.getRowOrCreate(mainSheet, rowIndex + i + 1);
            Cell timeCell = ExcelWriterUtil.getCellOrCreate(timeRow, columnIndex);
            SimpleDateFormat format;
            String dateString = "";
            if (TimeTypeEnum.TIME_RANGE.equals(timeTypeEnum)) {
                // 每个月的月底日期不确定，特殊处理
                switch (timeDivideEnum) {
                    case HOUR:
                        format = new SimpleDateFormat(DateUtil.hhmmFormat);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                    case DAY:
                        format = new SimpleDateFormat(DateUtil.ddChineseFormat);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                    case MONTH:
                        format = new SimpleDateFormat(DateUtil.MMChineseFormat);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                }
            } else {
                switch (timeDivideEnum) {
                    case HOUR:
                        format = new SimpleDateFormat(DateUtil.yyyyMMddHHChineseFormat);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                    case DAY:
                        format = new SimpleDateFormat(DateUtil.yyyyMMddChineseFormat);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                    case MONTH:
                        format = new SimpleDateFormat(DateUtil.yyyyMM);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                }
            }
            PoiCustomUtil.setCellValue(timeCell, dateString);
        }
    }

    /**
     * 处理数据写入
     * @param handleDataDTO
     */
    protected void handleData(HandleDataDTO handleDataDTO) {
        Workbook workbook = handleDataDTO.getWorkbook();
        WriterExcelDTO excelDTO = handleDataDTO.getExcelDTO();
        List<DateQuery> dateQuerys = handleDataDTO.getDateQuerys();
        ReportTemplateConfig reportTemplateConfig = handleDataDTO.getReportTemplateConfig();

        // 动态报表生成的模板默认取第二个sheet。
        Sheet mainSheet = workbook.getSheetAt(0);
        Sheet tagSheet = workbook.getSheetAt(1);
        // 写入时间列
        writeTimeColumn(mainSheet, reportTemplateConfig, dateQuerys);

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
}
