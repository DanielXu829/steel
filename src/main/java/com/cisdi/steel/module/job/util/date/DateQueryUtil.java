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
//    public static DateQuery buildToday(Date date) {
//        Date todayBeginTime = DateUtil.getDateBeginTime(date);
//        Date todayEndTime = DateUtil.getDateEndTime(date);
//        todayEndTime = DateUtil.addMinute(todayEndTime, 50);
//        return new DateQuery(todayBeginTime, todayEndTime, date);
//    }

    /**
     * 构建前一天的22点到当天的22点
     * @param date
     * @return
     */
    public static DateQuery buildDayAheadTwoHour(Date date) {
        Date todayBeginTime = DateUtil.getDateBeginTime(date);
        todayBeginTime = DateUtil.addHours(todayBeginTime, -2);
        Date todayEndTime = DateUtil.getDateEndTime(date);
        todayEndTime = DateUtil.addHours(todayEndTime, -2);
        return new DateQuery(todayBeginTime, todayEndTime, date);
    }

    /**
     * 构建一个当天的开始时间（23:05:00）---结束时间（23:05:00）
     * 如 时间：2019-12-16 10:00:00
     * recordDate=2019-12-16 10:00:00, startTime=2019-12-15 23:05:00, endTime=2019-12-16 23:05:00
     *
     * @return 结果
     */
    public static DateQuery buildToday(Date date) {
        Date todayBeginTime = DateUtil.getDateBeginTime(date);
        todayBeginTime = DateUtil.addMinute(todayBeginTime, -50);
        Date todayEndTime = DateUtil.getDateEndTime(date);
        todayEndTime = DateUtil.addMinute(todayEndTime, -50);
        return new DateQuery(todayBeginTime, todayEndTime, date);
    }

    /**
     * 构建某个时间的前后一分钟
     * 如 时间：2019-12-16 10:02:13
     * recordDate=2019-12-16 10:02:13, startTime=2019-12-16 10:01:13, endTime=2019-12-16 10:03:13
     *
     * @return 结果
     */
    public static DateQuery buildBeforeAfter1Minute(Date date) {
        Date before1MinuteTime = DateUtil.addMinute(date, -1);
        Date todayEndTime = DateUtil.addMinute(date, 1);
        return new DateQuery(before1MinuteTime, todayEndTime, date);
    }

    /**
     * 构建某个时间的不含秒时间
     * 如 时间：2019-12-16 10:02:13
     * recordDate=2019-12-16 10:02:13, startTime=2019-12-16 10:02:00, endTime=2019-12-16 10:02:00
     *
     * @return 结果
     */
    public static DateQuery buildDateWithoutSeconds(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new DateQuery(cal.getTime(), cal.getTime(), date);
    }

    /**
     * 构建一个当天的 开始 时间---结束时间，延迟到第二天5分钟
     * 如 时间：2018-12-20 10:32:55
     * recordDate=2018-12-20 10:32:55,startTime=2018-12-20 00:00:00,endTime=2018-12-21 00:00:00
     *
     * @return 结果
     */
    public static DateQuery buildTodayDelayFiveMinute(Date date) {
        Date todayBeginTime = DateUtil.getDateBeginTime(date);
        Date todayEndTime = DateUtil.getDateEndTime(date);
        todayEndTime = DateUtil.addMinute(todayEndTime, 5);
        return new DateQuery(todayBeginTime, todayEndTime, date);
    }

    /**
     * 构建一个当天的 开始 时间---结束时间
     * 如 时间：2019-11-20 10:32:55
     * recordDate=2019-11-20 10:32:55,startTime=2019-11-20 00:00:00,endTime=2019-11-21 00:00:00
     *
     * @return 结果
     */
    public static DateQuery buildTodayNoDelay(Date date) {
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
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        int hour = 23;
        List<DateQuery> queryList = new ArrayList<>();
        for (int i = 0; i <= hour; i++) {
            Date nextDate = DateUtil.addHours(dateBeginTime, i);
            DateQuery query = DateQueryUtil.buildHour(nextDate);
            queryList.add(query);
        }
        return queryList;
    }

    /**
     * 构建以传入的开始时间为起点，结束时间为最终点的，间隔为一小时的多个时间段
     * example:
     * 传入 (2019-12-15 23:05:00, 2019-12-16 23:05:00)
     * 结果 23.05 ~ 00.05  00.05 ~ 01.05 ......  21.05 ~ 22.05  22.05 ~ 23.05)
     * @param beginDate
     * @param endDate
     * @return
     */
    public static List<DateQuery> buildDayHourOneEach(Date beginDate, Date endDate) {
        List<DateQuery> queryList = new ArrayList<>();
        Date temp = beginDate;
        Date endTemp = null;
        DateQuery query = null;
        while (temp.before(endDate)) {
            endTemp = DateUtil.addHours(temp, 1);
            query = new DateQuery(temp, endTemp, temp);
            queryList.add(query);
            temp = DateUtil.addHours(temp, 1);
        }

        return queryList;
    }

    /**
     * 构建以传入的开始时间当天的0点为起点，结束时间为最终点的，间隔为一小时的多个时间段
     * example:
     * 传入 (2019-12-16 01:00:00, 2019-12-17 00:00:00)
     * 结果 00.00 ~ 01.00  01.00 ~ 02.00 ......  22.00 ~ 23.00  23.00 ~ 24.00)
     * @param beginDate
     * @param endDate
     * @return
     */
    public static List<DateQuery> buildStartAndEndDayHourEach(Date start, Date end) {
        Date dateBeginTime = DateUtil.getDateBeginTime(start);
        List<DateQuery> queryList = new ArrayList<>();
        Date temp = dateBeginTime;
        while (temp.before(end)) {
            DateQuery query = DateQueryUtil.buildHour(temp);
            queryList.add(query);
            temp = DateUtil.addHours(temp, 1);
        }
        return queryList;
    }

    public static void main(String[] args) {
        List<DateQuery> dateQueries = buildYearDayEach(new Date());
        dateQueries.forEach(System.out::println);
    }

    /**
     * 构建每半小时时间段
     *
     * @param date 指定时间
     * @return 结果
     */
    public static List<DateQuery> buildDayHalfHourEach(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        List<DateQuery> queryList = new ArrayList<>();
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        for (int i = 0; i < 48; i++) {
            Date previous = DateUtil.addMinute(dateBeginTime, 30);
            queryList.add(new DateQuery(dateBeginTime, previous, dateBeginTime));
            dateBeginTime = previous;
        }
        return queryList;
    }

    /**
     * 构建每2小时时间段
     *
     * @param date 指定时间
     * @return 结果
     */
    public static List<DateQuery> buildDay2HourEach(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        List<DateQuery> queryList = new ArrayList<>();
        Date dateBeginSixTime = DateUtil.getDateBeginTimeOfSix(date);
        for (int i = 0; i < 9; i++) {
            Date previous = DateUtil.addHours(dateBeginSixTime, 2);
            queryList.add(new DateQuery(dateBeginSixTime, previous, dateBeginSixTime));
            dateBeginSixTime = previous;
        }
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        for (int i = 0; i < 3; i++) {
            Date previous = DateUtil.addHours(dateBeginTime, 2);
            queryList.add(new DateQuery(dateBeginTime, previous, dateBeginTime));
            dateBeginTime = previous;
        }
        return queryList;
    }

    /**
     * 从 前一天18-20、20-22、....0-2、......14-16
     * @param date
     * @return
     */
    public static List<DateQuery> buildDay2HourFromYesEighteen(Date date) {
        List<DateQuery> queryList = new ArrayList<>();
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        Date dateYestodayEighteen = DateUtil.addHours(dateBeginTime, -6);
        for (int i = 0; i < 12; i++) {
            Date previous = DateUtil.addHours(dateYestodayEighteen, 2);
            queryList.add(new DateQuery(dateYestodayEighteen, previous, dateYestodayEighteen));
            dateYestodayEighteen = previous;
        }

        return queryList;
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
     * 构建每12小时时间段
     *  夜班：晚上8点-早上8点
     *  白班：早上8点-晚上8点
     * @param date 指定时间
     * @return 结果
     */
    public static List<DateQuery> buildDay12HourEach(Date date) {
        List<DateQuery> queryList = new ArrayList<>();
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        Date nightWorkBeginTime = DateUtil.addHours(dateBeginTime, -4);
        Date nightWorkEndTime = DateUtil.addHours(dateBeginTime, 8);
        Date dayWorkEndTime = DateUtil.addHours(nightWorkEndTime, 12);
        queryList.add(new DateQuery(nightWorkBeginTime, nightWorkEndTime, dateBeginTime));
        queryList.add(new DateQuery(nightWorkEndTime, dayWorkEndTime, nightWorkEndTime));

        return queryList;
    }

    /**
     * 构建24小时时间段
     * 昨天晚上8点-今天晚上8点
     *
     * @param date 指定时间
     * @return 结果
     */
    public static DateQuery build24HoursFromEight(Date date) {
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        Date nightWorkBeginTime = DateUtil.addHours(dateBeginTime, -4);
        Date dayWorkEndTime = DateUtil.addHours(dateBeginTime, 20);
        DateQuery dateQuery = new DateQuery(nightWorkBeginTime, dayWorkEndTime, nightWorkBeginTime);

        return dateQuery;
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

    /**
     * 构建开始和结束时间均为某一天的起始时间00:00:00
     *
     * @param date
     * @return
     */
    public static DateQuery buildDayWithBeginTimeForBoth(Date date) {
        Date todayBeginTime = DateUtil.getDateBeginTime(date);
        Date todayEndTime = DateUtil.getDateBeginTime(date);
        return new DateQuery(todayBeginTime, todayEndTime, date);
    }

    /**
     * 构建不规则时间段
     *
     * @param date
     * @return
     */
    public static void buildJiePaiLing(List<String> list, Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String dateTime = DateUtil.getFormatDateTime(calendar.getTime(), DateUtil.fullFormat);
        list.add(dateTime);
    }

    /**
     * 指定构建 从指定时间 加减指定时间
     *
     * @param date
     * @param hour
     * @param index
     * @return
     */
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

    public static List<DateQuery> buildStartAndEndDayEach(Date start, Date end) {
        DateQuery dateQuery = buildMonth(start);
        Date startTime = dateQuery.getStartTime();
        long betweenDays = DateUtil.getBetweenDays(startTime, end);
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
     * 记录每年的每天
     *
     * @return 结果
     */
    public static List<DateQuery> buildYearDayEach(Date date) {
        DateQuery dateQuery = buildYear(date);
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
     * 记录每年的每月
     *
     * @return 结果
     */
    public static List<DateQuery> buildYearMonthEach(Date date) {
        DateQuery dateQuery = buildYear(date);
        Date startTime = dateQuery.getStartTime();
        long betweenDays = DateUtil.getBetweenMonths(date);
        List<DateQuery> queryList = new ArrayList<>();
        Date currentDate = startTime;
        for (int i = 1; i <= betweenDays; i++) {
            DateQuery query = buildMonth(currentDate);
            queryList.add(query);
            currentDate = DateUtil.addMonths(currentDate, 1);
        }
        return queryList;
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
     * 指定 月的时间范围
     *
     * @param date 指定月
     * @return 结果
     */
    public static DateQuery buildMonthAppend12hour(Date date) {
        Date monthStartTime = getMonthStartTime(date);
        Date beginTime = DateUtil.getDateBeginTime(monthStartTime);
        Date monthEndTime = getMonthEndTime(date);
        Date endTime = DateUtil.getDateEndTime(monthEndTime);
        endTime = DateUtil.addHours(endTime, 12);
        return new DateQuery(beginTime, endTime, date);
    }

    /**
     * 指定 年的时间范围
     *
     * @param date 指定年
     * @return 结果
     */
    public static DateQuery buildYear(Date date) {
        Date yearStartTime = getYearStartTime(date);
        Date beginTime = DateUtil.getDateBeginTime(yearStartTime);
        Date yearEndTime = getYearEndTime(date);
        Date endTime = DateUtil.getDateEndTime(yearEndTime);
        return new DateQuery(beginTime, endTime, date);
    }

    /**
     * 获取指定月日期指定号
     */
    public static Date getMonthSomeTime(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定月日期 1号
     */
    public static Date getMonthStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        // 设置为第一天
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
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

    /**
     * 获取指定年日期 1号
     */
    public static Date getYearStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        // 设置为第一天
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * 获取指定年 最后一天
     */
    public static Date getYearEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //设置日期为本年最大日期
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        return calendar.getTime();
    }


    public static DateQuery handlerDelay(DateQuery dateQuery, Integer delay, String delayUnit) {
        return handlerDelay(dateQuery, delay, delayUnit, true);
    }

    /**
     * 时间参数延迟处理
     * flag=true:需要延迟处理  flag=false:正常时间处理
     *
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

    /**
     * 指定时间格式化
     *
     * @param dateQuery 需格式化的时间参数
     * @param hour      格式化小时
     * @param min       格式化分钟
     * @param sec       格式化秒
     * @return
     */
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

    /**
     * 判断当前月有多少天
     *
     * @param year
     * @param month
     * @return
     */
    public static int getDays(int year, int month) {
        int days = 0;
        if (month != 2) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    days = 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    days = 30;
            }
        } else {
            // 闰年
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                days = 29;
            } else {
                days = 28;
            }
        }
        return days;
    }

    public static void getMonthStartTime() {
    }
}
