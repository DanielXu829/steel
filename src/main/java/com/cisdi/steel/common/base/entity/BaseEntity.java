package com.cisdi.steel.common.base.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * <p>Description:  基础数据实体抽象类 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2018/1/6
 * @since 1.8
 */
public abstract class BaseEntity<T extends Model> extends Model<T>  {

    /**
     * 实体编号（唯一标识）
     */
    protected Long id;

    public BaseEntity() {

    }
    public BaseEntity(Long id) {
        this();
        this.id = id;
    }

    @JsonSerialize(using=ToStringSerializer.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        BaseEntity<?> that = (BaseEntity<?>) obj;
        return null != this.getId() && this.getId().equals(that.getId());
    }

}
