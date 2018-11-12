package com.cisdi.steel.module.job.strategy.date;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 构建当月
 * <p>
 * 开始时间和结束时间
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class MonthStrategy implements DateStrategy {
    @Override
    public String getKey() {
        return "month";
    }

    @Override
    public DateQuery handlerDate(Date date) {
        return DateQueryUtil.buildMonth(date);
    }
}
