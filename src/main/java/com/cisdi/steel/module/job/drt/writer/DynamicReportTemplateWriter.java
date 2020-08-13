package com.cisdi.steel.module.job.drt.writer;

import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.drt.dto.HandleDataDTO;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
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
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
        if (reportTemplateConfigDTO == null || reportTemplateConfigDTO.getReportTemplateConfig() == null
                || reportTemplateConfigDTO.getReportTemplateTags() == null) {
            log.error("动态报表配置信息为空！");
            throw new RuntimeException("动态报表配置信息为空");
        }
        ReportTemplateConfig reportTemplateConfig = reportTemplateConfigDTO.getReportTemplateConfig();
        List<ReportTemplateTags> reportTemplateTags = reportTemplateConfigDTO.getReportTemplateTags();

        Sheet sheet = workbook.getSheetAt(1); // tagSheet
        List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(sheet);
        List<TargetManagement> targetManagements = targetManagementMapper.selectTargetManagementsByTargetNames(tagNames);
        // 根据sequence排序
        reportTemplateTags = reportTemplateTags.stream().sorted(Comparator.comparing(ReportTemplateTags::getSequence))
                .collect(Collectors.toList());
        if (tagNames.size() != reportTemplateTags.size()) {
            throw new RuntimeException("动态报表tagSheet和配置中的target数量不一致");
        }
        // 拼接tag点前缀和后缀
        List<String> oldTagFormulas = targetManagements.stream().map(TargetManagement::getTargetFormula)
                .collect(Collectors.toList());
        List<String> newTagFormulas = joinSuffix(oldTagFormulas, reportTemplateTags);
        HashMap<String, TargetManagement> targetManagementMap = new LinkedHashMap<>();
        for (int i = 0; i < newTagFormulas.size(); i++) {
            String newTagFormula = newTagFormulas.get(i);
            TargetManagement targetManagement = targetManagements.get(i);
            targetManagementMap.put(newTagFormula, targetManagement);
        }
        List<DateQuery> dateQuerys = getDateQuerys(recordDate, reportTemplateConfig);

        // 构建DateQuery
        // 时间细分-小时，时间范围
        DynamicReportTemplateWriter writer = null;
        SequenceEnum sequenceEnum = SequenceEnum.getSequenceEnumByCode(template.getSequence());
        switch (sequenceEnum) {
            case GL8:
                writer = applicationContext.getBean(GLDynamicReportTemplateWriter.class);
                break;
            case JH910:
                writer = applicationContext.getBean(JHDynamicReportTemplateWriter.class);
                break;
        }

        String timeType = reportTemplateConfig.getTimeType();
        TimeTypeEnum timeTypeEnum = TimeTypeEnum.getEnumByCode(timeType);
        HandleDataDTO handleDataDTO = HandleDataDTO.builder()
                .excelDTO(excelDTO)
                .workbook(workbook)
                .version(version)
                .targetManagementMap(targetManagementMap)
                .dateQuerys(dateQuerys)
                .needToWriteTime(TimeTypeEnum.RECENT_TIME.equals(timeType))
                .build();

        if (writer == null){
            throw new RuntimeException("没有对应的子类解析器writer");
        } else {
            writer.handleData(handleDataDTO);
        }

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
     * 生成查询策略
     * @param reportTemplateConfig
     * @return
     */
    protected List<DateQuery> getDateQuerys(Date recordDate, ReportTemplateConfig reportTemplateConfig) {
        List<DateQuery> dateQueries;
        String timeType = reportTemplateConfig.getTimeType();
        TimeTypeEnum timeTypeEnum = TimeTypeEnum.getEnumByCode(timeType);
        switch (timeTypeEnum) {
            case TIME_RANGE:
                return getTimeRangeDateQuerys(recordDate, reportTemplateConfig);
            case RECENT_TIME:
                return getRecentTimeDateQuerys(recordDate, reportTemplateConfig);
        }
        Integer timeDivideType = reportTemplateConfig.getTimeDivideType();
        return null;
    }

    /**
     * 时间范围查询策略
     * @param reportTemplateConfig
     * @return
     */
    protected List<DateQuery> getTimeRangeDateQuerys(Date recordDate, ReportTemplateConfig reportTemplateConfig) {
        Integer timeDivideType = reportTemplateConfig.getTimeDivideType();
        TimeDivideEnum timeDivideEnum = TimeDivideEnum.getEnumByCode(timeDivideType);
        Integer startTimeslot = Integer.valueOf(reportTemplateConfig.getStartTimeslot());
        Integer endTimeslot = Integer.valueOf(reportTemplateConfig.getEndTimeslot());
        Integer timeslotInterval = Integer.valueOf(reportTemplateConfig.getTimeslotInterval());
        switch (timeDivideEnum) {
            case HOUR:
                return getDateQuerysByHourRange(recordDate, startTimeslot, endTimeslot, timeslotInterval);
            case DAY:
                return getDateQuerysByDayRange(recordDate, startTimeslot, endTimeslot, timeslotInterval);
            case MONTH:
                return getDateQuerysBymonthRange(recordDate, startTimeslot, endTimeslot, timeslotInterval);
        }
        return null;
    }

    /**
     * 最近时间查询策略
     * @param reportTemplateConfig
     * @return
     */
    protected List<DateQuery> getRecentTimeDateQuerys(Date recordDate, ReportTemplateConfig reportTemplateConfig) {
        Integer timeDivideType = reportTemplateConfig.getTimeDivideType();
        TimeDivideEnum timeDivideEnum = TimeDivideEnum.getEnumByCode(timeDivideType);
        Integer timeslotInterval = Integer.valueOf(reportTemplateConfig.getTimeslotInterval());
        Integer lastTimeslot = reportTemplateConfig.getLastTimeslot();
        switch (timeDivideEnum) {
            case HOUR:
                return getDateQuerysByRecentHours(recordDate, lastTimeslot, timeslotInterval);
            case DAY:
                return getDateQuerysByRecentHours(recordDate, lastTimeslot, timeslotInterval);
            case MONTH:
                return getDateQuerysByRecentHours(recordDate, lastTimeslot, timeslotInterval);
        }
        return null;
    }

    /**
     * 小时范围查询策略
     * @param recordDate
     * @param startHour
     * @param endHour
     * @param interval
     * @return
     */
    protected List<DateQuery> getDateQuerysByHourRange(Date recordDate, Integer startHour, Integer endHour, Integer interval) {
        List<DateQuery> dateQueries = new ArrayList<>();
        Date startDate;
        if (startHour < endHour) {
            // 开始时间为当天
            startDate = DateUtil.getHourTimeByDateAndHourNumber(recordDate, startHour);
        } else {
            // 开始时间为前一天
            startDate = DateUtil.getHourTimeByDateAndHourNumber(DateUtil.addDays(recordDate, -1), startHour);
        }
        Date endDate = DateUtil.getHourTimeByDateAndHourNumber(recordDate, endHour);
        Date queryEndDate = DateUtils.addHours(startDate, interval);
        while (DateUtil.isDayBeforeOrEqualAnotherDay(queryEndDate, endDate)) {
            DateQuery dateQuery = new DateQuery(startDate, queryEndDate, recordDate);
            dateQueries.add(dateQuery);
            startDate = queryEndDate;
            queryEndDate = DateUtils.addHours(queryEndDate, interval);
        }
        return dateQueries;
    }

    /**
     * 天范围查询策略
     * @param recordDate
     * @param startDay
     * @param endDay
     * @param interval
     * @return
     */
    protected List<DateQuery> getDateQuerysByDayRange(Date recordDate, Integer startDay, Integer endDay, Integer interval) {
        List<DateQuery> dateQueries = new ArrayList<>();
        Date startDate;
        if (startDay < endDay) {
            // 开始时间为当月
            startDate = DateUtil.getDayTimeByDateAndDayNumber(recordDate, startDay);
        } else {
            // 开始时间为前一个月
            startDate = DateUtil.getDayTimeByDateAndDayNumber(DateUtil.addMonths(recordDate, -1), startDay);
        }
        Date endDate = DateUtil.getDayTimeByDateAndDayNumber(recordDate, endDay);
        Date queryEndDate = DateUtils.addDays(startDate, interval);
        while (DateUtil.isDayBeforeOrEqualAnotherDay(queryEndDate, endDate)) {
            DateQuery dateQuery = new DateQuery(startDate, queryEndDate, recordDate);
            dateQueries.add(dateQuery);
            startDate = queryEndDate;
            queryEndDate = DateUtils.addDays(queryEndDate, interval);
        }
        return dateQueries;
    }

    protected List<DateQuery> getDateQuerysBymonthRange(Date recordDate, Integer startMonth, Integer endMonth, Integer interval) {
        List<DateQuery> dateQueries = new ArrayList<>();
        Date startDate;
        if (startMonth < endMonth) {
            // 开始时间为当月
            startDate = DateUtil.getDayTimeByDateAndMonthNumber(recordDate, startMonth);
        } else {
            // 开始时间为前一年
            startDate = DateUtil.getDayTimeByDateAndMonthNumber(DateUtil.addYears(recordDate, -1), startMonth);
        }
        Date endDate = DateUtil.getDayTimeByDateAndMonthNumber(recordDate, endMonth);
        Date queryEndDate = DateUtils.addMonths(startDate, interval);
        while (DateUtil.isDayBeforeOrEqualAnotherDay(queryEndDate, endDate)) {
            DateQuery dateQuery = new DateQuery(startDate, queryEndDate, recordDate);
            dateQueries.add(dateQuery);
            startDate = queryEndDate;
            queryEndDate = DateUtils.addMonths(queryEndDate, interval);
        }
        return dateQueries;
    }

    protected List<DateQuery> getDateQuerysByRecentHours(Date recordDate, Integer lastTimeslot, Integer timeslotInterval) {
        // TODO 最近小时
        return null;
    }

    /**
     * 处理报表数据
     * @param excelDTO
     * @param workbook
     * @param version
     */
    protected  void handleData(HandleDataDTO handleDataDTO) {

    }

}
