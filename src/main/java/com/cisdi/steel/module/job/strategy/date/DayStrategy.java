package com.cisdi.steel.module.job.strategy.date;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 构建当天开始时间和结束时间
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class DayStrategy implements DateStrategy {
    @Override
    public String getKey() {
        return "day";
    }

    @Override
    public DateQuery handlerDate(Date date) {
//        Date da=DateUtil.strToDate("2018-12-10 23:10:00","yyyy-MM-dd HH:mm:ss");
        return DateQueryUtil.buildToday(date);
    }
}
