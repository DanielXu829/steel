package com.cisdi.steel.module.report.controller;

import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.base.vo.PageQuery;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cisdi.steel.module.report.service.ReportCategoryService;
import com.cisdi.steel.module.report.entity.ReportCategory;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: 报表分类 前端控制器 </p>
 * <P>Date: 2018-10-23 </P>
 *
 * @author leaf
 * @version 1.0
 */
@RestController
@RequestMapping("/reportCategory")
public class ReportCategoryController {

    /**
     * 构造器注入
     */
    private final ReportCategoryService baseService;

    @Autowired
    public ReportCategoryController(ReportCategoryService baseService) {
        this.baseService = baseService;
    }

    @PostMapping(value = "/allCategory")
    public ApiResult selectAllCategory(@RequestBody ReportCategory record) {
        return baseService.selectAllCategory(record);
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
    public ApiResult insertRecord(@RequestBody ReportCategory record, String sequence) {
        return baseService.insertRecord(record, sequence);
    }

    /**
     * 更新
     */
    @PostMapping(value = "/update")
    public ApiResult updateRecord(@RequestBody ReportCategory record) {
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

}
