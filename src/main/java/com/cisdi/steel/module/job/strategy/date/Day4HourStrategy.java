package com.cisdi.steel.module.job.strategy.date;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * 构建每四小时的时间段
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class Day4HourStrategy implements DateStrategy {
    @Override
    public String getKey() {
        return "4hour";
    }

    @Override
    public DateQuery handlerDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int size = hour / 4;
        if (hour % 4 != 0) {
            size++;
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);

        calendar1.set(Calendar.HOUR_OF_DAY, size * 4);
        Date startTime = calendar1.getTime();
        calendar1.set(Calendar.HOUR_OF_DAY, (size + 1) * 4);
        Date endTime = calendar1.getTime();
        return new DateQuery(startTime, endTime, endTime);
    }
}
