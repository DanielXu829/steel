package com.cisdi.steel.module.job.util.date;

import com.cisdi.steel.common.util.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
     *
     * @return 结果
     */
    public static DateQuery buildToday() {
        return buildToday(new Date());
    }

    /**
     * 构建一个当天的 开始 时间---结束时间
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
     * 前一个小时时间段
     *
     * @param date 时间
     * @return 结果
     */
    public static DateQuery buildHour(Date date) {
        Date previous = DateUtil.addHours(date, -1);
        String dateTime = DateUtil.getFormatDateTime(previous, "yyyy-MM-dd HH");
        String startHourString = dateTime + ":00:00";
        String endHourString = dateTime + ":59:59";
        DateFormat df = new SimpleDateFormat(DateUtil.fullFormat);
        try {
            Date startHour = df.parse(startHourString);
            Date endHour = df.parse(endHourString);
            return new DateQuery(startHour, endHour, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DateQuery build4Hour(Date date) {
        Date previous = DateUtil.addHours(date, -4);
        String dateTime = DateUtil.getFormatDateTime(previous, "yyyy-MM-dd HH");
        String dateTime1 = DateUtil.getFormatDateTime(DateUtil.addHours(date, -1), "yyyy-MM-dd HH");
        String startHourString = dateTime + ":00:00";
        String endHourString = dateTime1 + ":59:59";
        DateFormat df = new SimpleDateFormat(DateUtil.fullFormat);
        try {
            Date startHour = df.parse(startHourString);
            Date endHour = df.parse(endHourString);
            return new DateQuery(startHour, endHour, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DateQuery build8Hour(Date date) {
        Date previous = DateUtil.addHours(date, -8);
        String dateTime = DateUtil.getFormatDateTime(previous, "yyyy-MM-dd HH");
        String dateTime1 = DateUtil.getFormatDateTime(DateUtil.addHours(date, -1), "yyyy-MM-dd HH");
        String startHourString = dateTime + ":00:00";
        String endHourString = dateTime1 + ":59:59";
        DateFormat df = new SimpleDateFormat(DateUtil.fullFormat);
        try {
            Date startHour = df.parse(startHourString);
            Date endHour = df.parse(endHourString);
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
     *
     * @param date 指定时间
     * @return 结果
     */
    public static List<DateQuery> buildDayHourEach(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        List<DateQuery> queryList = new ArrayList<>();
        for (int i = hour - 1; i >= 0; i--) {
            Date nextDate = DateUtil.addHours(date, -i);
            DateQuery query = DateQueryUtil.buildHour(nextDate);
            queryList.add(query);
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
        for (int i = hour - 4; i >= 0; i -= 4) {
            Date nextDate = DateUtil.addHours(date, -i);
            DateQuery query = DateQueryUtil.build4Hour(nextDate);
            queryList.add(query);
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
        for (int i = hour - 8; i >= -1; i -= 8) {
            Date nextDate = DateUtil.addHours(date, -i);
            DateQuery query = DateQueryUtil.build8Hour(nextDate);
            queryList.add(query);
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
     * 返回一小时范围
     *
     * @return 结果
     */
    public static DateQuery buildHour() {
        return buildHour(new Date());
    }

    /**
     * 当月的时间段
     *
     * @return 当月时间范围
     */
    public static DateQuery buildMonth() {
        return buildMonth(new Date());
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
}
