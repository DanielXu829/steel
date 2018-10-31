package com.cisdi.steel.common.base.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>Description:  通用查询条件 分页数据  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2018/1/6
 * @since 1.8
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataTableQuery extends SortQuery implements Serializable {
    private static final long serialVersionUID = 2252240868205663450L;

    /**
     * 搜索条件
     */
    private Map<String, Object> searchParams;
}
