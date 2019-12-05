package com.cisdi.steel.module.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cisdi.steel.module.report.entity.TargetManagement;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>Description: 指标管理 Mapper 接口 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019-11-05 </P>
 *
 * @version 1.0
 */
@Mapper
public interface TargetManagementMapper extends BaseMapper<TargetManagement> {

    /**
     * 通过别名查询tag点
     * @param targetNames
     * @return 别名对应的tag点集合
     */
    List<String> selectTargetFormulasByTargetNames(List<String> targetNames);

    /**
     * 模糊查询
     * @param condition
     * @return
     */
    List<TargetManagement> selectTargetManagementByCondition(@Param("condition") String condition);
}
