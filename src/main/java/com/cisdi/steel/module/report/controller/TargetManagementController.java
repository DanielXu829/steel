package com.cisdi.steel.module.report.controller;

import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.base.vo.PageQuery;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.service.TargetManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: tag点别名 前端控制器 </p>
 * <P>Date: 2019-11-14 </P>
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/targetmanagement")
public class TargetManagementController {

    /**
     * 构造器注入
     */
    private final TargetManagementService baseService;

    @Autowired
    public TargetManagementController(TargetManagementService baseService) {
        this.baseService = baseService;
    }

    @PostMapping(value = "/allTarget")
    public ApiResult selectAllTargetManagement(@RequestBody TargetManagement record) {
        return baseService.selectAllTargetManagement(record);
    }

    /**
     * 列表
     */
    @PostMapping(value = "/pageList")
    public ApiResult pageList(@RequestBody PageQuery query) {
        return baseService.pageList(query);
    }

    /**
     * 插入
     */
    @PostMapping(value = "/insert")
    public ApiResult insertRecord(@RequestBody TargetManagement record) {
        return baseService.insertRecord(record);
    }

    /**
     * 更新
     */
    @PostMapping(value = "/update")
    public ApiResult updateRecord(@RequestBody TargetManagement record) {
        return baseService.updateRecord(record);
    }

    /**
     * 查询
     */
    @PostMapping(value = "/get")
    public ApiResult getRecord(@RequestBody BaseId baseId) {
        return baseService.getById(baseId.getId());
    }

    /**
     * 删除
     */
    @PostMapping(value = "/delete")
    public ApiResult deleteById(@RequestBody BaseId baseId) {
        return baseService.deleteRecord(baseId);
    }

    /**
     * 递归删除当前节点下的所有节点
     */
    @PostMapping(value = "/deleteCurrentTarget")
    public ApiResult deleteCurrentTarget(@RequestBody TargetManagement record) {
        return baseService.deleteCurrentTarget(record);
    }

    @GetMapping(value = "/selectTargetManagementByCondition")
    public ApiResult selectTargetManagementByCondition(String condition) {
        return baseService.selectTargetManagementByCondition(condition);
    };
}
