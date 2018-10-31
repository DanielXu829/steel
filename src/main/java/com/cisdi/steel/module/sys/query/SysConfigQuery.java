package com.cisdi.steel.module.sys.query;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import com.cisdi.steel.common.base.vo.PageQuery;

/**
 * <p>Description: 配置 查询参数</p>
 * <P>Date: 2018-10-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SysConfigQuery extends PageQuery implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 编码
     */
    private String code;

}
