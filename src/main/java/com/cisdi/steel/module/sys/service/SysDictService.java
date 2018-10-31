package com.cisdi.steel.module.sys.service;


import com.cisdi.steel.common.base.service.IBaseService;
import com.cisdi.steel.common.resp.ApiPageResult;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.module.sys.entity.SysDict;
import com.cisdi.steel.module.sys.query.SysDictQuery;

import java.util.List;
import java.util.Map;

/**
 * <p>Description: 系统字典 服务类 </p>
 * <P>Date: 2018-08-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface SysDictService extends IBaseService<SysDict> {

    /**
     * 通过父类编码获取子选项
     * label - value
     *
     * @param parentCode 父类编码
     * @return 结果 子类
     */
    ApiResult getOptions(String parentCode);


    /**
     * 查询所有父类
     *
     * @return 结果
     */
    List<Map<String, Object>> selectTreeList();


    /**
     * 分页查询结果
     *
     * @param sysDictQuery 参数参数
     * @return 结果
     */
    ApiPageResult pageList(SysDictQuery sysDictQuery);
}
