package com.cisdi.steel.common.base.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>Description: 分页参数  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author common
 * @version 1.0
 * @date 2018/4/12
 */
@Data
public class PageQuery implements Serializable {
    /**
     * 默认页码
     */
    private int currentPage = 1;

    /**
     * 每页显示条数，默认 10
     */
    private int pageSize = 10;

}
