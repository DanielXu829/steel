package com.cisdi.steel.module.job.util.date;

import com.cisdi.steel.common.util.DateUtil;

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

    public static DateQuery buildMonth() {
        return buildMonth(new Date());
    }

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
