package com.cisdi.steel.module.job.strategy.options;

import com.cisdi.steel.module.job.util.date.DateQuery;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 将一段时间内的所有数据经过计算形成一条记录
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
public class AllOptionStrategy implements OptionsStrategy {
    @Override
    public List<DateQuery> execute(DateQuery dateQuery) {
        // TODO: 待处理
        return null;
    }
}
