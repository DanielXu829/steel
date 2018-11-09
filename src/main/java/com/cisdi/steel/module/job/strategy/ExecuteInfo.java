package com.cisdi.steel.module.job.strategy;

import com.cisdi.steel.module.job.strategy.api.ApiStrategy;
import com.cisdi.steel.module.job.strategy.date.DateStragegy;
import lombok.Builder;
import lombok.Data;

/**
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
     * 执行的策略
     */
    private ApiStrategy apiStrategy;

    /**
     * 时间
     */
    private DateStragegy dateStragegy;
}
