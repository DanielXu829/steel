package com.cisdi.steel.module.job.util.date;

import com.cisdi.steel.common.util.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间构建
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/5 </P>
 *
 * @author leaf
 * @version 1.0
 */
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
        // TODO: 现在当天没有数据 所以目前扩大时间范围
//        Date todayBeginTime = DateUtil.getDateBeginTime(date);
//        Date todayEndTime = DateUtil.getDateEndTime(date);
        Calendar instance = Calendar.getInstance();
        instance.set(2015, 1, 1);
        Date todayBeginTime = instance.getTime();
        instance.set(2019, 1, 1);
        Date todayEndTime = instance.getTime();
        return new DateQuery(todayBeginTime, todayEndTime);
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
            return new DateQuery(startHour, endHour);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        return new DateQuery(beginTime, endTime);
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
