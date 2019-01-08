package com.cisdi.steel.module.report.util;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * <p>Description:  文件名称处理的工作类 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/23 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class FileNameHandlerUtil {

    /**
     * 获取不同报表类型的 日志时间展示
     *
     * @param templateTypeEnum 类型
     * @return 结果
     */
    public static String handlerName(ReportTemplateTypeEnum templateTypeEnum, DateQuery dateQuery) {
        boolean flag = false;
        if (Objects.nonNull(dateQuery.getOldDate())) {
            String dateTime = DateUtil.getFormatDateTime(dateQuery.getOldDate(), "HH");
            if ("00".equals(dateTime)) {
                flag = true;
            }
        }

        if (Objects.nonNull(dateQuery.getDelay()) && !dateQuery.getDelay()) {
            // 延迟时间生成，表明是手动生成
            String time = DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd");
            return time + "_24";
        }

        switch (templateTypeEnum) {
            case report_hour:
                return hourName(dateQuery.getRecordDate(), flag);
            case report_four_hour:
                return hourName(dateQuery.getRecordDate(), flag);
            case report_class:
                return hourName(dateQuery.getRecordDate(), flag);
            case report_day:
                return hourName(dateQuery.getRecordDate(), flag);
            case report_week:
                return DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd");
            case report_month:
                return DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd");
            case report_year:
                return DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd");
            default:
                return "";
        }
    }

    private static String hourName(Date date, boolean flag) {
        if (flag) {
            String time = DateUtil.getFormatDateTime(date, "yyyy-MM-dd");
            return time + "_24";
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            String dateTime = DateUtil.getFormatDateTime(calendar.getTime(), "HH");
            if ("00".equals(dateTime)) {
                String time = DateUtil.getFormatDateTime(date, "yyyy-MM-dd");
                return time + "_24";
            }
            return DateUtil.getFormatDateTime(calendar.getTime(), "yyyy-MM-dd_HH");
        }

    }
}
