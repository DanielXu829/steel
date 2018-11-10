package com.cisdi.steel.module.job.strategy.options;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 将一段时间内的每天的数据经过计算形成一条记录
 * 每个小时时间段
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class DayOptionStrategy implements OptionsStrategy {
    @Override
    public List<DateQuery> execute(DateQuery dateQuery) {
        return DateQueryUtil.buildMonthDayEach(dateQuery.getRecordDate());
    }
}
