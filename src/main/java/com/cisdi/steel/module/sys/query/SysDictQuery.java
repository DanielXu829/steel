package com.cisdi.steel.module.sys.query;

import com.cisdi.steel.common.base.vo.SortQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>Description: 系统字典 查询参数 </p>
 * <P>Date: 2018-08-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SysDictQuery extends SortQuery implements Serializable {

    /**
     * 名称
     */
    private String name;
    /**
     * 父类编码
     */
    private String parentCode;

    /**
     * 编码
     */
    private String code;
}
