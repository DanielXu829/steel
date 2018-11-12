package com.cisdi.steel.module.job.strategy.date;

import com.cisdi.steel.module.job.util.date.DateQuery;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShiftStrategy implements DateStrategy {
    @Override
    public DateQuery handlerDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int size = hour / 8;
        if (hour % 8 != 0) {
            size++;
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);

        calendar1.set(Calendar.HOUR_OF_DAY, size * 8);
        Date startTime = calendar1.getTime();
        calendar1.set(Calendar.HOUR_OF_DAY, (size + 1) * 8);
        Date endTime = calendar1.getTime();
        return new DateQuery(startTime, endTime, endTime);
    }

    @Override
    public String getKey() {
        return "shift";
    }
}
