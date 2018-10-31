package com.cisdi.steel.common.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.base.vo.DataTableQuery;
import com.cisdi.steel.common.base.vo.PageQuery;
import com.cisdi.steel.common.resp.ApiResult;

/**
 * <p>Description:  基础增删查改 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2018/1/6
 * @since 1.8
 */
public interface IBaseService<T> extends IService<T> {

    /**
     * 分页查询
     *
     * @return 返回结果
     */
    ApiResult pageList(PageQuery pageQuery);

    /**
     * DateTable 分页查询
     *
     * @param dt 参数
     * @return 返回结果
     */
    ApiResult pageList(DataTableQuery dt);


    /**
     * 保存或更新
     * @param record 数据
     * @return 结果
     */
    ApiResult saveRecord(T record);

    /**
     * 添加
     *
     * @param record 数据
     * @return 结果
     */
    ApiResult insertRecord(T record);

    /**
     * 更新
     *
     * @param record 数据
     * @return 结果
     */
    ApiResult updateRecord(T record);

    /**
     * 删除
     * 仅适用普通的单表删除
     * @param record 多个ID
     * @return 返回结果
     */
    ApiResult deleteRecord(BaseId record);

    /**
     * 通过id查询结果
     * 不建议使用 推荐查询指定的列 默认会查询所有的列
     *
     * @param id id
     * @return 返回结果
     */
    ApiResult getById(Long id);
}
