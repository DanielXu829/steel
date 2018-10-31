package com.cisdi.steel.common.base.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Description: 存储基础id 用户   </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng  2018/2/11
 * @version 1.0
 * @since 1.8
 */
@Data
public class BaseId implements Serializable {
    /**
     * 单个id
     */
    private Long id;

    /**
     * 多个id
     */
    private List<Long> ids;
}
