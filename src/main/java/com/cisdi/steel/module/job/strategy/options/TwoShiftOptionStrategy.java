package com.cisdi.steel.module.job.strategy.options;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 将一段时间内的每班(两班)的数据经过计算形成一条记录
 * <p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/11/26 </P>
 * @version 1.0
 */
@Component
public class TwoShiftOptionStrategy implements OptionsStrategy {

    public List<DateQuery> execute(DateQuery dateQuery) {
        List<DateQuery> dateQueries = DateQueryUtil.buildDay12HourEach(dateQuery.getRecordDate());
        return dateQueries;
    }

    @Override
    public String getKey() {
        return "twoshift";
    }
}
