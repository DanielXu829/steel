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
     * 6高炉 6
     */
    private String urlApiGLOne;

    /**
     * 6高炉 8
     */
    private String urlApiGLTwo;

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


    public String getGlUrlVersion(String version) {
        if ("6.0".equals(version)) {
            return urlApiGLOne;
        } else if ("8.0".equals(version)) {
            return urlApiGLTwo;
        }
        return urlApiGLOne;
    }

}
