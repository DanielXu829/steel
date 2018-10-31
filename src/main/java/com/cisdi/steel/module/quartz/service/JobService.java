package com.cisdi.steel.module.quartz.service;

import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.module.quartz.query.QuartzEntityQuery;

/**
 * <p>Description:  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author leaf
 * @version 1.0
 * @since 2018/7/9
 */
public interface JobService {

    /**
     * 结果
     *
     * @param query 查询参数
     * @return 获取结果列表
     */
    ApiResult listQuartzEntity(QuartzEntityQuery query);

    /**
     * 查询当前所有分组
     *
     * @return 名称
     */
    ApiResult selectAllGroupName();

}
