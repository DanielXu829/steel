package com.cisdi.steel.module.data.util;

import com.cisdi.steel.common.util.DateUtil;

import java.util.*;

/**
 * <p>Description: 数据操作的工作类   </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/29 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class DataUtil {

    /**
     * 获取每天的小时
     *
     * @return 每天的小时数 只能取 小时和分钟 时间类型
     */
    public static List<Date> getDayHour() {
        Calendar calendar = Calendar.getInstance();
        List<Date> result = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), i, 0);
            Date time = gregorianCalendar.getTime();
            result.add(time);
        }
        return result;
    }

    /**
     * 获取每天的小时
     * 如 00:00 01:00 ~~ 23:00
     *
     * @return 字符串类型
     */
    public static List<String> getDayHourString() {
        List<Date> dayHour = getDayHour();
        List<String> result = new ArrayList<>(dayHour.size());
        dayHour.forEach(item -> result.add(DateUtil.getFormatDateTime(item, DateUtil.hhmmFormat)));
        return result;
    }

    public static List<Date> getMonthDay() {
        Calendar calendar = Calendar.getInstance();
        List<Date> result = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            Date time = gregorianCalendar.getTime();
            result.add(time);
        }
        return result;
    }

    /**
     * 取得当月天数
     */
    public static int getCurrentMonthLastDay() {
        Calendar a = Calendar.getInstance();
        //把日期设置为当月第一天
        a.set(Calendar.DATE, 1);
        //日期回滚一天，也就是最后一天
        a.roll(Calendar.DATE, -1);
        return a.get(Calendar.DATE);
    }

    /**
     * 得到指定月的天数
     */
    public static int getMonthLastDay(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        // 把日期设置为当月第一天
        a.set(Calendar.DATE, 1);
        // 日期回滚一天，也就是最后一天
        a.roll(Calendar.DATE, -1);
        return a.get(Calendar.DATE);
    }

    /**
     *
     * @param date 指定日期
     * @param day 每月几号
     * @return 结果
     */
    public static Date getMonthDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, day);
        return calendar.getTime();
    }
}
