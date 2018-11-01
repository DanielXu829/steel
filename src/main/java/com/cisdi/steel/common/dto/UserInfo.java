package com.cisdi.steel.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Description: 用户信息  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2018/1/6
 * @since 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -1373760761780840081L;
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 设备号 主要是app端
     */
    private String device;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 构建用户信息
     *
     * @param id     用户id
     * @param name   用户名称
     * @param device 设备号
     * @return 信息
     */
    public static UserInfo build(Long id,String name,Date date, String device) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setDevice(device);
        userInfo.setName(name);
        userInfo.setCreateTime(date);
        return userInfo;
    }
}
