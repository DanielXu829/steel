package com.cisdi.steel.module.quartz.util;

import org.quartz.CronExpression;
import org.quartz.CronTrigger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * <p>Description: quartz工具类 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author leaf
 * @version 1.0
 * @since 2018/7/10
 */
public class QuartzUtil {

    private static final SimpleDateFormat FORMAT =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 默认运行的次数 至少
     */
    private static final int DEFAULT_COUNT = 5;


    /**
     * 获取 最近的表达式
     * @param cron cron表达式
     * @param count 运行多少次
     * @return 结果 null 表示运行失败
     */
    public static List<String> getCronSchedule(String cron,Integer count){
        if(!CronExpression.isValidExpression(cron)){
            return null;
        }
        if(Objects.isNull(count) || count < DEFAULT_COUNT){
            count = DEFAULT_COUNT;
        }
        List<String> result = new ArrayList<>(count);
        try {
            CronTrigger trigger = newTrigger().withIdentity("Calculate Date").withSchedule(cronSchedule(cron)).build();
            Date dateTime = trigger.getStartTime();
            result.add(FORMAT.format(dateTime));
            for (int i = 1; i < count; i++) {
                dateTime = trigger.getFireTimeAfter(dateTime);
                result.add(FORMAT.format(dateTime));
            }
        } catch (Exception e) {
            return null;
        }
        return result;
    }


    /***
     * 将时间转换为对应的cron表达式
     * @param date 传入的时间参数
     * @return cron表达式
     */
    public static String getCron(Date date){
        String dateFormat="ss mm HH dd MM ? yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

}
