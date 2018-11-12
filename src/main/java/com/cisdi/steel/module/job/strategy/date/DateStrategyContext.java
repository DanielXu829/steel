package com.cisdi.steel.module.job.strategy.date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class DateStrategyContext {
    private final Map<String, DateStrategy> dateStrategyMap = new ConcurrentHashMap<>();


    @Autowired
    public DateStrategyContext(Map<String, DateStrategy> strategyMap) {
        this.dateStrategyMap.clear();
        strategyMap.forEach((k, v) -> dateStrategyMap.put(v.getKey(), v));
    }


    public DateStrategy getDateStrategy(String date){
        return this.dateStrategyMap.get(date);
    }
}
