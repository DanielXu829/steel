package com.cisdi.steel.module.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>Description: 报表动态模板 - 参数列表 实体类 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReportTemplateTags extends Model<ReportTemplateTags> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 对应report_template_sheet中的id
     */
    private Long templateSheetId;

    /**
     * 外键，对应target_management中的主键
     */
    private Long targetId;

    /**
     * 参数排序，定义了excel中的顺序
     */
    private Integer sequence;

    private Long topParentId;

    /**
     * tag点时间后缀
     */
    private String tagTimeSuffix;

    /**
     * tag点计算后缀
     */
    private String tagCalSuffix;

    /**
     * tag点小数点位数
     */
    private Integer decimalScale;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
