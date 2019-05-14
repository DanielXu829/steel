package com.cisdi.steel.module.job.strategy.options;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 将一段时间内的每班的数据经过计算形成一条记录
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShiftOptionStrategy implements OptionsStrategy {
    @Override
    public List<DateQuery> execute(DateQuery dateQuery) {
        List<DateQuery> dateQueries = DateQueryUtil.buildDay8HourEach(dateQuery.getRecordDate());

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(dateQuery.getRecordDate());
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int size = hour / 8;
//        if (hour % 8 != 0) {
//            size++;
//        }
//        List<DateQuery> result = new ArrayList<>();
//        for (int i = 0; i < size; i++) {
//            Calendar calendar1 = Calendar.getInstance();
//            calendar1.setTime(dateQuery.getRecordDate());
//            calendar1.set(Calendar.MINUTE, 0);
//            calendar1.set(Calendar.SECOND, 0);
//
//            calendar1.set(Calendar.HOUR_OF_DAY, i * 8);
//            Date startTime = calendar1.getTime();
//            calendar1.set(Calendar.HOUR_OF_DAY, (i + 1) * 8);
//            Date endTime = calendar1.getTime();
//            result.add(new DateQuery(startTime, endTime, endTime));
//        }

        return dateQueries;
    }

    @Override
    public String getKey() {
        return "shift";
    }
}
