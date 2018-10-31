
package com.cisdi.steel.common.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>Description: 基础数据实体抽象类 </p>
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
public abstract class AbstractDataEntity<T extends Model> extends BaseEntity<T> {

    private static final long serialVersionUID = 1L;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    protected LocalDateTime createTime;
    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    protected LocalDateTime updateTime;

    /**
     * 创建人 id
     */
    @TableField(value = "create_id")
    protected Long createId;
    /**
     * 更新人 id
     */
    @TableField(value = "update_id")
    protected Long updateId;

    /**
     * 备注
     */
    protected String remark;

}
