package com.cisdi.steel.module.job.strategy.options;

import com.cisdi.steel.module.job.strategy.Key;
import com.cisdi.steel.module.job.util.date.DateQuery;

import java.util.List;

/**
 * options 处理
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface OptionsStrategy extends Key {

    /**
     * 把时间处理成对应的 格式
     *
     * @param dateQuery 查询条件
     * @return 结果
     */
    List<DateQuery> execute(DateQuery dateQuery);
}
