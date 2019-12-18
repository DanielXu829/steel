package com.cisdi.steel.module.report.controller;

import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.base.vo.PageQuery;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cisdi.steel.module.report.service.ReportTemplateTagsService;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: 报表动态模板 - 参数列表 前端控制器 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@RestController
@RequestMapping("/reportTemplateTags")
public class ReportTemplateTagsController {

    /**
     * 构造器注入
     */
    private final ReportTemplateTagsService baseService;

    @Autowired
    public ReportTemplateTagsController(ReportTemplateTagsService baseService) {
        this.baseService = baseService;
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
    public ApiResult insertRecord(@RequestBody ReportTemplateTags record) {
        return baseService.insertRecord(record);
    }

    /**
     * 更新
     */
    @PostMapping(value = "/update")
    public ApiResult updateRecord(@RequestBody ReportTemplateTags record) {
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
