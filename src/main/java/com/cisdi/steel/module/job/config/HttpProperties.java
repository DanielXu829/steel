package com.cisdi.steel.module.job.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 请求参数配置文件
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = "http.url")
@Data
public class HttpProperties {
    /**
     * 高炉的接口地址
     */
    private String urlApiGLOne;

    /**
     * 焦化的接口地址
     */
    private String urlApiJHOne;
    /**
     * 能介的接口地址
     */
    private String urlApiNJOne;

    /**
     * 原供料的接口地址
     */
    private String urlApiYGLOne;

    /**
     * 5号烧结
     */
    private String urlApiSJOne;

    /**
     * 6号烧结
     */
    private String urlApiSJTwo;

}
