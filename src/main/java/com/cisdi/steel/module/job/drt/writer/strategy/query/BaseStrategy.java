package com.cisdi.steel.module.job.drt.writer.strategy.query;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.enums.TimeDivideEnum;
import com.cisdi.steel.module.report.enums.TimeTypeEnum;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseStrategy implements HandleQueryDataStrategy {
    @Autowired
    protected HttpProperties httpProperties;

    @Autowired
    protected HttpUtil httpUtil;

    /**
     * 生成查询策略
     * @param recordDate
     * @param reportTemplateConfig
     * @return
     */
    @Override
    public List<DateQuery> getDateQueries(Date recordDate, ReportTemplateConfig reportTemplateConfig) {
        String timeType = reportTemplateConfig.getTimeType();
        TimeTypeEnum timeTypeEnum = TimeTypeEnum.getEnumByCode(timeType);
        switch (timeTypeEnum) {
            case TIME_RANGE:
                return getTimeRangeDateQuerys(recordDate, reportTemplateConfig);
            case RECENT_TIME:
                return getRecentTimeDateQuerys(recordDate, reportTemplateConfig);
        }
        return null;
    }

    /**
     * 时间范围查询策略
     * @param recordDate
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
                return getDateQuerysByRecentDays(recordDate, lastTimeslot, timeslotInterval);
            case MONTH:
                return getDateQuerysByRecentMonths(recordDate, lastTimeslot, timeslotInterval);
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
        endDate = DateUtils.addHours(endDate, interval);
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
     * 天范围查询策略  当日的22点到第二天的22点
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
        endDate = DateUtil.addDays(endDate, interval);// 补充最后一天的查询条件
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

    /**
     * 最近多少小时查询策略
     * @param recordDate
     * @param lastTimeslot
     * @param interval
     * @return
     */
    protected List<DateQuery> getDateQuerysByRecentHours(Date recordDate, Integer lastTimeslot, Integer interval) {
        Date endDate = DateUtil.getCurrentHourTimeOfDate(recordDate);
        endDate = DateUtils.addHours(endDate, interval); // 补充最后一小时的查询条件
        List<DateQuery> dateQueries = new ArrayList<>();
        Date startDate = DateUtils.addHours(endDate, -lastTimeslot);
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
     * 最近多少天查询策略
     * @param recordDate
     * @param lastTimeslot
     * @param interval
     * @return
     */
    protected List<DateQuery> getDateQuerysByRecentDays(Date recordDate, Integer lastTimeslot, Integer interval) {
        List<DateQuery> dateQueries = new ArrayList<>();
        Date endDate = DateUtil.getHourTimeByDateAndHourNumber(recordDate, 22); // 当天22点
        // 最早的日期
        Date startDate = DateUtil.addDays(endDate, 1 - lastTimeslot);
        endDate = DateUtil.addDays(endDate, interval);
        // 补充最后一天的查询条件
        Date queryEndDate = DateUtils.addDays(startDate, interval);
        while (DateUtil.isDayBeforeOrEqualAnotherDay(queryEndDate, endDate)) {
            DateQuery dateQuery = new DateQuery(startDate, queryEndDate, recordDate);
            dateQueries.add(dateQuery);
            startDate = queryEndDate;
            queryEndDate = DateUtils.addDays(queryEndDate, interval);
        }
        return dateQueries;
    }

    /**
     * 最近多少月
     * @param recordDate
     * @param lastTimeslot
     * @param interval
     * @return
     */
    protected List<DateQuery> getDateQuerysByRecentMonths(Date recordDate, Integer lastTimeslot, Integer interval) {
        List<DateQuery> dateQueries = new ArrayList<>();
        Date endDate = DateUtil.getHourTimeByMonthAndDayAndHourNumber(recordDate, 1, 22); // 当月第一天的22点
        // 最早的日期
        Date startDate = DateUtil.addMonths(endDate, 1 - lastTimeslot);
        endDate = DateUtil.addMonths(endDate, interval);
        // 补充最后一个月的查询条件
        Date queryEndDate = DateUtils.addMonths(startDate, interval);
        while (DateUtil.isDayBeforeOrEqualAnotherDay(queryEndDate, endDate)) {
            DateQuery dateQuery = new DateQuery(startDate, queryEndDate, recordDate);
            dateQueries.add(dateQuery);
            startDate = queryEndDate;
            queryEndDate = DateUtils.addMonths(queryEndDate, interval);
        }
        return dateQueries;
    }
}
