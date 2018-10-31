package com.cisdi.steel.common.properties;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 系统配置文件 以后会动态改变 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author common
 * @version 1.0
 * @date 2018/5/5
 */
@Data
@Component
public class SysProperties {

    /**
     * 存储在用户token redis的过期时间 秒
     * 默认2个小时
     */
    private long tokenExpire = 7200;

    /**
     * 权限存在时间 默认4个小时
     */
    private long permissionExpire = 14400;

    /**
     * 是否开始权限验证 默认1开启
     */
    private int openPermission = 1;

    /**
     * 是否单点登录
     */
    private Boolean singlePoint = true;
}
