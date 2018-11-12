package com.cisdi.steel.module.job.strategy.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * api管理
 *
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ApiStrategyContext {

    private final Map<String, ApiStrategy> apiStrategyMap = new ConcurrentHashMap<>();

    @Autowired
    public ApiStrategyContext(Map<String, ApiStrategy> strategyMap) {
        this.apiStrategyMap.clear();
        strategyMap.forEach((k, v) -> this.apiStrategyMap.put(v.getKey(), v));
    }

    public ApiStrategy getApiStrategy(String api) {
        return this.apiStrategyMap.get(api);
    }
}
