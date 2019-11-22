package com.cisdi.steel.module.job.strategy.date;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 构建当天开始时间和结束时间, 结束时间延迟20分钟
 * <p>Copyright: Copyright (c) 2019</p>
 * <P>Date: 2019/11/25 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class DayTwoStrategy implements DateStrategy {
    @Override
    public String getKey() {
        return "daytwo";
    }

    @Override
    public DateQuery handlerDate(Date date) {
        return DateQueryUtil.buildTodayDelayTwentyMin(date);
    }
}
