package com.cisdi.steel.module.job.strategy;

import com.cisdi.steel.module.job.strategy.api.ApiStrategy;
import com.cisdi.steel.module.job.strategy.api.ApiStrategyContext;
import com.cisdi.steel.module.job.strategy.date.DateStrategy;
import com.cisdi.steel.module.job.strategy.date.DateStrategyContext;
import com.cisdi.steel.module.job.strategy.options.OptionsStrategy;
import com.cisdi.steel.module.job.strategy.options.OptionsStrategyContext;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
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
@Component
public class StrategyContext {

    private static final String SPLIT = "_";

    @Autowired
    private ApiStrategyContext apiStrategyContext;

    @Autowired
    private DateStrategyContext dateStrategyContext;

    @Autowired
    private OptionsStrategyContext optionsStrategyContext;

    /**
     * 获取对应的处理方式
     * 有一项为空 均不处理：说明不符合规则没有对应的处理方式
     *
     * @param sheetName 指定名称
     * @return 结果 返回值为null 表示没有对应的处理 请跳过
     */
    public ExecuteInfo getApiBySheetName(String sheetName) {
        String[] split = sheetName.split(SPLIT);
        if (split.length == 4) {
            // 第一个值 api
            String api = split[1];
            ApiStrategy apiStrategy = apiStrategyContext.getApiStrategy(api);
            if (Objects.isNull(apiStrategy)) {
                return null;
            }
            // 第二个值 时间
            String time = split[2];
            DateStrategy dateStragegy = dateStrategyContext.getDateStrategy(time);
            if (Objects.isNull(dateStragegy)) {
                return null;
            }
            // 第三个值 option
            String option = split[3];
            OptionsStrategy optionStartegy = optionsStrategyContext.getOptionStrategy(option);
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


    public ApiStrategy getApi(String api) {
        return apiStrategyContext.getApiStrategy(api);
    }

    public DateStrategy getDate(String date) {
        return dateStrategyContext.getDateStrategy(date);
    }

    public OptionsStrategy getOption(String option) {
        return optionsStrategyContext.getOptionStrategy(option);
    }


    public List<DateQuery> handlerStrategy(String strategyName, Date date) {
        String[] split = strategyName.split("_");
        DateStrategy dateStrategy = getDate(split[2]);
        DateQuery handlerDate = dateStrategy.handlerDate(date);
        OptionsStrategy option = getOption(split[3]);
        List<DateQuery> dateQueries = option.execute(handlerDate);
        return dateQueries;
    }
}
