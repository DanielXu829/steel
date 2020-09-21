package com.cisdi.steel.module.job.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>Description:  执行任务的的一些参数    </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = "job")
@Data
public class JobProperties {
    /**
     * 存储的文件路径
     */
    private String filePath;

    /**
     * 模板存储的路径
     */
    private String templatePath;

    /**
     * 临时存储文件目录
     */
    private String tempPath;

    /**
     * 空模板路径
     */
    private String emptyPath;

    /**
     * 临时excel图片存储目录
     */
    private String tempImagePath;

}
