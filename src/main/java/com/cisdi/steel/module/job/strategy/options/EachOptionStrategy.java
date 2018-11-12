package com.cisdi.steel.module.job.strategy.options;

import com.cisdi.steel.module.job.util.date.DateQuery;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Each 处理
 * 一个时间段
 * 将一段时间内的每一条数据形成一条记录
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class EachOptionStrategy implements OptionsStrategy {
    @Override
    public List<DateQuery> execute(DateQuery dateQuery) {
        List<DateQuery> dateQueries = new ArrayList<>();
        dateQueries.add(dateQuery);
        return dateQueries;
    }

    @Override
    public String getKey() {
        return "each";
    }
}
