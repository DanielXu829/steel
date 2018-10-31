package com.cisdi.steel.common.base.controller;

import com.cisdi.steel.common.base.service.IBaseService;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.base.vo.DataTableQuery;
import com.cisdi.steel.common.resp.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>Description: 基础controller  根据需要继承 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2018/1/4
 * @since 1.8
 */
@SuppressWarnings("ALL")
public abstract class BaseController<M extends IBaseService<T>,T> {

    /**
     * 基础的service类
     */
    @Autowired
    protected  M baseService;


    @PostMapping(value = "/pageList")
    public ApiResult pageList(@RequestBody DataTableQuery dataTableParam) {
        return baseService.pageList(dataTableParam);
    }

    @PostMapping(value = "/insert")
    public ApiResult insertRecord(@RequestBody T record) {
        return baseService.insertRecord(record);
    }

    @PostMapping(value = "/update")
    public ApiResult updateRecord(@RequestBody T record) {
        return baseService.updateRecord(record);
    }

    @PostMapping(value = "/query")
    public ApiResult queryRecord(@RequestBody BaseId record) {
        return baseService.getById(record.getId());
    }

    @PostMapping(value = "/delete")
    public ApiResult deleteById(@RequestBody BaseId record) {
        return baseService.deleteRecord(record);
    }
}
