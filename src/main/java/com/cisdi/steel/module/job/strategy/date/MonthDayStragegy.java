package com.cisdi.steel.module.job.strategy.date;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 构建每月 每天 查询参数
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class MonthDayStragegy implements DateStragegy {
    @Override
    public List<DateQuery> execute(Date date) {
        return DateQueryUtil.buildMonthDayEach(date);
    }
}
