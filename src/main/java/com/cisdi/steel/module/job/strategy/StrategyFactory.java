package com.cisdi.steel.module.job.strategy;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.strategy.api.AcsStragegy;
import com.cisdi.steel.module.job.strategy.api.ApiStrategy;
import com.cisdi.steel.module.job.strategy.api.TagStrategy;
import com.cisdi.steel.module.job.strategy.date.DateStragegy;
import com.cisdi.steel.module.job.strategy.date.DayHourStragegy;
import com.cisdi.steel.module.job.strategy.date.MonthDayStragegy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
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

    private static final HttpUtil httpUtil = ApplicationContextHolder.getBean(HttpUtil.class);
    private static final HttpProperties httpProperties = ApplicationContextHolder.getBean(HttpProperties.class);

    private static final Map<String, DateStragegy> maps = new HashMap<>();

    static {
        maps.put("day", ApplicationContextHolder.getBean(DayHourStragegy.class));
        maps.put("month", ApplicationContextHolder.getBean(MonthDayStragegy.class));
    }

    /**
     * 获取对应的处理方式
     *
     * @param sheetName 指定名称
     * @return 结果 返回值为null 表示没有对应的处理 请跳过
     */
    public static ExecuteInfo getApiBySheetName(String sheetName) {
        String[] split = sheetName.split(SPLIT);
        if (split.length == 4) {
            // 第一个值
            String method = split[1];
            ApiStrategy api = getApi(method);
            if (Objects.isNull(api)) {
                return null;
            }
            // 第二个值
            String time = split[2];
            DateStragegy dateStragegy = getTime(time);
            if (Objects.isNull(dateStragegy)) {
                return null;
            }
            return ExecuteInfo.builder().apiStrategy(api)
                    .dateStragegy(dateStragegy)
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
    private static ApiStrategy getApi(String method) {
        if ("tag".equals(method)) {
            return new TagStrategy(httpUtil, httpProperties);
        }else if("acsReport".equals(method)){
            return new AcsStragegy(httpUtil, httpProperties);
        }

        return null;
    }

    /**
     * 获取对应的时间策略
     *
     * @param time 时间
     * @return 结果
     */
    private static DateStragegy getTime(String time) {
        return maps.get(time);
    }
}
