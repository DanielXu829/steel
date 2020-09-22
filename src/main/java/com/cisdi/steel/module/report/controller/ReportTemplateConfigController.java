package com.cisdi.steel.module.report.controller;

import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.base.vo.PageQuery;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.module.report.dto.ReportTemplateConfigDTO;
import com.cisdi.steel.module.report.service.ReportTemplateConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>Description: 报表动态模板配置 前端控制器 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@RestController
@RequestMapping("/reportTemplateConfig")
@Slf4j
public class ReportTemplateConfigController {

    /**
     * 构造器注入
     */
    private final ReportTemplateConfigService reportTemplateConfigService;

    @Autowired
    public ReportTemplateConfigController(ReportTemplateConfigService reportTemplateConfigService) {
        this.reportTemplateConfigService = reportTemplateConfigService;
    }
    /**
     * 列表
     */
    @PostMapping(value = "/pageList")
    public ApiResult pageList(@RequestBody PageQuery query) {
        return reportTemplateConfigService.pageList(query);
    }

    /**
     * 添加或更新 报表模板配置
     * @param configDTO
     * @return
     */
    @PostMapping(value = "/saveOrUpdateDTO")
    public ApiResult saveOrUpdateDTO(@Valid @RequestBody ReportTemplateConfigDTO configDTO) {
        reportTemplateConfigService.saveOrUpdateDTO(configDTO);
        return ApiUtil.success("保存报表模板成功", configDTO);
    }

    /**
     * 在线预览模板文件
     * @param configDTO
     * @return
     */
    @PostMapping(value = "/viewTemplateFile")
    public ApiResult viewTemplateFile(@Valid @RequestBody ReportTemplateConfigDTO configDTO) {
        try {
            String templateFilePath = reportTemplateConfigService.generateTemporaryFile(configDTO);
            return ApiUtil.success("生成临时模板文件成功", templateFilePath);
        } catch (Exception e) {
            log.error("生成临时模板文件失败", e);
            return ApiUtil.fail("生成临时模板文件失败");
        }
    }

    /**
     * 生成excel预览图片
     * @param configDTO
     * @return
     */
    @PostMapping(value = "/getExcelImage")
    public ApiResult getExcelImage(@Valid @RequestBody ReportTemplateConfigDTO configDTO) {
        String code = reportTemplateConfigService.generateExcelImage(configDTO);
        if (code == null) {
            return ApiUtil.fail("获取excel图片失败");
        }
        return ApiUtil.success("获取excel图片成功", code);
    }

    /**
     * 查询报表模板配置DTO
     */
    @PostMapping(value = "/getDTOById")
    public ApiResult getRecord(@RequestBody BaseId baseId) {
        try {
            ReportTemplateConfigDTO configDTO = reportTemplateConfigService.getDTOById(baseId.getId());
            return ApiUtil.success("获取报表模板成功", configDTO);
        } catch (Exception e) {
            log.error("获取报表模板失败", e);
        }
        return ApiUtil.fail("获取报表模板失败");
    }

    /**
     * 删除
     */
    @PostMapping(value = "/delete")
    public ApiResult deleteById(@RequestBody BaseId baseId) {
        return reportTemplateConfigService.deleteRecord(baseId);
    }

}
