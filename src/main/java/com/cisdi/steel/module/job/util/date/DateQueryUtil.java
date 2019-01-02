package com.cisdi.steel.module.job.util.date;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.enums.TimeUnitEnum;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间构建
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/5 </P>
 *
 * @author leaf
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class DateQueryUtil {

    /**
     * 构建一个当天的 开始 时间---结束时间
     * 如 时间：2018-12-20 10:32:55
     * recordDate=2018-12-20 10:32:55,startTime=2018-12-20 00:00:00,endTime=2018-12-21 00:00:00
     *
     * @return 结果
     */
    public static DateQuery buildToday(Date date) {
        Date todayBeginTime = DateUtil.getDateBeginTime(date);
        Date todayEndTime = DateUtil.getDateEndTime(date);
        return new DateQuery(todayBeginTime, todayEndTime, date);
    }

    /**
     * 返回一小时范围
     * 当前一个小时时间段
     *
     * @param date 时间
     * @return 结果
     */
    private static DateQuery buildHour(Date date) {
        String dateTime = DateUtil.getFormatDateTime(date, "yyyy-MM-dd HH");
        String startHourString = dateTime + ":00:00";
        DateFormat df = new SimpleDateFormat(DateUtil.fullFormat);
        try {
            Date startHour = df.parse(startHourString);
            Date endHour = DateUtil.addHours(startHour, 1);
            return new DateQuery(startHour, endHour, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 返回 指定周的开始和结束时间
     *
     * @param date 时间
     * @return 结果
     */
    public static DateQuery buildWeek(Date date) {
        Date weekBeginTime = DateUtil.getWeekBeginTime(date);
        Date weekEndTime = DateUtil.getWeekEndTime(date);
        Date dateBeginTime = DateUtil.getDateBeginTime(weekBeginTime);
        Date dateEndTime = DateUtil.getDateEndTime(weekEndTime);
        return new DateQuery(dateBeginTime, dateEndTime, date);
    }

    /**
     * 构建每小时时间段
     * 如传入时间为 04:29:51
     * recordDate=2018-12-20 01:29:51,startTime=2018-12-20 00:00:00,endTime=2018-12-20 01:00:00
     * recordDate=2018-12-20 02:29:51,startTime=2018-12-20 01:00:00,endTime=2018-12-20 02:00:00
     * recordDate=2018-12-20 03:29:51,startTime=2018-12-20 02:00:00,endTime=2018-12-20 03:00:00
     * recordDate=2018-12-20 04:29:51,startTime=2018-12-20 03:00:00,endTime=2018-12-20 04:00:00
     * recordDate=2018-12-20 05:29:51,startTime=2018-12-20 04:00:00,endTime=2018-12-20 05:00:00
     *
     * @param date 指定时间
     * @return 结果
     */
    public static List<DateQuery> buildDayHourEach(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        List<DateQuery> queryList = new ArrayList<>();
        for (int i = hour; i >= 0; i--) {
            Date nextDate = DateUtil.addHours(date, -i);
            DateQuery query = DateQueryUtil.buildHour(nextDate);
            queryList.add(query);
        }
        return queryList;
    }


    /**
     * 构建每小时时间段
     *
     * @param date 指定时间
     * @return 结果
     */
    public static List<DateQuery> buildDayHalfHourEach(Date date) {
        List<DateQuery> result = new ArrayList<>();
        Date startTime = DateUtil.getDateBeginTime(date);

        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(dateBeginTime);
        endTime.add(Calendar.MINUTE, 30);
        do {
            result.add(new DateQuery(startTime, endTime.getTime(), date));
            startTime = endTime.getTime();
            endTime.add(Calendar.MINUTE, 30);
        } while (endTime.getTime().before(date));
        result.add(new DateQuery(startTime, new Date(), date));
        return result;
    }

    /**
     * 构建每4小时时间段
     *
     * @param date 指定时间
     * @return 结果
     */
    public static List<DateQuery> buildDay4HourEach(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        List<DateQuery> queryList = new ArrayList<>();
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        for (int i = 0; i < 6; i++) {
            Date previous = DateUtil.addHours(dateBeginTime, 4);
            queryList.add(new DateQuery(dateBeginTime, previous, dateBeginTime));
            dateBeginTime = previous;
        }
        return queryList;
    }


    /**
     * 构建每8小时时间段
     *
     * @param date 指定时间
     * @return 结果
     */
    public static List<DateQuery> buildDay8HourEach(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        List<DateQuery> queryList = new ArrayList<>();
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        for (int i = 0; i < 3; i++) {
            Date previous = DateUtil.addHours(dateBeginTime, 8);
            queryList.add(new DateQuery(dateBeginTime, previous, dateBeginTime));
            dateBeginTime = previous;
        }
        return queryList;
    }


    /**
     * 构建不规则时间段
     *
     * @param date
     * @return
     */
    public static List<DateQuery> buildDayOtherHourEach(Date date) {
        List<DateQuery> queryList = new ArrayList<>();
        queryList.add(buildHour(date, 0, 8));
        queryList.add(buildHour(date, 8, 6));
        queryList.add(buildHour(date, 14, 3));
        queryList.add(buildHour(date, 17, 2));
        queryList.add(buildHour(date, 19, 3));
        queryList.add(buildHour(date, 22, 0));
        return queryList;
    }

    private static DateQuery buildHour(Date date, int hour, int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();
        Date end = DateUtil.addHours(time, index);
        DateQuery dateQuery = new DateQuery(time, end, date);
        return dateQuery;
    }

    /**
     * 记录每月的每天
     *
     * @return 结果
     */
    public static List<DateQuery> buildMonthDayEach(Date date) {
        DateQuery dateQuery = buildMonth(date);
        Date startTime = dateQuery.getStartTime();
        long betweenDays = DateUtil.getBetweenDays(startTime, date);
        List<DateQuery> queryList = new ArrayList<>();
        Date currentDate = startTime;
        for (int i = 0; i <= betweenDays; i++) {
            DateQuery query = buildToday(currentDate);
            queryList.add(query);
            currentDate = DateUtil.addDays(currentDate, 1);
        }
        return queryList;
    }

    /**
     * 每月的数据
     *
     * @param date 查询的时间
     * @return 结果
     */
    public static List<DateQuery> buildYearMonthEach(Date date) {
        return null;
    }

    /**
     * 指定 月的时间范围
     *
     * @param date 指定月
     * @return 结果
     */
    public static DateQuery buildMonth(Date date) {
        Date monthStartTime = getMonthStartTime(date);
        Date beginTime = DateUtil.getDateBeginTime(monthStartTime);
        Date monthEndTime = getMonthEndTime(date);
        Date endTime = DateUtil.getDateEndTime(monthEndTime);
        return new DateQuery(beginTime, endTime, date);
    }


    /**
     * 获取指定月日期 1号
     */
    public static Date getMonthStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        // 设置为第一天
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 获取指定月 最后一天
     */
    public static Date getMonthEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //设置日期为本月最大日期
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return calendar.getTime();
    }


    public static DateQuery handlerDelay(DateQuery dateQuery, Integer delay, String delayUnit) {
        return handlerDelay(dateQuery, delay, delayUnit, true);
    }

    /**
     * @param dateQuery
     * @param delay
     * @param delayUnit
     * @param flag      true延迟 false
     * @return
     */
    public static DateQuery handlerDelay(DateQuery dateQuery, Integer delay, String delayUnit, boolean flag) {
        TimeUnitEnum timeUnitEnum = TimeUnitEnum.getValues(delayUnit);
        if (Objects.isNull(timeUnitEnum) || Objects.isNull(delay)) {
            dateQuery.setOldDate(dateQuery.getRecordDate());
            return dateQuery;
        }
        Date recordDate = dateQuery.getRecordDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(recordDate);
        int yield = Calendar.HOUR_OF_DAY;
        switch (timeUnitEnum) {
            case SECOND:
                yield = Calendar.SECOND;
                delay += 10;
                break;
            case MINUTE:
                yield = Calendar.MINUTE;
                delay += 2;
                break;
            case HOUR:
                yield = Calendar.HOUR_OF_DAY;
                break;
            case DATE:
                yield = Calendar.DAY_OF_MONTH;
                break;
            case MONTH:
                yield = Calendar.MONTH;
                break;
            default:
                yield = Calendar.HOUR_OF_DAY;
        }
        if (flag) {
            calendar.add(yield, -delay);
        } else {
            calendar.add(yield, delay);
        }
        dateQuery.setRecordDate(calendar.getTime());
        dateQuery.setOldDate(recordDate);
        return dateQuery;
    }

    public static Map<String, String> getQueryParam(DateQuery dateQuery, int hour, int min, int sec) {
        Map<String, String> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getStartTime());
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        map.put("starttime", calendar.getTime().getTime() + "");

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(dateQuery.getEndTime());
        calendar1.set(Calendar.HOUR, hour);
        calendar1.set(Calendar.MINUTE, min);
        calendar1.set(Calendar.SECOND, sec);
        map.put("endtime", calendar1.getTime().getTime() + "");
        return map;
    }
}
