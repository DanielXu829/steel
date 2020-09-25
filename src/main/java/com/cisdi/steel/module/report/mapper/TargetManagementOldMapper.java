package com.cisdi.steel.module.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cisdi.steel.module.report.entity.TargetManagement;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>Description: 指标管理 MapperOld 接口 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020-09-25 </P>
 *
 * @version 1.0
 */
@Mapper
public interface TargetManagementOldMapper extends BaseMapper<TargetManagement> {

    /**
     * 通过别名查询tag点
     * @param targetNames
     * @return 别名对应的tag点集合
     */
    List<String> selectTargetFormulasByTargetNames(List<String> targetNames);

    /**
     * 通过一个别名查询一个tag点
     * @param targetName
     * @return 别名对应的tag点
     */
    String selectTargetFormulaByTargetName(String targetName);

    /**
     * 通过一个别名查询target
     * @param targetName
     * @return 别名对应的target对象
     */
    TargetManagement selectTargetByTargetName(String targetName);

    /**
     * 模糊查询
     * @param condition
     * @return
     */
    List<TargetManagement> selectTargetManagementByCondition(@Param("condition") String condition);

    /**
     * 查询所有的tag点
     * @return Map<id, TargetManagement>
     */
    @MapKey("id")
    Map<Long, TargetManagement> selectAllTargetManagement();

    /**
     * 通过别名查询tag点
     * @param targetNames
     * @return 别名对应的tag点集合
     */
    List<TargetManagement> selectTargetManagementsByTargetNames(List<String> targetNames);

    List<TargetManagement> listByIds(List<Long> ids);
}
