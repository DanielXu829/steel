package com.cisdi.steel.module.job.strategy.date;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

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
public class WeekStrategy implements DateStrategy {
    @Override
    public DateQuery handlerDate(Date date) {
        return DateQueryUtil.buildWeek(date);
    }

    @Override
    public String getKey() {
        return "week";
    }
}
