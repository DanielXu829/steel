package com.cisdi.steel.module.report.controller;

import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.base.vo.PageQuery;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cisdi.steel.module.report.service.ReportTemplateConfigService;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: 报表动态模板配置 前端控制器 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@RestController
@RequestMapping("/reportTemplateConfig")
public class ReportTemplateConfigController {

    /**
     * 构造器注入
     */
    private final ReportTemplateConfigService baseService;

    @Autowired
    public ReportTemplateConfigController(ReportTemplateConfigService baseService) {
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
    public ApiResult insertRecord(@RequestBody ReportTemplateConfig record) {
        return baseService.insertRecord(record);
    }

    /**
     * 更新
     */
    @PostMapping(value = "/update")
    public ApiResult updateRecord(@RequestBody ReportTemplateConfig record) {
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
