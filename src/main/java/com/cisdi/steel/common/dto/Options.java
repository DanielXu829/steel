package com.cisdi.steel.common.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Description:  选项 标签名-值  </p>
 * <p>email: ypasdf@163.com </p>
 * <p>Copyright: Copyright (c) 2017 </p>
 *
 * @author yangpeng  2018/2/12
 * @version 1.0
 * @since 1.8
 */
@Data
public class Options<T> {

    /**
     * 标签
     */
    private String label;
    /**
     * 值
     */
    private T value;

    /**
     * 多个子集
     */
    private List<Options<T>> children;

}