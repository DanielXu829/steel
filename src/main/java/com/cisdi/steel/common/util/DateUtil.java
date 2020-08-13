package com.cisdi.steel.common.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description:  操作时间的工具类 </p>
 * <p>email: ypasdf@163.com </p>
 * <p>Copyright: Copyright (c) 2018 </p>
 * <P>Date: 2018/3/25 </P>
 *
 * @author common
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class DateUtil {

    /**
     * 小时:分钟
     */
    public static final String hhmmFormat = "HH:mm";
    /**
     * 月-几号
     */
    public static final String MMddFormat = "MM-dd";
    /**
     * 年份
     */
    public static final String yyyyFormat = "yyyy";

    /**
     * 月份
     */
    public static final String MMFormat = "MM";

    /**
     * 日份
     */
    public static final String ddFormat = "dd";
    /**
     * 年份
     */
    public static final String yyyyChineseFormat = "yyyy年";

    /**
     * 年-月-日
     */
    public static final String yyyyMMddFormat = "yyyy-MM-dd";

    /**
     * 年-月
     */
    public static final String yyyyMMFormat = "yyyy-MM";
    /**
     * 年-月-日 小时-分钟-秒数
     */
    public static final String fullFormat = "yyyy-MM-dd HH:mm:ss";
    /**
     * 几月几日
     */
    public static final String MMddChineseFormat = "MM月dd日";

    /**
     * 几日
     */
    public static final String ddChineseFormat = "dd日";

    /**
     * 几年几月几日 小时分钟
     */
    public static final String yyyyMMddHHmmChineseFormat = "yyyy年MM月dd日 HH时mm分";
    /**
     * 几年几月几日
     */
    public static final String yyyyMMddChineseFormat = "yyyy年MM月dd日";

    /**
     * 几年几月几日 小时分钟秒数
     */
    public static final String fullChineseFormat = "yyyy年MM月dd日 HH时mm分ss秒";
    /**
     * 几年几月几日 小时分钟秒数毫秒数
     */
    public static final String yyyyMMddHHmmssSSS = "yyyy-MM-dd HH:mm:ss:SSS";
    /**
     * 几年几月几日 小时分钟
     */
    public static final String yyyyMMddHHmm = "yyyy-MM-dd HH:mm";

    /**
     * 几年几月
     */
    public static final String yyyyMM = "yyyy年MM月";

    /**
     * 几年几月
     */
    public static final String yyyyMMWithSpace = "yyyy 年 MM 月";

    /**
     * 星期
     */
    public static final String[] WEEKS = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    public static final String NO_SEPARATOR = "yyyyMMddHHmmss";

    public static final List<String> WEEKS_THUR2WED = Arrays.asList("星期四", "星期五", "星期六", "星期日", "星期一", "星期二", "星期三");

    /**
     * 1个小时
     */
    public static final Long mills4oneHour = 60 * 60 * 1000L;
    /**
     * 1分钟
     */
    public static final Float mills4oneMinuteFloat = 60 * 1000f;
    /**
     * 1小时
     */
    public static final Float mills4oneHourFloat = 60 * 60 * 1000f;
    /**
     * 1天
     */
    public static final long mills4oneDay = 24 * mills4oneHourFloat.longValue();
    /**
     * 1天 floatl类型
     */
    public static final float mills4oneDayFloat = 24 * mills4oneHourFloat;


    /**
     * 返回当前日期 格式参照fullFormat
     *
     * @return
     */
    public static String curDateTimeStr() {
        DateFormat df = new SimpleDateFormat(fullFormat);
        return df.format(new Date());
    }

    /**
     * 返回当前日期 格式参照yyyyMMddFormat
     *
     * @return
     */
    public static String curDateStr() {
        DateFormat df = new SimpleDateFormat(yyyyMMddFormat);
        return df.format(new Date());
    }

    /**
     * 字符串转 日期类型
     *
     * @param str     需要转换的字符串
     * @param pattern 日期格式
     * @return 结果 null 表示格式错误 或者 正确的时间
     */
    public static Date strToDate(String str, String pattern) {
        if (StringUtils.isBlank(str) || StringUtils.isBlank(pattern) || str.length() != pattern.length()) {
            return null;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            Date result = df.parse(str);
            return result;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 字符串转字符串
     *
     * @param str     需要转换的字符串
     * @param oldFormat 日期格式
     * @param newFormat 日期格式
     * @return 结果 null 表示格式错误 或者 正确的时间
     */
    public static String strToStr(String str, String oldFormat, String newFormat) {
        Date date = strToDate(str, oldFormat);
        return (null == date) ? null : DateFormatUtils.format(date, newFormat);
    }

    /**
     * 字符串转日期
     *
     * @param str     需要转换的字符串
     * @param oldFormat 日期格式
     * @param newFormat 日期格式
     * @return 结果 null 表示格式错误 或者 正确的时间
     */
    public static Date strToDate(String str, String oldFormat, String newFormat) {
        String s = strToStr(str, oldFormat, newFormat);
        return (null == s) ? null : strToDate(s, newFormat);
    }

    /**
     * 得到指定时间的时间日期格式
     *
     * @param date   指定的时间
     * @param format 时间日期格式
     * @return
     */
    public static String getFormatDateTime(Date date, String format) {
        return DateFormatUtils.format(date, format);
    }

    /**
     * 判断是否是润年
     *
     * @param date 指定的时间
     * @return true:是润年,false:不是润年
     */
    public static boolean isLeapYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return isLeapYear(cal.get(Calendar.YEAR));
    }

    /**
     * 判断是否是润年
     *
     * @param year 指定的年
     * @return true:是润年,false:不是润年
     */
    public static boolean isLeapYear(int year) {
        GregorianCalendar calendar = new GregorianCalendar();
        return calendar.isLeapYear(year);
    }

    /**
     * 判断指定的时间是否是今天
     *
     * @param date 指定的时间
     * @return true:是今天,false:非今天
     */
    public static boolean isInToday(Date date) {
        boolean flag = false;
        Date now = new Date();
        String fullFormat = getFormatDateTime(now, DateUtil.yyyyMMddFormat);
        String beginString = fullFormat + " 00:00:00";
        String endString = fullFormat + " 23:59:59";
        DateFormat df = new SimpleDateFormat(DateUtil.fullFormat);
        try {
            Date beginTime = df.parse(beginString);
            Date endTime = df.parse(endString);
            flag = date.before(endTime) && date.after(beginTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 判断两时间是否是同一天
     *
     * @param from 第一个时间点
     * @param to   第二个时间点
     * @return true:是同一天,false:非同一天
     */
    public static boolean isSameDay(Date from, Date to) {
        boolean isSameDay = false;
        DateFormat df = new SimpleDateFormat(DateUtil.yyyyMMddFormat);
        String firstDate = df.format(from);
        String secondDate = df.format(to);
        isSameDay = firstDate.equals(secondDate);
        return isSameDay;
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
//        if (nowTime.getTime() == startTime.getTime()
//                || nowTime.getTime() == endTime.getTime()) {
//            return true;
//        }
        if (nowTime.getTime() == startTime.getTime()) {
            return true;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 求出指定的时间那天是星期几
     *
     * @param date 指定的时间
     * @return 星期X
     */
    public static String getWeekString(Date date) {
        return DateUtil.WEEKS[getWeek(date) - 1];
    }

    /**
     * 求出指定时间那天是星期几
     *
     * @param date 指定的时间
     * @return 1-7
     */
    public static int getWeek(Date date) {
        int week = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        week = cal.get(Calendar.DAY_OF_WEEK);
        return week;
    }

    /**
     * 判断今天是否是月末
     *
     * @param date
     * @return
     */
    public static boolean isLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + 1));
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 取得指定时间离现在是多少时间以前，如：3秒前,2小时前等
     * 注意：此计算方法不是精确的
     *
     * @param date 已有的指定时间
     * @return 时间段描述
     */
    public static String getAgoTimeString(Date date) {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Date agoTime = cal.getTime();
        long mTime = now.getTime() - agoTime.getTime();
        String str = "";
        long sTime = mTime / 1000;
        long minute = 60;
        long hour = 60 * 60;
        long day = 24 * 60 * 60;
        long weeks = 7 * 24 * 60 * 60;
        long months = 100 * 24 * 60 * 60;
        if (sTime < minute) {
            long timeValue = sTime;
            if (timeValue <= 0) {
                timeValue = 1;
            }
            str = timeValue + "秒前";
        } else if (sTime >= minute && sTime < hour) {
            long timeValue = sTime / minute;
            if (timeValue <= 0) {
                timeValue = 1;
            }
            str = timeValue + "分前";
        } else if (sTime >= hour && sTime < day) {
            long timeValue = sTime / hour;
            if (timeValue <= 0) {
                timeValue = 1;
            }
            str = timeValue + "小时前";
        } else if (sTime >= day && sTime < weeks) {
            long timeValue = sTime / day;
            if (timeValue <= 0) {
                timeValue = 1;
            }
            str = timeValue + "天前";
        } else if (sTime >= weeks && sTime < months) {
            DateFormat df = new SimpleDateFormat(DateUtil.MMddFormat);
            str = df.format(date);
        } else {
            DateFormat df = new SimpleDateFormat(DateUtil.yyyyMMddFormat);
            str = df.format(date);
        }
        return str;
    }

    /**
     * 判断指定时间是否是周末
     *
     * @param date 指定的时间
     * @return true:是周末,false:非周末
     */
    public static boolean isWeeks(Date date) {
        boolean isWeek = (getWeek(date) - 1 == 0 || getWeek(date) - 1 == 6);
        return isWeek;
    }

    /**
     * 得到今天的最开始时间
     *
     * @return 今天的最开始时间  00:00:00
     */
    public static Date getTodayBeginTime() {
        // 当前时间
        Date beginTime = new Date();
        return getDateBeginTime(beginTime);
    }

    /**
     * 得到指定日期最开始时间
     *
     * @param date 指定日期
     * @return 指定日期的最开始时间  00:00:00
     */
    public static Date getDateBeginTime(Date date) {
        String dateString = DateFormatUtils.format(date, "yyyy-MM-dd 00:00:00");
        Date beginTime = null;
        try {
            beginTime = DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd hh:mm:ss"});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return beginTime;
    }

    /**
     * 得到指定日期最开始时间
     *
     * @param date 指定日期
     * @return 指定日期的最开始时间  06:00:00
     */
    public static Date getDateBeginTimeOfSix(Date date) {
        String dateString = DateFormatUtils.format(date, "yyyy-MM-dd 06:00:00");
        Date beginTime = null;
        try {
            beginTime = DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd hh:mm:ss"});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return beginTime;
    }

    /**
     * 得到指定日期最开始时间
     *
     * @param date 指定日期
     * @return 指定日期的最开始时间  06:00:00
     */
    public static Date getDateBeginTimeOfTwenty(Date date) {
        String dateString = DateFormatUtils.format(date, "yyyy-MM-dd 20:00:00");
        Date beginTime = null;
        try {
            beginTime = DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd hh:mm:ss"});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return beginTime;
    }

    /**
     * 今天 最后结束时间
     *
     * @return 今天日期最后时间 23:59:59
     */
    public static Date getTodayEndTime() {
        // 目前
        Date endTime = new Date();
        return getDateEndTime(endTime);
    }

    /**
     * 指定 最后结束时间
     *
     * @return 指定时间 最后时间 23:59:59
     */
    public static Date getDateEndTime(Date date) {
        String dateString = DateFormatUtils.format(date, "yyyy-MM-dd 24:00:00");
        Date endTime = null;
        try {
            endTime = DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd hh:mm:ss"});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
    }

    public static Date getDateEndTime59(Date date) {
        String dateString = DateFormatUtils.format(date, "yyyy-MM-dd 23:59:59");
        Date endTime = null;
        try {
            endTime = DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd hh:mm:ss"});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
    }

    public static Date getDateEndTime22(Date date) {
        String dateString = DateFormatUtils.format(date, "yyyy-MM-dd 22:00:00");
        Date endTime = null;
        try {
            endTime = DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd hh:mm:ss"});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
    }

    /**
     * 取得本周的开始时间
     *
     * @return 本周的开始时间
     */
    public static Date getThisWeekBeginTime() {
        Date beginTime = null;
        Calendar cal = Calendar.getInstance();
        int week = getWeek(cal.getTime());
        week = week - 1;
        int days = 0;
        if (week == 0) {
            days = 6;
        } else {
            days = week - 1;
        }
        cal.add(Calendar.DAY_OF_MONTH, -days);
        beginTime = cal.getTime();
        return beginTime;
    }

    /**
     * 指定时间获取周开始时间
     *
     * @return
     */
    public static Date getWeekBeginTime(Date date) {
        Date beginTime = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = getWeek(cal.getTime());
        week = week - 1;
        int days = 0;
        if (week == 0) {
            days = 6;
        } else {
            days = week - 1;
        }
        cal.add(Calendar.DAY_OF_MONTH, -days);
        beginTime = cal.getTime();
        return beginTime;
    }


    /**
     * 取得本周的开始日期
     *
     * @param format 时间的格式
     * @return 指定格式的本周最开始时间
     */
    public static String getThisWeekBeginTimeString(String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(getThisWeekBeginTime());
    }

    /**
     * 获取一周7天的日期
     * @param date
     * @return
     */
    public static List<Date> getDaysOfWeek(Date date) {
        Date weekBeginTime = getWeekBeginTime(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(weekBeginTime);
        List<Date> datesOfWeek = new ArrayList<>();
        datesOfWeek.add(cal.getTime());
        for (int i = 0; i < 6; i++) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            datesOfWeek.add(cal.getTime());
        }
        return datesOfWeek;
    }

    /**
     * 获取 指定日期是本周的第几天
     * @param date
     * @return
     */
    public static int getDayOfWeekDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        if (weekDay == 1) {
            weekDay = 7;
        } else {
            weekDay = weekDay - 1;
        }
        return weekDay;
    }


    /**
     * 取得本周的结束时间
     *
     * @return 本周的结束时间
     */
    public static Date getThisWeekEndTime() {
        Date endTime = null;
        Calendar cal = Calendar.getInstance();
        int week = getWeek(cal.getTime());
        week = week - 1;
        int days = 0;
        if (week != 0) {
            days = 7 - week;
        }
        cal.add(Calendar.DAY_OF_MONTH, days);
        endTime = cal.getTime();
        return endTime;
    }

    /**
     * 获取指定时间周的结束时间
     *
     * @return
     */
    public static Date getWeekEndTime(Date date) {
        Date endTime = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = getWeek(cal.getTime());
        week = week - 1;
        int days = 0;
        if (week != 0) {
            days = 7 - week;
        }
        cal.add(Calendar.DAY_OF_MONTH, days);
        endTime = cal.getTime();
        return endTime;
    }

    /**
     * 取得本周的结束日期
     *
     * @param format 时间的格式
     * @return 指定格式的本周结束时间
     */
    public static String getThisWeekEndTimeString(String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(getThisWeekEndTime());
    }

    /**
     * 取得两时间相差的天数
     *
     * @param from 第一个时间
     * @param to   第二个时间
     * @return 相差的天数
     */
    public static long getBetweenDays(Date from, Date to) {
        long days = 0;
        long dayTime = 24 * 60 * 60 * 1000;
        long fromTime = from.getTime();
        long toTime = to.getTime();
        long times = Math.abs(fromTime - toTime);
        days = times / dayTime;
        return days;
    }

    public static long getBetweenMonths(Date to) {
        String dateTime = DateUtil.getFormatDateTime(to, "MM");
        return Long.valueOf(dateTime);
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param beginDate 较小的时间
     * @param endDate   较大的时间
     * @return 相差天数
     */

    public static int getTimeDistance(Date beginDate, Date endDate) {
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(beginDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        long beginTime = beginCalendar.getTime().getTime();
        long endTime = endCalendar.getTime().getTime();
        int betweenDays = (int) ((endTime - beginTime) / (1000 * 60 * 60 * 24));//先算出两时间的毫秒数之差大于一天的天数

        endCalendar.add(Calendar.DAY_OF_MONTH, -betweenDays);//使endCalendar减去这些天数，将问题转换为两时间的毫秒数之差不足一天的情况
        endCalendar.add(Calendar.DAY_OF_MONTH, -1);//再使endCalendar减去1天
        if (beginCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH))//比较两日期的DAY_OF_MONTH是否相等
            return betweenDays + 1;    //相等说明确实跨天了
        else
            return betweenDays + 0;    //不相等说明确实未跨天
    }


    /**
     * 取得两时间相差的小时数
     *
     * @param from 第一个时间
     * @param to   第二个时间
     * @return 相差的小时数
     */
    public static long getBetweenHours(Date from, Date to) {
        long hours = 0;
        long hourTime = 60 * 60 * 1000;
        long fromTime = from.getTime();
        long toTime = to.getTime();
        long times = Math.abs(fromTime - toTime);
        hours = times / hourTime;
        return hours;
    }

    /**
     * @param endDate 结束时间
     * @param nowDate 当前时间
     * @return 相差分钟数
     */
    public static Long getBetweenMin(Date endDate, Date nowDate) {
        long minTime = 60 * 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少分钟
        long min = diff / minTime;
        return min;
    }

    /**
     * 取得在指定时间上加减days天后的时间
     *
     * @param date 指定的时间
     * @param days 天数,正为加，负为减
     * @return 在指定时间上加减days天后的时间
     */
    public static Date addDays(Date date, int days) {
        Date time = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        time = cal.getTime();
        return time;
    }

    /**
     * 取得在指定时间上加减months月后的时间
     *
     * @param date   指定时间
     * @param months 月数，正为加，负为减
     * @return 在指定时间上加减months月后的时间
     */
    public static Date addMonths(Date date, int months) {
        Date time = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        time = cal.getTime();
        return time;
    }

    /**
     * 取得在指定时间上加减years年后的时间
     *
     * @param date  指定时间
     * @param years 年数，正为加，负为减
     * @return 在指定时间上加减years年后的时间
     */
    public static Date addYears(Date date, int years) {
        Date time = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        time = cal.getTime();
        return time;
    }

    /**
     * 取得在指定时间上
     * 24 小时
     *
     * @param date  指定时间
     * @param hours 小时，正为加，负为减
     * @return 在指定时间上加减hour小时
     */
    public static Date addHours(Date date, int hours) {
        Date time = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        time = cal.getTime();
        return time;
    }

    public static Date addMinute(Date date, int minute) {
        Date time = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);
        time = cal.getTime();
        return time;
    }

    public static Date addSecond(Date date, int second) {
        Date time = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, second);
        time = cal.getTime();
        return time;
    }

    /**
     * 时间毫秒时间轴 转换 long
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static long toMillis(String dateStr) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(yyyyMMddHHmmssSSS);
        Date date = format.parse(dateStr);
        return date.getTime();
    }


    /**
     * 获取某个时刻的毫秒值
     *
     * @param clock 某个时刻
     * @return long值
     */
    public long getClockTimeMills(Integer clock) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, clock);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取某一天某个时刻的毫秒值
     *
     * @param clock
     * @return
     */
    public long getClockTimeMills(Integer clock, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, clock);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 字符串是否符合某格式的日期
     *
     * @param str
     * @param pattern
     * @return
     */
    public static boolean isDate(String str, String pattern) {
        if (StringUtils.isBlank(str) || StringUtils.isBlank(pattern) || str.length() != pattern.length()) {
            return false;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            df.parse(str);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 格式化时间 毫秒为单位
     *
     * @param ms 时间毫秒
     * @return 结果 字符串
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;
        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
        if (milliSecond > 0) {
            sb.append(milliSecond + "毫秒");
        }
        return sb.toString();
    }

    /**
     * 分钟、秒变为0
     *
     * @param timestamp
     * @param minute
     * @param second
     * @return
     */
    public static Date handleToZero(Long timestamp, int minute, int second) {
        Date time = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }

    /**
     * 获取当月从1号开始到当前日期，每一天的开始时间00:00:00,
     * @param date
     * @return
     */
    public static List<Date> getAllDayBeginTimeInCurrentMonth(Date date) {
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateBeginTime);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        List<Date> dates = new ArrayList<Date>();
        // 循环遍历每一个
        for (int i = 1; i <= dayOfMonth; i++) {
            Calendar day = Calendar.getInstance();
            day.set(Calendar.DAY_OF_MONTH, i);
            dates.add(day.getTime());
        }

        return dates;
    }

    /**
     * 获取当月从1号开始到当前日期，每一天的开始时间00:00:00,
     * @param date
     * @return
     */
    public static List<Date> getAllDayBeginTimeInCurrentMonthBeforeDays(Date date, int beforeDays) {
        date = DateUtil.addDays(date, -beforeDays);
        Date dateBeginTime = DateUtil.getDateBeginTime(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateBeginTime);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        List<Date> dates = new ArrayList<Date>();
        // 循环遍历每一个
        for (int i = 1; i <= dayOfMonth; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            dates.add(cal.getTime());
        }

        return dates;
    }

    /**
     * 获取当月从1号开始到当前日期，每一天的结束时间24:00:00,
     * @param date
     * @param beforeDays  当前日期往前的天数
     * @return
     */
    public static List<Date> getAllDayEndTimeInCurrentMonthBeforeDays(Date date, int beforeDays) {
        List<Date> allDayBeginTimeInCurrentMonth = getAllDayBeginTimeInCurrentMonthBeforeDays(date, beforeDays);
        List<Date> allDayEndTimeInCurrentMonth = allDayBeginTimeInCurrentMonth.stream()
                .map(e -> DateUtil.addDays(e, 1)).collect(Collectors.toList());

        return allDayEndTimeInCurrentMonth;
    }

    /**
     * 获取日期是当月的第几天
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定日期的整点时间
     * @return
     */
    public static Date getHourTimeByDateAndHourNumber(Date date, Integer hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    /**
     * 获取指定日期当月的指定天  时间设定为22点(武钢目前默认是22点到22点的查询策略)
     * @param date
     * @param dayOfMonth
     * @return
     */
    public static Date getDayTimeByDateAndDayNumber(Date date, Integer dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        int maxDateOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth <= maxDateOfCurrentMonth ? dayOfMonth : maxDateOfCurrentMonth);
        return calendar.getTime();
    }

    /**
     * 取指定日期当年的指定月 日期设定为月最后一天22点
     * @param date
     * @param monthOfYear
     * @return
     */
    public static Date getDayTimeByDateAndMonthNumber(Date date, Integer monthOfYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int maxMonthOfCurrentYear = calendar.getActualMaximum(Calendar.MONTH);
        calendar.set(Calendar.MONTH, monthOfYear - 1 <= maxMonthOfCurrentYear ? monthOfYear - 1 : maxMonthOfCurrentYear);
        return calendar.getTime();
    }



    /**
     * date1是否小于等于date2
     * @param date1
     * @param date2
     * @return
     */
    public static Boolean isDayBeforeOrEqualAnotherDay(Date date1, Date date2) {
        return date1.getTime() <= date2.getTime();
    }

    /**
     * 第一个date1是否处于date2和date3之间
     * @param date1
     * @param date2
     * @param date3
     * @return
     */
    public static Boolean isDayBetweenAnotherTwoDays(Date date1, Date date2, Date date3) {
        return date1.getTime() >= date2.getTime() && date1.getTime() <= date3.getTime();
    }
}