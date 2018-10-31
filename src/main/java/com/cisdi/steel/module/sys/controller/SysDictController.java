package com.cisdi.steel.module.sys.controller;

import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.sys.entity.SysDict;
import com.cisdi.steel.module.sys.query.SysDictQuery;
import com.cisdi.steel.module.sys.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: 系统字典 前端控制器 </p>
 * <P>Date: 2018-08-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@RestController
@RequestMapping("/sys/sysDict")
public class SysDictController {

    /**
     * 构造器注入
     */
    private final SysDictService baseService;

    @Autowired
    public SysDictController(SysDictService baseService) {
        this.baseService = baseService;
    }


    /**
     * 获取指定的options
     */
    @PostMapping(value = "/optionsChildren")
    public ApiResult getOptions(@RequestBody SysDictQuery query) {
        if (StringUtils.isNotBlank(query.getCode())) {
            return baseService.getOptions(query.getCode());
        }
        return ApiUtil.fail("参数不能为空");
    }


    /**
     * 分页搜索
     */
    @PostMapping(value = "/pageList")
    public ApiResult pageList(@RequestBody SysDictQuery sysDictQuery) {
        return baseService.pageList(sysDictQuery);
    }

    /**
     * 添加
     */
    @PostMapping(value = "/insert")
    public ApiResult insertRecord(@RequestBody SysDict record) {
        return baseService.insertRecord(record);
    }

    /**
     * 更新
     */
    @PostMapping(value = "/update")
    public ApiResult updateRecord(@RequestBody SysDict record) {
        return baseService.updateRecord(record);
    }

    /**
     * 查询
     */
    @PostMapping(value = "/query")
    public ApiResult queryRecord(@RequestBody BaseId id) {
        return baseService.getById(id.getId());
    }

    /**
     * 删除
     */
    @PostMapping(value = "/delete")
    public ApiResult deleteById(@RequestBody BaseId id) {
        return baseService.deleteRecord(id);
    }

    /**
     * 父类列表-选项框
     */
    @PostMapping(value = "/treeList")
    public ApiResult selectTreeList() {
        return ApiUtil.success(baseService.selectTreeList());
    }

}
