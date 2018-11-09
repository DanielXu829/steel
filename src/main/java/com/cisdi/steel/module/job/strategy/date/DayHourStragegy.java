package com.cisdi.steel.module.job.strategy.date;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 构建每天的时间 每小时
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class DayHourStragegy implements DateStragegy {
    @Override
    public List<DateQuery> execute(Date date) {
        return DateQueryUtil.buildDayHourEach(date);
    }
}
