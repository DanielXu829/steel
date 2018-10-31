package com.cisdi.steel.common.enums;

/**
 * <p>Description:  常用列名 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng  2018/2/11
 * @version 1.0
 * @since 1.8
 */
public enum ColumnEnum {
    /**
     * id 字段
     */
    ID("id"),
    /**
     * 删除字段
     */
    DEL_FLAG("del_flag"),
    /**
     * 名称 字段
     */
    NAME("name"),
    /**
     * 编码 字段
     */
    CODE("code"),

    /**
     * 排序字段 常用的列
     */
    SORT("sort"),
    /**
     * 创建时间 字段
     */
    CREATE_TIME("create_time"),
    /**
     * 更新时间 字段
     */
    UPDATE_TIME("update_time"),

    /**
     * 列名 是否禁止 0 启用 1禁止
     */
    FORBID("forbid"),

    /**
     * 父类的id
     */
    PARENT_ID("parent_id");

    /**
     * 列
     */
    private String column;

    ColumnEnum(String column){
        this.column = column;
    }

    public String getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return this.column;
    }
}
