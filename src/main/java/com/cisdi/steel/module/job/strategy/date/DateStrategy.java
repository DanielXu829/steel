package com.cisdi.steel.module.job.strategy.date;

import com.cisdi.steel.module.job.util.date.DateQuery;

import java.util.Date;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface DateStrategy {
    /**
     * 结果
     *
     * @param date 时间
     * @return 处理后的时间
     */
    DateQuery handlerDate(Date date);
}
