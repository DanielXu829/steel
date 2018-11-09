package com.cisdi.steel.common.base.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * <p>Description: 带有排序的 查询参数 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: ${date}</P>
 *
 * @author yangpeng
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SortQuery extends PageQuery {
    /**
     * 排序 条件 key 字段 cellValue:desc或asc
     */
    private Map<String, String> sorts;
}
