package com.cisdi.steel.module.job.strategy;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.strategy.api.*;
import com.cisdi.steel.module.job.strategy.date.DateStrategy;
import com.cisdi.steel.module.job.strategy.date.DayStrategy;
import com.cisdi.steel.module.job.strategy.date.MonthStrategy;
import com.cisdi.steel.module.job.strategy.options.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 策略工厂
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@SuppressWarnings("All")
public class StrategyFactory {

    private static final String SPLIT = "_";

    private static final HttpUtil HTTP_UTIL = ApplicationContextHolder.getBean(HttpUtil.class);
    private static final HttpProperties HTTP_PROPERTIES = ApplicationContextHolder.getBean(HttpProperties.class);

    private static final Map<String, DateStrategy> DATE_STRATEGY_MAP = new HashMap<>();
    private static final Map<String, OptionsStrategy> OPTIONS_STRATEGY_MAP = new HashMap<>();

    static {
        DATE_STRATEGY_MAP.put("day", ApplicationContextHolder.getBean(DayStrategy.class));
        DATE_STRATEGY_MAP.put("month", ApplicationContextHolder.getBean(MonthStrategy.class));

        OPTIONS_STRATEGY_MAP.put("all", ApplicationContextHolder.getBean(AllOptionStrategy.class));
        OPTIONS_STRATEGY_MAP.put("each", ApplicationContextHolder.getBean(EachOptionStrategy.class));
        OPTIONS_STRATEGY_MAP.put("month", ApplicationContextHolder.getBean(MonthOptionStrategy.class));
        OPTIONS_STRATEGY_MAP.put("day", ApplicationContextHolder.getBean(DayOptionStrategy.class));
        OPTIONS_STRATEGY_MAP.put("hour", ApplicationContextHolder.getBean(HourOptionStrategy.class));
    }

    /**
     * 获取对应的处理方式
     * 有一项为空 均不处理：说明不符合规则
     *
     * @param sheetName 指定名称
     * @return 结果 返回值为null 表示没有对应的处理 请跳过
     */
    public static ExecuteInfo getApiBySheetName(String sheetName) {
        String[] split = sheetName.split(SPLIT);
        if (split.length == 4) {
            // 第一个值 api
            String api = split[1];
            ApiStrategy apiStrategy = getApiStrategy(api);
            if (Objects.isNull(apiStrategy)) {
                return null;
            }
            // 第二个值 时间
            String time = split[2];
            DateStrategy dateStragegy = getDateStrategy(time);
            if (Objects.isNull(dateStragegy)) {
                return null;
            }
            // 第三个值 option
            String option = split[3];
            OptionsStrategy optionStartegy = getOptionStartegy(option);
            if (Objects.isNull(optionStartegy)) {
                return null;
            }
            return ExecuteInfo.builder()
                    .apiStrategy(apiStrategy)
                    .dateStrategy(dateStragegy)
                    .optionsStrategy(optionStartegy)
                    .build();
        }
        return null;
    }

    /**
     * 获取对应的api
     * 目前就tag
     *
     * @param method 方法
     * @return 结果
     */
    private static ApiStrategy getApiStrategy(String method) {
        if ("tag".equals(method)) {
            return new TagStrategy(HTTP_UTIL, HTTP_PROPERTIES);
        } else if ("acsReport".equals(method)) {
            return new AcsStrategy(HTTP_UTIL, HTTP_PROPERTIES);
        } else if ("charge".equals(method)) {
            return new ChargeStrategy(HTTP_UTIL, HTTP_PROPERTIES);
        } else if ("tap".equals(method)) {
            return new TapStrategy(HTTP_UTIL, HTTP_PROPERTIES);
        }

        return null;
    }

    /**
     * option处理
     *
     * @param option 名称
     * @return 结果
     */
    private static OptionsStrategy getOptionStartegy(String option) {
        return OPTIONS_STRATEGY_MAP.get(option);
    }

    /**
     * 获取对应的时间策略
     *
     * @param time 时间
     * @return 结果
     */
    private static DateStrategy getDateStrategy(String time) {
        return DATE_STRATEGY_MAP.get(time);
    }
}
