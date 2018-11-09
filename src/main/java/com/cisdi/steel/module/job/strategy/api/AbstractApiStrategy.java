package com.cisdi.steel.module.job.strategy.api;

import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.config.HttpProperties;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
public abstract class AbstractApiStrategy implements ApiStrategy {

    protected HttpUtil httpUtil;
    protected HttpProperties httpProperties;


    public AbstractApiStrategy(HttpUtil httpUtil, HttpProperties httpProperties) {
        this.httpUtil = httpUtil;
        this.httpProperties = httpProperties;
    }
}
