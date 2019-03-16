package com.cisdi.steel.module.job.strategy.options;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 将一段时间按周分
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class WeekOptionStrategy implements OptionsStrategy {
    @Override
    public List<DateQuery> execute(DateQuery dateQuery) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getRecordDate());
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        List<DateQuery> list = new ArrayList<>();
        for (int i = week - 1; i >= 0; i--) {
            Calendar instance = Calendar.getInstance();
            instance.setTime(dateQuery.getRecordDate());
            instance.add(Calendar.DATE, -(i * 7));
            DateQuery dateQuery1 = DateQueryUtil.buildWeek(instance.getTime());
            list.add(dateQuery1);
        }
        return list;
    }

    @Override
    public String getKey() {
        return "week";
    }
}
