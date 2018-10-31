package com.cisdi.steel.common.base.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * <p>Description: 基层树形结构实体类   </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author common
 * @date 2018/3/24
 * @version 1.0
 *
 */
@Data
public class BaseTreeObj implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private String id;

    /**
     * 父类id
     */
    private String parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 子数据
     */
    private List<BaseTreeObj> childList = new ArrayList<>();

    /**
     * 是否打开 默认否
     */
    private Boolean opened = false;

    /**
     * 是否禁止勾选
     */
    private Boolean chkDisabled = false;

    /**
     * 是否选中 默认false
     */
    private Boolean checked = false;

}