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
     * 8高炉 8
     */
    private String urlApiGLTwo;

    /**
     * 7高炉 7
     */
    private String urlApiGLThree;

    /**
     * 67焦化的接口地址
     */
    private String urlApiJHOne;

    /**
     * 12焦化的接口地址
     */
    private String urlApiJHTwo;

    /**
     * 45焦化的接口地址
     */
    private String urlApiJHThree;

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
        }else if ("7.0".equals(version)) {
            return urlApiGLThree;
        }
        return urlApiGLOne;
    }

    public String getJHUrlVersion(String version) {
        if ("12.0".equals(version)) {
            return urlApiJHTwo;
        } else if ("67.0".equals(version)) {
            return urlApiJHOne;
        }else if ("45.0".equals(version)) {
            return urlApiJHThree;
        }
        return urlApiGLOne;
    }
}
