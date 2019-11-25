package com.cisdi.steel.module.job.strategy.date;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 构建当天开始时间和结束时间，延迟第二天5分钟
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/11/25 </P>
 *
 * @version 1.0
 */
@Component
public class DayFiveDelayStrategy implements DateStrategy {

    @Override
    public String getKey() {
        return "dayone";
    }

    @Override
    public DateQuery handlerDate(Date date) {
        return DateQueryUtil.buildTodayDelayFiveMinute(date);
    }

}
