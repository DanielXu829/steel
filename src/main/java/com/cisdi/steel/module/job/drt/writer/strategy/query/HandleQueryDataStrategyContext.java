package com.cisdi.steel.module.job.drt.writer.strategy.query;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HandleQueryDataStrategyContext {
    private final Map<String, HandleQueryDataStrategy> handleQueryDataStrategyMap = new ConcurrentHashMap<>();

    public HandleQueryDataStrategyContext(Map<String, HandleQueryDataStrategy> strategyMap) {
        this.handleQueryDataStrategyMap.clear();
        strategyMap.forEach((k, v) -> handleQueryDataStrategyMap.put(v.getKey(), v));
    }
    public HandleQueryDataStrategy getHandleQueryDataStrategy(String sequenceCode){
        return this.handleQueryDataStrategyMap.get(sequenceCode);
    }

}
