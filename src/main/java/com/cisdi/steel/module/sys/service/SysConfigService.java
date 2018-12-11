package com.cisdi.steel.module.sys.service;

import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.module.sys.entity.SysConfig;
import com.cisdi.steel.common.base.service.IBaseService;

/**
 * <p>Description: 配置 服务类 </p>
 * <P>Date: 2018-10-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface SysConfigService extends IBaseService<SysConfig> {
    /**
     * 通过编码查询 当前名称
     *
     * @param code 编码
     * @return 结果
     */
    String selectNameByCode(String code);

    /**
     * 通过编码查询 当前执行的值
     *
     * @param code 编码
     * @return 结果
     */
    String selectActionByCode(String code);

    /**
     * 查询系统参数
     *
     * @return 结果
     */
    ApiResult systemParam();
}
