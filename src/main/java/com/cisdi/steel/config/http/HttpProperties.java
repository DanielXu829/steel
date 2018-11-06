package com.cisdi.steel.config.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
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

}
