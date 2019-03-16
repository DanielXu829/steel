package com.cisdi.steel.module.job.strategy.options;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class OptionsStrategyContext {
    private final Map<String, OptionsStrategy> optionsStrategyMap = new ConcurrentHashMap<>();


    @Autowired
    public OptionsStrategyContext(Map<String, OptionsStrategy> strategyMap) {
        this.optionsStrategyMap.clear();
        strategyMap.forEach((k, v) -> this.optionsStrategyMap.put(v.getKey(), v));
    }

    /**
     * 获取对应的策略
     *
     * @param option 参数
     * @return 策略
     */
    public OptionsStrategy getOptionStrategy(String option) {
        return optionsStrategyMap.get(option);
    }

}
