package com.cisdi.steel.module.report.util;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.enums.ReportTemplateTypeEnum;

import java.util.Date;

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
        switch (templateTypeEnum) {
            case report_hour:
                return DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd_HH");
            case report_four_hour:
                return DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd_HH");
            case report_class:
                return DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd_HH");
            case report_day:
                return DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy-MM-dd_HH");
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
}
