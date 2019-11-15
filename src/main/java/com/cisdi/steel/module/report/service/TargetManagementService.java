package com.cisdi.steel.module.report.service;

import com.cisdi.steel.common.base.service.IBaseService;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.module.report.entity.TargetManagement;

import java.util.List;

/**
 * <p>Description: tag点别名 服务类 </p>
 * <P>Date: 2019-11-14 </P>
 *
 * @version 1.0
 */
public interface TargetManagementService extends IBaseService<TargetManagement> {

    /**
     * 查询所有模板节点
     *
     * @return 所有模板节点
     */
    ApiResult<List<TargetManagement>> selectAllTargetManagement(TargetManagement record);

    /**
     * 删除该节点，以及该几点下面所有的子节点
     *
     * @return 所有模板节点
     */
    ApiResult deleteCurrentTarget(TargetManagement record);
}
