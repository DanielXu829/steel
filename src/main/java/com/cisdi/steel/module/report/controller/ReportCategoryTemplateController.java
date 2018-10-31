package com.cisdi.steel.module.report.controller;

import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.query.ReportCategoryTemplateQuery;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.Objects;

/**
 * <p>Description: 分类模板配置 前端控制器 </p>
 * <P>Date: 2018-10-23 </P>
 *
 * @author leaf
 * @version 1.0
 */
@RestController
@RequestMapping("/report/reportCategoryTemplate")
public class ReportCategoryTemplateController {

    /**
     * 构造器注入
     */
    private final ReportCategoryTemplateService baseService;

    @Autowired
    public ReportCategoryTemplateController(ReportCategoryTemplateService baseService) {
        this.baseService = baseService;
    }

    /**
     * 列表
     */
    @PostMapping(value = "/pageList")
    public ApiResult pageList(@RequestBody ReportCategoryTemplateQuery query) {
        return baseService.pageList(query);
    }

    /**
     * 插入
     */
    @PostMapping(value = "/insert")
    public ApiResult insertRecord(@RequestBody ReportCategoryTemplate record) {
        if (StringUtils.isBlank(record.getTemplatePath())) {
            return ApiUtil.fail("路径不能为空");
        }
        if (StringUtils.isBlank(record.getReportCategoryCode())) {
            return ApiUtil.fail("编码不能为空");
        }
        if (StringUtils.isBlank(record.getTemplateName())) {
            return ApiUtil.fail("模板名称不能为空");
        }
        return baseService.insertRecord(record);
    }


    /**
     * 更新
     */
    @PostMapping(value = "/update")
    public ApiResult updateRecord(@RequestBody ReportCategoryTemplate record) {
        if (Objects.isNull(record.getId())) {
            return ApiUtil.fail("没有ID值");
        }
        return baseService.updateRecord(record);
    }


    /**
     * 删除
     */
    @PostMapping(value = "/delete")
    public ApiResult deleteById(@RequestBody BaseId baseId) {
        return baseService.deleteRecord(baseId);
    }

}
