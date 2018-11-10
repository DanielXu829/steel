package com.cisdi.steel.module.job.strategy;

import com.cisdi.steel.module.job.strategy.api.ApiStrategy;
import com.cisdi.steel.module.job.strategy.date.DateStrategy;
import com.cisdi.steel.module.job.strategy.options.OptionsStrategy;
import lombok.Builder;
import lombok.Data;

/**
 *
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@Builder(toBuilder = true)
public class ExecuteInfo {
    /**
     * api处理
     */
    private ApiStrategy apiStrategy;

    /**
     * 时间处理
     */
    private DateStrategy dateStrategy;

    /**
     * option处理
     */
    private OptionsStrategy optionsStrategy;
}
